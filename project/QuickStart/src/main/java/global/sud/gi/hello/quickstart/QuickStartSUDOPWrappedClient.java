package global.sud.gi.hello.quickstart;

import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.AppUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.SUDGIWrapper.custom.image.ImageManager;
import global.sud.gi.SUDGIWrapper.custom.ui.GameUiManager;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.PreviewImageView;
import global.sud.gi.SUDGIWrapper.custom.video.VideoContainer;
import global.sud.op.runtime.client.SUDOPCommonStateHandle;
import global.sud.op.runtime.client.model.MenuButtonBoundingClientRect;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.listener.SUDOPWrappedClient;
import global.sud.op.runtime.core.model.SUDOPSetKeepScreenOnParams;
import global.sud.op.runtime.core.model.SUDOPSetScreenBrightnessParams;
import global.sud.op.runtime.core.model.SUDOPShowActionSheetParams;
import global.sud.op.runtime.core.model.SUDOPShowLoadingParams;
import global.sud.op.runtime.core.model.SUDOPShowModalParams;
import global.sud.op.runtime.core.model.SUDOPShowToastParams;
import global.sud.op.runtime.core.video.SUDOPVideo;
import global.sud.op.runtime.core.wrapped.SUDOPChooseImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPOnGetUserProfileParams;
import global.sud.op.runtime.core.wrapped.SUDOPPreviewImageParams;
import global.sud.op.runtime.core.wrapped.SUDOPRequestPaymentParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageTempParams;
import global.sud.op.runtime.core.wrapped.SUDOPSaveImageToPhotosAlbumParams;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuickStartSUDOPWrappedClient implements SUDOPWrappedClient {

    private final BaseGameViewModel gameViewModel;
    private final FragmentActivity activity;
    private final FrameLayout gameContainer;
    private final ConstraintLayout widgetContainer;

    private GameUiManager gameUiManager;
    private ImageManager imageManager;

    public QuickStartSUDOPWrappedClient(BaseGameViewModel gameViewModel, FragmentActivity activity, FrameLayout gameContainer, ConstraintLayout widgetContainer) {
        this.gameViewModel = gameViewModel;
        this.activity = activity;
        this.gameContainer = gameContainer;
        this.widgetContainer = widgetContainer;
        initData();
    }

    private void initData() {
        gameUiManager = new GameUiManager(activity, widgetContainer);
        imageManager = new ImageManager(activity);
    }

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
        // TODO: 2026/7/16 游戏向app请求当前用户的昵称、头像
        // TODO: 2026/7/16 The game requests the current user's nickname and profile picture from the app.
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
        // doc：https://developer.sud.tech/openpaas/app/guide/user-info/get-user-profile.html
        // 游戏向app获取当前用户的敏感信息，可参考AppGameViewModel，客户端的实现
        // The game app requests sensitive information of the current user.


        OkHttpClient client = new OkHttpClient();
        String req;
        try {
            JSONObject reqJsonObj = new JSONObject();
            reqJsonObj.put("app_id", gameViewModel.getAppId());
            reqJsonObj.put("user_id", gameViewModel.getUserId());
            reqJsonObj.put("encrypted_data", params.encryptedData);
            req = reqJsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            req = "";
        }

        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), req);
        Request request = new Request.Builder()
                .url("https://hello-op.sud.ltd/v1/app/get/user/profile")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handle.failure(-1, "error:" + e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String dataJson = Objects.requireNonNull(response.body()).string();
                    JSONObject jsonObject = new JSONObject(dataJson);
                    int ret_code = jsonObject.getInt("ret_code");
                    String ret_msg = jsonObject.getString("ret_msg");
                    if (ret_code == 0) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        String user_profile_data = dataObject.getString("user_profile_data");
                        handle.success(user_profile_data);
                    } else {
                        handle.failure(ret_code, ret_msg);
                    }
                } catch (Exception e) {
                    handle.failure(-1, "error:" + e);
                }
            }
        });
    }

    @Override
    public void requestPayment(SUDOPStateHandle handle, SUDOPRequestPaymentParams params) {
        // 游戏请求支付，可参考：global.sud.gi.hello.game.pay.PayManager.java
        // The game requests' payment. Please refer to:
    }

    @Override
    public void saveImageTemp(SUDOPStateHandle handle, SUDOPSaveImageTempParams params) {
        imageManager.saveImageTemp(handle, params);
    }

    @Override
    public String saveImageTempSync(SUDOPSaveImageTempParams params) {
        return imageManager.saveImageTempSync(params);
    }

    @Override
    public void saveImageToPhotosAlbum(SUDOPStateHandle handle, SUDOPSaveImageToPhotosAlbumParams params) {
        imageManager.saveImageToPhotosAlbum(handle, params);
    }

    @Override
    public void chooseImage(SUDOPStateHandle handle, SUDOPChooseImageParams params) {
        imageManager.chooseImage(handle, params);
    }

    @Override
    public void previewImage(SUDOPStateHandle handle, SUDOPPreviewImageParams params) {
        handle.success(null);
        if (params.urls == null || params.urls.length == 0) {
            return;
        }
        PreviewImageView previewImageView = new PreviewImageView(activity);
        widgetContainer.addView(previewImageView, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        previewImageView.setImages(params.urls, params.current);
        previewImageView.setOnImageClickListener((position, url) -> {
            widgetContainer.removeView(previewImageView);
        });
    }

    @Override
    public void createVideo(SUDOPVideo params) {
        VideoContainer videoContainer = new VideoContainer(activity, gameContainer);
        videoContainer.initVideo(params);
    }

    @Override
    public void showLoading(SUDOPStateHandle handle, SUDOPShowLoadingParams params) {
        gameUiManager.showLoading(handle, params);
    }

    @Override
    public void hideLoading(SUDOPStateHandle handle) {
        gameUiManager.hideLoading(handle);
    }

    @Override
    public void showToast(SUDOPCommonStateHandle handle, SUDOPShowToastParams params) {
        gameUiManager.showToast(handle, params);
    }

    @Override
    public void hideToast(SUDOPCommonStateHandle handle) {
        gameUiManager.hideToast(handle);
    }

    @Override
    public void showActionSheet(SUDOPCommonStateHandle handle, SUDOPShowActionSheetParams params) {
        gameUiManager.showActionSheet(handle, params);
    }

    @Override
    public void showModal(SUDOPCommonStateHandle handle, SUDOPShowModalParams params) {
        gameUiManager.showModal(handle, params);
    }

    @Override
    public void setScreenBrightness(SUDOPCommonStateHandle handle, SUDOPSetScreenBrightnessParams params) {
        gameUiManager.setScreenBrightness(handle, params);
    }

    @Override
    public void getScreenBrightness(SUDOPCommonStateHandle handle) {
        gameUiManager.getScreenBrightness(handle);
    }

    @Override
    public void setKeepScreenOn(SUDOPCommonStateHandle handle, SUDOPSetKeepScreenOnParams params) {
        gameUiManager.setKeepScreenOn(handle, params);
    }

    @Override
    public void getAppBaseInfo(JSONObject jsonObject) {
        try {
            jsonObject.put("version", AppUtils.getAppVersionName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public MenuButtonBoundingClientRect getMenuButtonBoundingClientRect() {
        // TODO: 2026/7/16 右上角控件的尺寸，可参考：AppSUDOPWrappedClient.java
        // TODO: 2026/7/16 The size of the control in the upper right corner can be referred to as follows：AppSUDOPWrappedClient.java
        return null;
    }

}
