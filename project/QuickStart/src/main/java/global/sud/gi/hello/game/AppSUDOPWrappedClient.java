package global.sud.gi.hello.game;

import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.AppUtils;

import org.json.JSONObject;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.SUDGIWrapper.custom.image.ImageManager;
import global.sud.gi.SUDGIWrapper.custom.ui.GameUiManager;
import global.sud.gi.SUDGIWrapper.custom.ui.widget.view.PreviewImageView;
import global.sud.gi.SUDGIWrapper.custom.video.VideoContainer;
import global.sud.gi.hello.app.common.http.param.BaseResponse;
import global.sud.gi.hello.app.common.http.rx.RxCallback;
import global.sud.gi.hello.app.service.MainRepository;
import global.sud.gi.hello.app.service.resp.GetUserProfileResp;
import global.sud.gi.hello.game.ad.AdManager;
import global.sud.gi.hello.game.pay.PayManager;
import global.sud.op.runtime.client.SUDOPCommonStateHandle;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.ad.SUDOPBannerAd;
import global.sud.op.runtime.core.ad.SUDOPCustomAd;
import global.sud.op.runtime.core.ad.SUDOPGameBannerAd;
import global.sud.op.runtime.core.ad.SUDOPGameDrawerAd;
import global.sud.op.runtime.core.ad.SUDOPGamePortalAd;
import global.sud.op.runtime.core.ad.SUDOPInterstitialAd;
import global.sud.op.runtime.core.ad.SUDOPRewardedAd;
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

public abstract class AppSUDOPWrappedClient implements SUDOPWrappedClient {

    private final BaseGameViewModel gameViewModel;
    private final FragmentActivity activity;
    private final FrameLayout gameContainer;
    private final ConstraintLayout widgetContainer;

    private AdManager adManager;
    private GameUiManager gameUiManager;
    private ImageManager imageManager;
    private PayManager payManager;

    public AppSUDOPWrappedClient(BaseGameViewModel gameViewModel, FragmentActivity activity, FrameLayout gameContainer, ConstraintLayout widgetContainer) {
        this.gameViewModel = gameViewModel;
        this.activity = activity;
        this.gameContainer = gameContainer;
        this.widgetContainer = widgetContainer;
        initData();
    }

    private void initData() {
        adManager = new AdManager(activity, widgetContainer);
        gameUiManager = new GameUiManager(activity, widgetContainer);
        imageManager = new ImageManager(activity);
        payManager = new PayManager(activity, gameViewModel);
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
        MainRepository.getUserProfile(activity, gameViewModel.getAppId(), gameViewModel.getUserId(), encrypted_data, new RxCallback<GetUserProfileResp>() {
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
        payManager.requestPayment(handle, params);
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
    public void createBannerAd(SUDOPBannerAd bannerAd) {
        adManager.createBannerAd(bannerAd);
    }

    @Override
    public void createCustomAd(SUDOPCustomAd customAd) {
        adManager.createCustomAd(customAd);
    }

    @Override
    public void createInterstitialAd(SUDOPInterstitialAd interstitialAd) {
        adManager.createInterstitialAd(interstitialAd);
    }

    @Override
    public void createGameBannerAd(SUDOPGameBannerAd gameBannerAd) {
        adManager.createGameBannerAd(gameBannerAd);
    }

    @Override
    public void createGamePortalAd(SUDOPGamePortalAd gamePortalAd) {
        adManager.createGamePortalAd(gamePortalAd);
    }

    @Override
    public void createGameDrawerAd(SUDOPGameDrawerAd gameDrawerAd) {
        adManager.createGameDrawerAd(gameDrawerAd);
    }

    @Override
    public void createRewardedVideoAd(SUDOPRewardedAd rewardedAd) {
        adManager.createRewardedVideoAd(rewardedAd);
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

}
