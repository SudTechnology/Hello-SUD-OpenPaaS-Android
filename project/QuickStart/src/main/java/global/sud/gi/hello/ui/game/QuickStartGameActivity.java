package global.sud.gi.hello.ui.game;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import global.sud.gi.SUDGIWrapper.viewmodel.BaseGameViewModel;
import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseActivity;
import global.sud.gi.hello.common.http.param.BaseResponse;
import global.sud.gi.hello.common.http.param.RetCode;
import global.sud.gi.hello.common.http.rx.RxCallback;
import global.sud.gi.hello.common.utils.DensityUtils;
import global.sud.gi.hello.common.utils.ViewUtils;
import global.sud.gi.hello.service.MainRepository;
import global.sud.gi.hello.service.resp.CreateOrderResp;
import global.sud.gi.hello.service.resp.GetUserProfileResp;
import global.sud.gi.hello.service.resp.PaymentResp;
import global.sud.gi.hello.service.resp.QueryOrderResp;
import global.sud.gi.hello.ui.game.mgr.AdManager;
import global.sud.gi.hello.ui.game.model.ChooseImageResult;
import global.sud.gi.hello.ui.game.page.PreviewImageActivity;
import global.sud.gi.hello.ui.game.utils.ImageCompressUtil;
import global.sud.gi.hello.ui.game.utils.IntentUtils;
import global.sud.gi.hello.ui.game.utils.QgClientUtils;
import global.sud.gi.hello.ui.game.utils.UriCopyUtil;
import global.sud.gi.hello.ui.game.widget.dialog.ChooseImageDialog;
import global.sud.gi.hello.ui.game.widget.dialog.GameInformationDialog;
import global.sud.gi.hello.ui.game.widget.dialog.GameRoomMoreDialog;
import global.sud.gi.hello.ui.game.widget.dialog.PaymentDialog;
import global.sud.gi.hello.ui.game.widget.view.VideoContainer;
import global.sud.gi.hello.ui.main.model.GameModel;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.ad.SUDOPBannerAd;
import global.sud.op.runtime.core.listener.SUDOPGetGameInformationListener;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPGameInformationModel;
import global.sud.op.runtime.core.model.SUDOPGamePathType;
import global.sud.op.runtime.core.wrapped.SUDOPChooseImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPOnGetUserProfileParams;
import global.sud.op.runtime.core.wrapped.SUDOPPreviewImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPRequestPaymentParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageTempParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageToPhotosAlbumParams;
import global.sud.op.runtime.core.wrapped.SUDOPVideoParams;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.internal.CustomAdapt;

/**
 * 游戏页面
 * Game page
 */
public class QuickStartGameActivity extends BaseActivity implements CustomAdapt {

    private String gameId;
    private String gameUrl;
    private String gamePkgVersion;
    private SUDOPGamePathType pathType;
    private String manifestJson;
    private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel();
    private LifecycleOwner lifecycleOwner = this;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private FrameLayout gameContainer;
    private boolean chooseImageIsOriginal;
    private SUDOPStateHandle chooseImageStateHandle;
    private AdManager adManager;

    // 拍照
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private Uri imageUri;
    private String userId;

    private SUDOPGameInformationModel gameInformationModel;
    private boolean isRequestingGameInfo;

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model) {
        start(context, model, null);
    }

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model, String userId) {
        Intent intent = new Intent(context, QuickStartGameActivity.class);
        intent.putExtra("GameModel", model);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        GameModel model = (GameModel) getIntent().getSerializableExtra("GameModel");
        if (model == null) {
            return true;
        }
        userId = getIntent().getStringExtra("userId");
        gameId = model.gameId;
        gameUrl = model.gameUrl;
        gamePkgVersion = model.gamePkgVersion;
        pathType = model.pathType;
        manifestJson = model.manifestJson;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameContainer = findViewById(R.id.game_container); // 获取游戏View容器 English: Retrieve the game view container.

        adManager = new AdManager(this, findViewById(R.id.container_ad));
        ViewUtils.addMarginTop(findViewById(R.id.container_more), ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        if (!TextUtils.isEmpty(userId)) {
            gameViewModel.userId = userId;
        }
        gameViewModel.sudOPWrappedClient = sudOPWrappedClient;
        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        // Call this method to load the corresponding game. Developers can decide when to load the game based on their business logic.
        gameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion, pathType, manifestJson);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除 English: When closing the game, remove the game view.
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内 English: Add the game view to the container.
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        gameViewModel.progressLiveData.observe(this, new Observer<BaseGameViewModel.ProgressModel>() {
            @Override
            public void onChanged(BaseGameViewModel.ProgressModel model) {
                // Game loading progress changes
            }
        });

        gameViewModel.queryExitLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                finish();
            }
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            processChooseImageUri(uri);
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (chooseImageStateHandle == null) {
                return;
            }
            if (success && imageUri != null) {
                processChooseImageUri(imageUri);
                imageUri = null;
            } else {
                chooseImageStateHandle.failure(RetCode.Fail, "user cancel");
                chooseImageStateHandle = null;
            }
        });
        findViewById(R.id.iv_close).setOnClickListener(v -> finish());
        findViewById(R.id.iv_more).setOnClickListener(this::onClickMore);
    }

    private void onClickMore(View view) {
        if (gameInformationModel != null) {
            showGameInformationDialog(gameInformationModel);
            return;
        }
        if (isRequestingGameInfo) {
            return;
        }
        isRequestingGameInfo = true;
        SUDOP.getGameInformation(gameId, new SUDOPGetGameInformationListener() {
            @Override
            public void onSuccess(SUDOPGameInformationModel model) {
                gameInformationModel = model;
                showGameInformationDialog(model);
                isRequestingGameInfo = false;
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showShort("error retCode:" + retCode + " retMsg:" + retMsg);
                isRequestingGameInfo = false;
            }
        });
    }

    private void showGameInformationDialog(SUDOPGameInformationModel model) {
        if (model == null) {
            return;
        }
        GameInformationDialog dialog = GameInformationDialog.newInstance(model);
        dialog.setOnClickInformationListener(new GameInformationDialog.OnClickInformationListener() {
            @Override
            public void onClickRestart() {
                restartGame();
            }

            @Override
            public void onClickInformation() {
                GameInformationActivity.start(context, model);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void restartGame() {
        gameViewModel.destroyGame();
        gameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion, pathType, manifestJson);
    }

    private void processChooseImageUri(Uri uri) {
        if (chooseImageStateHandle == null) {
            return;
        }
        if (uri == null) {
            chooseImageStateHandle.failure(RetCode.Fail, "user cancel");
            chooseImageStateHandle = null;
            return;
        }
        // 这里拿到图片 URI
        executorService.execute(() -> {
            try {
                File outDir = getTempDir();
                File result;
                if (chooseImageIsOriginal) {
                    result = UriCopyUtil.copyUriToFile(this, uri, outDir);
                } else { // 压缩
                    result = ImageCompressUtil.compressUriToFile(this, uri, outDir,
                            1080,   // 目标最长边
                            80      // JPEG质量(0~100)
                    );
                }
                ChooseImageResult chooseImageResult = new ChooseImageResult();
                chooseImageResult.addFile(result);
                chooseImageStateHandle.success(GsonUtils.toJson(chooseImageResult));
            } catch (Exception e) {
                e.printStackTrace();
                chooseImageStateHandle.failure(RetCode.Fail, "compress fail:" + e);
            }
            chooseImageStateHandle = null;
        });
    }

    private void showMoreDialog() {
        GameRoomMoreDialog dialog = new GameRoomMoreDialog();
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                gameViewModel.destroyGame();
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void updateStatusBar() {
        // 这个沉浸式状态栏的使用是APP的业务，对于游戏而言不是必须的
        // The use of the immersive status bar is part of the app's functionality and is not essential for games.
        if (!TextUtils.isEmpty(gameId)) { // 玩着游戏 English: Playing the game.
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
        // 注意：要在此处调用onResume()方法
        // Note: Call the onResume() method here.
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注意：要在此处调用onPause()方法
        // Note: Call the onPause() method here.
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    @Override
    public void onBackPressed() {
        // 注意：需要保证页面销毁之前，先调用游戏的销毁方法
        // 如果有其他地方调用finish()，那么也要在finish()之前，先调用游戏的销毁方法

        // Note: Ensure that the game's destruction method is called before the page is destroyed.
        // If finish() is called elsewhere, make sure to call the game's destruction method before finish().

        gameViewModel.destroyGame();

        super.onBackPressed();
    }

    @Override
    public void finish() {
        gameViewModel.destroyGame();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.destroyGame();
    }

    private SUDOPWrappedClient sudOPWrappedClient = new SUDOPWrappedClient() {
        @Override
        public void onGetLegacyUserIdentity(SUDOPStateHandle handle) {
            String userId = gameViewModel.getUserId();
            JSONObject obj = new JSONObject();
            try {
                obj.put("legacy_user_identity", userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handle.success(obj.toString());
        }

        @Override
        public void onGetUserInfo(SUDOPStateHandle handle) {
            String userId = gameViewModel.getUserId();
            JSONObject obj = new JSONObject();
            try {
                obj.put("nickname", "this is nickname");
                obj.put("avatar", "this is avatar");
            } catch (Exception e) {
                e.printStackTrace();
            }
            handle.success(obj.toString());
        }

        @Override
        public void onGetUserProfile(SUDOPStateHandle handle, SUDOPOnGetUserProfileParams params) {
            String encrypted_data = params.encryptedData;
            MainRepository.getUserProfile(lifecycleOwner, gameViewModel.getAppId(), gameViewModel.getUserId(), encrypted_data, new RxCallback<GetUserProfileResp>() {
                @Override
                public void onNext(BaseResponse<GetUserProfileResp> resp) {
                    super.onNext(resp);
                    if (resp.getRet_code() == 0) {
                        GetUserProfileResp data = resp.getData();
                        if (data == null || TextUtils.isEmpty(data.user_profile_data)) {
                            handle.failure(-1, "The server returned an empty user profile.");
                        } else {
                            handle.success(data.user_profile_data);
                        }
                    } else {
                        handle.failure(resp.getRet_code(), resp.getRet_msg());
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    handle.failure(-1, "error:" + e);
                }
            });
        }

        @Override
        public void requestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
            onRequestPayment(handle, params);
        }

        @Override
        public void saveImageTemp(SUDOPStateHandle handle, SUDOPSaveImageTempParams params) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String filePath = getTempFile(params.fileType);
                    try {
                        if ("jpg".equals(params.fileType)) {
                            QgClientUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
                        } else if ("png".equals(params.fileType)) {
                            QgClientUtils.rgbaToPng(params.data, params.width, params.height, filePath);
                        } else {
                            handle.failure(-1, "This file type is not supported：" + params.fileType);
                            return;
                        }
                    } catch (IOException e) {
                        handle.failure(-1, "error:" + e);
                        return;
                    }
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("tempFilePath", filePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String result = obj.toString();
                    handle.success(result);
                }
            }.start();
        }

        @Override
        public String saveImageTempSync(SUDOPSaveImageTempParams params) {
            String filePath = getTempFile(params.fileType);
            try {
                if ("jpg".equals(params.fileType)) {
                    QgClientUtils.rgbaToJpeg(params.data, params.width, params.height, filePath);
                } else if ("png".equals(params.fileType)) {
                    QgClientUtils.rgbaToPng(params.data, params.width, params.height, filePath);
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
            return filePath;
        }

        @Override
        public void saveImageToPhotosAlbum(SUDOPStateHandle handle, SUDOPSaveImageToPhotosAlbumParams params) {
            File file;
            if (params.filePath == null || !(file = new File(params.filePath)).exists()) {
                handle.failure(-1, "The filePath does not exist.");
                return;
            }
            boolean result = false;
            try {
                result = saveImageToGallery(context, file);
            } catch (Exception e) {
                handle.failure(-1, "saveImageToPhotsAlbum error:" + e);
                return;
            }
            if (result) {
                handle.success(null);
            } else {
                handle.failure(-1, "saveImageToPhotsAlbum fail");
            }
        }

        @Override
        public void chooseImage(SUDOPStateHandle handle, SUDOPChooseImageParams params) {
            int count = params.count;
            String[] sizeType = params.sizeType;
            String[] sourceType = params.sourceType;
            boolean isOriginal = true;
            if (sizeType != null) {
                for (String type : sizeType) {
                    if ("compressed".equals(type)) {
                        isOriginal = false;
                    }
                }
            }
            boolean isAlbum = false;
            boolean isCamera = false;
            if (sourceType != null) {
                for (String type : sourceType) {
                    if ("album".equals(type)) {
                        isAlbum = true;
                    } else if ("camera".equals(type)) {
                        isCamera = true;
                    }
                }
            }
            if (isAlbum && isCamera) {
                showChooseDialog(count, isOriginal, handle);
            } else if (isCamera) {
                selectCamera(count, isOriginal, handle);
            } else if (isAlbum) {
                selectAlbum(count, isOriginal, handle);
            } else {
                handle.failure(-1, "sourceType fail to identify");
            }
        }

        @Override
        public void previewImage(SUDOPStateHandle handle, SUDOPPreviewImageParams params) {
            handle.success(null);
            PreviewImageActivity.start(context, params);
        }

        @Override
        public void createVideo(SUDOPVideoParams params) {
            processCreateVideo(params);
        }

        @Override
        public void createBannerAd(SUDOPBannerAd bannerAd) {
            adManager.createBannerAd(bannerAd);
        }
    };

    private void processCreateVideo(SUDOPVideoParams params) {
        VideoContainer videoContainer = new VideoContainer(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(params.width, params.height);
        layoutParams.topMargin = params.y;
        layoutParams.setMarginStart(params.x);
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        videoContainer.initVideo(params);
        gameContainer.addView(videoContainer, layoutParams);
    }

    @NonNull
    private String getTempFile(String suffixName) {
        return new File(getTempDir(), UUID.randomUUID() + "." + suffixName).getAbsolutePath();
    }

    @NonNull
    private File getTempDir() {
        File dir = getExternalCacheDir();
        if (dir == null) {
            dir = getCacheDir();
        }
        File hellosud = new File(dir, "hellosud");
        if (!hellosud.exists()) {
            hellosud.mkdir();
        }
        return hellosud;
    }

    public static boolean saveImageToGallery(Context context, File file) throws Exception {
        try {
            // Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES);
                ContentResolver resolver = context.getContentResolver();
                Uri uri = resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );
                if (uri == null) {
                    return false;
                }
                try (OutputStream out = resolver.openOutputStream(uri);
                     FileInputStream in = new FileInputStream(file)) {
                    if (out == null) {
                        return false;
                    }
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                    return true;
                }
            } else {
                // Android 9及以下
                File picturesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                );
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs();
                }
                File destFile = new File(picturesDir, file.getName());
                try (FileInputStream in = new FileInputStream(file);
                     FileOutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    out.flush();
                }
                // 通知图库刷新
                MediaScannerConnection.scanFile(context, new String[]{destFile.getAbsolutePath()}, new String[]{"image/jpeg"}, null);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void onRequestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        MainRepository.createOrder(lifecycleOwner, gameViewModel.getAppId(), gameViewModel.getUserId(), params.signData, params.signature, new RxCallback<CreateOrderResp>() {
            @Override
            public void onNext(BaseResponse<CreateOrderResp> t) {
                super.onNext(t);
                if (t.getRet_code() == 0) {
                    if (t.getData() != null && t.getData().is_valid) {
                        showPaymentDialog(handle, params);
                    } else {
                        handle.failure(-1, "validate payment fail");
                    }
                } else {
                    handle.failure(t.getRet_code(), t.getRet_msg());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                handle.failure(-1, "validate payment net fail:" + e);
            }
        });

    }

    private void showPaymentDialog(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        PaymentDialog dialog = new PaymentDialog(params);
        dialog.setPaymentListener(new PaymentDialog.PaymentListener() {
            @Override
            public void paymentOperate(boolean isPayment) {
                if (isPayment) {
                    MainRepository.payment(context, params.sudTradeNo, new RxCallback<PaymentResp>() {
                        @Override
                        public void onNext(BaseResponse<PaymentResp> t) {
                            super.onNext(t);
                            String payUrl = t.getData() == null ? null : t.getData().pay_url;
                            if (t.getRet_code() == 0 && !TextUtils.isEmpty(payUrl)) {
                                IntentUtils.openUrl(context, payUrl);
                            } else {
                                handle.failure(-1, "app server fail,code:" + t.getRet_code() + " pay_url:" + payUrl);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            handle.failure(-1, "payment net fail:" + e);
                        }
                    });
                } else {
                    handle.failure(-1, "user cancel");
                    dialog.dismiss();
                }
            }

            @Override
            public void payemntCompleted() {
                MainRepository.queryOrder(context, params.sudTradeNo, new RxCallback<QueryOrderResp>() {
                    @Override
                    public void onNext(BaseResponse<QueryOrderResp> t) {
                        super.onNext(t);
                        int orderStatus = t.getData() == null ? 0 : t.getData().order_status;
                        if (t.getRet_code() == 0) {
                            if (orderStatus == 3) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("sudTradeNo", params.sudTradeNo);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                handle.success(jsonObject.toString());
                            } else {
                                handle.failure(-1, "payment fail,orderStatus:" + orderStatus);
                            }
                        } else {
                            handle.failure(-1, "queryOrder app server fail,code:" + t.getRet_code());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        handle.failure(-1, "user cancel");
                    }
                });
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showChooseDialog(int count, boolean isOriginal, SUDOPStateHandle handle) {
        ChooseImageDialog dialog = new ChooseImageDialog(this);
        dialog.setChooseImageListener(new ChooseImageDialog.ChooseImageListener() {
            @Override
            public void onClickCamera() {
                selectCamera(count, isOriginal, handle);
            }

            @Override
            public void onClickAlbum() {
                selectAlbum(count, isOriginal, handle);
            }

            @Override
            public void onClickCancel() {
                handle.failure(RetCode.Fail, "user cancel chooseImage");
            }
        });
        dialog.show();
    }

    private void selectCamera(int count, boolean isOriginal, SUDOPStateHandle handle) {
        boolean granted = PermissionUtils.isGranted(Manifest.permission.CAMERA);
        if (!granted) {
            PermissionUtils permissionUtils = PermissionUtils.permission(Manifest.permission.CAMERA);
            permissionUtils.callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    runOnUiThread(() -> {
                        selectCamera(count, isOriginal, handle);
                    });
                }

                @Override
                public void onDenied() {
                    handle.failure(-1, "No permission to take photos.");
                }
            });
            permissionUtils.request();
            return;
        }
        String tempFilePath = getTempFile("jpg");
        File imageFile = new File(tempFilePath);
        imageUri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".fileprovider",
                imageFile
        );
        this.chooseImageIsOriginal = isOriginal;
        this.chooseImageStateHandle = handle;
        takePictureLauncher.launch(imageUri);
    }

    private void selectAlbum(int count, boolean isOriginal, SUDOPStateHandle handle) {
        this.chooseImageIsOriginal = isOriginal;
        this.chooseImageStateHandle = handle;
        pickImageLauncher.launch("image/*");
    }

    @Override
    public boolean isBaseOnWidth() {
        Point screenSize = DensityUtils.getAppScreenSize();
        return screenSize.x < screenSize.y;
    }

    @Override
    public float getSizeInDp() {
        return 375;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AutoSize.autoConvertDensityOfCustomAdapt(this, this);
    }

}
