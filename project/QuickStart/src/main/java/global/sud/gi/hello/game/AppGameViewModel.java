package global.sud.gi.hello.game;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;
import java.util.List;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.SUDGIWrapper.custom.base.utils.QuickStartUtils;
import global.sud.gi.SUDGIWrapper.custom.client.QgExtendedClient;
import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.app.common.http.param.BaseResponse;
import global.sud.gi.hello.app.common.http.rx.RxCallback;
import global.sud.gi.hello.app.service.MainRepository;
import global.sud.gi.hello.app.service.resp.GameLoginResp;
import global.sud.op.runtime.core.SUDRTGameHandle;
import global.sud.op.runtime.core.SUDRTGamePackageManager;
import global.sud.op.runtime.core.model.SUDOPGameInfo;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class AppGameViewModel extends BaseGameViewModel {

    private static final String TAG = "QuickStartGameViewModel";
    // TODO: Sud平台申请的appId
    // TODO: The appId obtained from Sud platform application.
    public static String SUD_APP_ID = "2049108146763776002";

    // TODO: Sud平台申请的appKey
    // TODO: The appKey obtained from Sud platform application.
    public static String SUD_APP_KEY = "LMKp0m44C4jYzbAAjUChSmodNnQq2N9Q";

    // TODO: 使用的UserId。这里随机生成作演示，开发者将其修改为业务使用的唯一userId
    // TODO: Used UserId. Here it is randomly generated for demonstration purposes. Developers should modify it to the unique userId used for the business.
    public static String userId = QuickStartUtils.genUserID();

    public final MutableLiveData<View> gameViewLiveData = new MutableLiveData<>();

    /**
     * 向接入方服务器获取code
     * Retrieve the code from the partner's server.
     */
    @Override
    protected void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener) {
        // TODO: 2022/6/10 注意，这里是演示使用OkHttpClient请求hello-sud服务
        // TODO: 2022/6/10 开发者在与后端联调时需将其改成自己的网络请求方式向自己的服务器获取code
        // TODO: 2023/10/26 每次回调此方法都去自己的后端拿最新的code，不要缓存code

        // TODO: 2022/6/10 Note that this is a demonstration using OkHttpClient to request the hello-sud service.
        // TODO: 2022/6/10 Developers should modify this to their own network request method to retrieve the code from their own server during backend integration.
        // TODO: 2023/10/26 Retrieve the latest code from your own backend every time this method is called, and avoid caching the code.
        MainRepository.login(activity instanceof LifecycleOwner ? (LifecycleOwner) activity : null, getAppId(), userId, new RxCallback<GameLoginResp>() {

            @Override
            public void onNext(BaseResponse<GameLoginResp> t) {
                super.onNext(t);
                if (t.getRet_code() == 0) {
                    GameLoginResp gameLoginResp = t.getData();
                    if (gameLoginResp == null || TextUtils.isEmpty(gameLoginResp.user_signature)) {
                        listener.onFailed(-1, "user_signature cannot be empty");
                    } else {
                        listener.onSuccess(gameLoginResp.user_signature);
                    }
                } else {
                    listener.onFailed(t.getRet_code(), t.getRet_msg());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listener.onFailed(-1, "error:" + e);
            }
        });
    }

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the partner).
     */
    @Override
    public String getUserId() {
        return userId;
    }

    /**
     * 设置Sud平台申请的appId
     * Set the appId obtained from the Sud platform.
     */
    @Override
    public String getAppId() {
        return SUD_APP_ID;
    }

    /**
     * 设置Sud平台申请的appKey
     * Set the appKey obtained from the Sud platform.
     */
    @Override
    public String getAppKey() {
        return SUD_APP_KEY;
    }

    @Override
    protected void onAddGameView(View gameView) {
        gameViewLiveData.setValue(gameView);
    }

    @Override
    protected void onRemoveGameView() {
        gameViewLiveData.setValue(null);
    }

    @Override
    protected void onGameHandleCreated(SUDRTGameHandle handle, SUDOPGameInfo gameInfo) {
        handle.registerExtendedClient("qg", new QgExtendedClient());
        initListener(handle);
        setGameStartOptions(handle);
    }

    private void setGameStartOptions(SUDRTGameHandle handle) {
        Bundle bundle = new Bundle();
        if (pathType != null && pathType == SUDOPGamePathType.DIR) {
            bundle.putString(SUDRTGamePackageManager.KEY_PACKAGE_CONTENT_PATH, gameUrl);
        }
//        bundle.putBoolean(SUDRTGameHandle.KEY_GAME_DEBUG_OPTION_ENABLE_DEBUGGER, true);
        if (BuildConfig.DEBUG) {
            bundle.putBoolean(SUDRTGameHandle.KEY_GAME_DEBUG_OPTION_ENABLE_V_CONSOLE, true);
        }
        if (manifestJson != null) {
            bundle.putString(SUDRTGameHandle.KEY_GAME_START_OPTIONS_CUSTOM_CONFIG, manifestJson);
        }
        handle.setGameStartOptions(gameId, bundle);
    }

    private void initListener(SUDRTGameHandle handle) {
        handle.setGameQueryExitListener(new SUDRTGameHandle.GameQueryExitListener() {
            @Override
            public void onQueryExit(String appID, String result) {
                queryExitLiveData.setValue(true);
            }
        });
        handle.setGameLoadSubpackageListener(new SUDRTGameHandle.GameLoadSubpackageListener() {
            /**
             * 当小游戏请求加载分包时回调
             *
             * @param handle 通过此 handle<br/>把下载并安装分包的结果返回给小游戏
             * @param root   根据该值计算分包的 URL
             */
            @Override
            public void onLoadSubpackage(SUDRTGameHandle.GameLoadSubpackageHandle handle, String name, String root) {
                // 只有加载存量游戏包，才会回调到这一块，这里需要传入分包的绝对路径，路径根据自己平台的存放规则来拿
                handle.success(new File(getGameUrl(), root).getAbsolutePath());
            }
        });
        handle.setGameQueryPermissionListener(new SUDRTGameHandle.GameQueryPermissionListener() {
            @Override
            public void onQueryPermission(SUDRTGameHandle.GameQueryPermissionHandle handle, String permission, String appId) {
                String[] systemPermission = null;
                if ("scope.userLocation".equals(permission)) {
                    systemPermission = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                } else if ("scope.record".equals(permission)) {
                    systemPermission = new String[]{Manifest.permission.RECORD_AUDIO};
                } else if ("scope.camera".equals(permission)) {
                    systemPermission = new String[]{Manifest.permission.CAMERA};
                } else if ("scope.writePhotosAlbum".equals(permission)) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { // Android 10以下才需要
                        systemPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    }
                }
                if (systemPermission == null) {
                    handle.complete(permission, true);
                    return;
                }
                PermissionUtils permissionUtils = PermissionUtils.permission(systemPermission);
                permissionUtils.callback(new PermissionUtils.SingleCallback() {
                    @Override
                    public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        if (granted.isEmpty()) {
                            handle.complete(permission, false);
                        } else {
                            handle.complete(permission, true);
                        }
                    }
                });
                permissionUtils.request();
            }
        });
        handle.setGameQuerySystemPermissionListener(new SUDRTGameHandle.GameQuerySystemPermissionListener() {
            @Override
            public void beforeQuerySystemPermission(SUDRTGameHandle.GameQuerySystemPermissionHandle handle, String fromJSMethod, String systemPermission, String appId, int authStatus, boolean serviceEnabled) {
                PermissionUtils permissionUtils = PermissionUtils.permission(systemPermission);
                permissionUtils.callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        handle.continueQuerySystemPermission(systemPermission);
                    }

                    @Override
                    public void onDenied() {
                        handle.continueQuerySystemPermission(systemPermission);
                    }
                });
                permissionUtils.request();
            }
        });
    }

    @Override
    protected void logE(String msg) {
        Log.e(TAG, msg);
    }

    @Override
    protected void toastMsg(String msg) {
        ToastUtils.showShort(msg);
    }
}
