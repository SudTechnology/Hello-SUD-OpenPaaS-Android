package global.sud.gi.hello.game.ad;

import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.blankj.utilcode.util.ThreadUtils;

import java.util.HashMap;
import java.util.Map;

import global.sud.gi.SUDGIWrapper.custom.base.utils.DensityUtils;
import global.sud.gi.hello.app.common.http.param.BaseResponse;
import global.sud.gi.hello.app.common.http.param.RetCode;
import global.sud.gi.hello.app.common.http.rx.RxCallback;
import global.sud.gi.hello.app.service.MainRepository;
import global.sud.gi.hello.app.service.resp.GetMeterialResp;
import global.sud.gi.hello.app.service.resp.ReportRewardResp;
import global.sud.gi.hello.game.AppGameViewModel;
import global.sud.gi.hello.game.ad.widget.view.BannerAdView;
import global.sud.gi.hello.game.ad.widget.view.DemoInterstitialAdView;
import global.sud.gi.hello.game.ad.widget.view.GameBannerView;
import global.sud.gi.hello.game.ad.widget.view.GameDrawerAdView;
import global.sud.gi.hello.game.ad.widget.view.GamePortalAdView;
import global.sud.gi.hello.game.ad.widget.view.MiniGameNativeAdView;
import global.sud.gi.hello.game.ad.widget.view.RewardedAdView;
import global.sud.op.runtime.core.SUDOPStateHandle;
import global.sud.op.runtime.core.ad.SUDOPBannerAd;
import global.sud.op.runtime.core.ad.SUDOPCustomAd;
import global.sud.op.runtime.core.ad.SUDOPGameBannerAd;
import global.sud.op.runtime.core.ad.SUDOPGameDrawerAd;
import global.sud.op.runtime.core.ad.SUDOPGamePortalAd;
import global.sud.op.runtime.core.ad.SUDOPInterstitialAd;
import global.sud.op.runtime.core.ad.SUDOPRewardVideoAdSSVData;
import global.sud.op.runtime.core.ad.SUDOPRewardedAd;

public class AdManager {

    private ConstraintLayout container;
    private FragmentActivity activity;
    private Map<Object, View> viewMap = new HashMap<>(); // 存储每个广告id

    public AdManager(FragmentActivity activity, ConstraintLayout container) {
        this.activity = activity;
        this.container = container;
    }

    public void createBannerAd(SUDOPBannerAd ad) {
        ad.setOperateListener(new SUDOPBannerAd.OperateListener() {
            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    int left = 0;
                    int top = 0;
                    int width = 0;
                    int height = 0;
                    if (ad.style != null) {
                        left = ad.style.left;
                        top = ad.style.top;
                        width = ad.style.width;
                        height = ad.style.height;
                    }
                    view = new BannerAdView(activity);
                    view.setOnClickListener(v -> {
                        ad.notifyClick(0, "click");
                    });
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.setMarginStart(left);
                    params.topMargin = top;
                    container.addView(view, params);
                    viewMap.put(ad, view);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void hide(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                handle.success(null);
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createCustomAd(SUDOPCustomAd ad) {
        ad.setOperateListener(new SUDOPCustomAd.OperateListener() {
            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    int left = 0;
                    int top = 0;
                    int width = 0;
                    if (ad.style != null) {
                        left = ad.style.left;
                        top = ad.style.top;
                        width = ad.style.width;
                    }
                    view = new MiniGameNativeAdView(activity);
                    view.setOnClickListener(v -> {
                        ad.notifyClick(0, "click");
                    });
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, DensityUtils.dp2px(activity, 100));
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.setMarginStart(left);
                    params.topMargin = top;
                    container.addView(view, params);
                    viewMap.put(ad, view);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public boolean isShow() {
                View view = viewMap.get(ad);
                if (view != null && view.getVisibility() == View.VISIBLE) {
                    return true;
                }
                return false;
            }

            @Override
            public void hide(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                handle.success(null);
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createInterstitialAd(SUDOPInterstitialAd ad) {
        ad.setOperateListener(new SUDOPInterstitialAd.OperateListener() {
            @Override
            public void load(SUDOPStateHandle handle) {
                handle.success(null);
            }

            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    DemoInterstitialAdView adView = new DemoInterstitialAdView(activity);
                    view = adView;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                    container.addView(view, params);
                    viewMap.put(ad, view);
                    adView.setOnAdClickListener(new DemoInterstitialAdView.OnAdClickListener() {
                        @Override
                        public void onDownloadClick() {
                            ad.notifyClick(0, "click");
                        }

                        @Override
                        public void onCloseClick() {
                            ad.notifyClosed();
                            adView.setVisibility(View.GONE);
                        }
                    });
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createGameBannerAd(SUDOPGameBannerAd ad) {
        ad.setOperateListener(new SUDOPGameBannerAd.OperateListener() {
            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    int left = 0;
                    int top = 0;
                    String orientation = null;
                    if (ad.style != null) {
                        left = ad.style.left;
                        top = ad.style.top;
                        orientation = ad.style.orientation;
                    }

                    GameBannerView adView = new GameBannerView(activity, left, top, orientation);
                    adView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ad.notifyClick(0, "click");
                        }
                    });
                    view = adView;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(adView.getBannerWidthPx(), adView.getBannerHeightPx());
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.setMarginStart(left);
                    params.topMargin = top;
                    container.addView(view, params);
                    viewMap.put(ad, view);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void hide(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                handle.success(null);
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createGamePortalAd(SUDOPGamePortalAd ad) {
        ad.setOperateListener(new SUDOPGamePortalAd.OperateListener() {
            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    GamePortalAdView adView = new GamePortalAdView(activity);
                    adView.setOnAdItemClickListener(new GamePortalAdView.OnAdItemClickListener() {
                        @Override
                        public void onAdItemClick(int position, String name) {
                            // position: 0 到 8
                            // name: 广告名称
                            // 这里可以打开对应小游戏、跳转落地页、上报点击等
                            ad.notifyClick(0, "click");
                        }
                    });
                    adView.setOnCloseClickListener(new GamePortalAdView.OnCloseClickListener() {
                        @Override
                        public void onCloseClick(GamePortalAdView view) {
                            view.setVisibility(View.GONE);
                            ad.notifyClosed();
                        }
                    });
                    view = adView;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                    container.addView(view, params);
                    viewMap.put(ad, view);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void load(SUDOPStateHandle handle) {
                handle.success(null);
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createGameDrawerAd(SUDOPGameDrawerAd ad) {
        ad.setOperateListener(new SUDOPGameDrawerAd.OperateListener() {
            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    int top = 0;
                    if (ad.style != null) {
                        top = ad.style.top;
                    }
                    GameDrawerAdView adView = new GameDrawerAdView(activity, top);
                    adView.setOnDrawerAdItemClickListener(new GameDrawerAdView.OnDrawerAdItemClickListener() {
                        @Override
                        public void onDrawerAdItemClick(int position, GameDrawerAdView.DrawerAdItem item) {
                            ad.notifyClick(0, "click");
                        }
                    });
                    view = adView;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                    container.addView(view, params);
                    viewMap.put(ad, view);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void hide(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
                handle.success(null);
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view != null) {
                    container.removeView(view);
                    viewMap.remove(ad);
                }
                ad.notifyClosed();
            }
        });
        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

    public void createRewardedVideoAd(SUDOPRewardedAd ad) {
        ad.setOperateListener(new SUDOPRewardedAd.OperateListener() {
            private GetMeterialResp meterialResp;
            private SUDOPRewardVideoAdSSVData rewardVideoAdSSVData;
            private boolean isReportedRewardSSVData;
            private boolean isWatchedComplete;

            @Override
            public void show(SUDOPStateHandle handle) {
                View view = viewMap.get(ad);
                if (view == null) {
                    isWatchedComplete = false;
                    RewardedAdView adView = new RewardedAdView(activity);
                    adView.setVideoScaleType(RewardedAdView.VideoScaleType.FIT_CENTER);
                    adView.setOnRewardedAdListener(new RewardedAdView.OnRewardedAdListener() {

                        /**
                         * 已经看满要求时长，比如默认 30 秒。
                         */
                        @Override
                        public void onRewardedAdCompleted() {
                        }

                        /**
                         * 最终关闭回调。
                         *
                         * @param watchedComplete 是否看完，是否满足奖励条件
                         * @param watchedMillis   已观看毫秒数
                         * @param reason          关闭原因
                         */
                        @Override
                        public void onRewardedAdClosed(boolean watchedComplete, long watchedMillis, RewardedAdView.CloseReason reason) {
                            isWatchedComplete = watchedComplete;
                            reportCompleted(watchedComplete);
                            ad.notifyClosed(watchedComplete);

                            viewMap.remove(ad);
                            container.removeView(adView);
                        }

                        /**
                         * 播放出错。
                         */
                        @Override
                        public void onRewardedAdError(Exception e) {
                            ad.notifyError(-1, "error:" + e);
                        }

                        @Override
                        public void onClickVideo() {
                            ad.notifyClick(0, "click");
                        }
                    });
                    view = adView;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                    container.addView(view, params);
                    viewMap.put(ad, view);
                    adView.load("http://test-runtime.cocos.com/cocos-runtime-demo/media/videos/test4.mp4");
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                handle.success(null);
                ad.notifyShown();
            }

            @Override
            public void load(SUDOPStateHandle handle) {
                MainRepository.getAdMeterial(activity, 0, AppGameViewModel.userId, AppGameViewModel.SUD_APP_ID, new RxCallback<GetMeterialResp>() {
                    @Override
                    public void onNext(BaseResponse<GetMeterialResp> t) {
                        super.onNext(t);
                        if (t.getRet_code() == RetCode.SUCCESS) {
                            meterialResp = t.getData();
                            handle.success(null);
                            reportRewardSSVData();
                        } else {
                            handle.failure(t.getRet_code(), "error msg:" + t.getRet_msg());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        handle.failure(RetCode.Fail, "exception:" + e);
                    }
                });
            }

            @Override
            public void destroy() {
                View view = viewMap.get(ad);
                if (view instanceof RewardedAdView) {
                    RewardedAdView adView = (RewardedAdView) view;
                    adView.destroy();
                } else if (view != null) {
                    container.removeView(view);
                }
                viewMap.remove(ad);
                ad.notifyClosed(isWatchedComplete);
            }

            @Override
            public void setServerSideVerificationData(SUDOPRewardVideoAdSSVData data) {
                rewardVideoAdSSVData = data;
                reportRewardSSVData();
            }

            private void reportCompleted(boolean isWatchedComplete) {
                SUDOPRewardVideoAdSSVData rewardVideoAdSSVData = this.rewardVideoAdSSVData;
                GetMeterialResp meterialResp = this.meterialResp;
                if (meterialResp == null || rewardVideoAdSSVData == null) {
                    return;
                }
                int status = isWatchedComplete ? 1 : 2;
                MainRepository.rewardCompleted(activity, status, meterialResp.transaction_id, rewardVideoAdSSVData.sudTransactionId, new RxCallback<Object>() {
                    @Override
                    public void onSuccess(Object o) {
                        super.onSuccess(o);
                    }
                });
            }

            private void reportRewardSSVData() {
                if (isReportedRewardSSVData) {
                    return;
                }
                SUDOPRewardVideoAdSSVData rewardVideoAdSSVData = this.rewardVideoAdSSVData;
                GetMeterialResp meterialResp = this.meterialResp;
                if (meterialResp == null || rewardVideoAdSSVData == null) {
                    return;
                }
                isReportedRewardSSVData = true;
                MainRepository.reportReward(activity, meterialResp.transaction_id, rewardVideoAdSSVData.sudTransactionId,
                        rewardVideoAdSSVData.rewardItem, rewardVideoAdSSVData.rewardAmount, new RxCallback<ReportRewardResp>() {
                            @Override
                            public void onSuccess(ReportRewardResp reportRewardResp) {
                                super.onSuccess(reportRewardResp);
                            }
                        });
            }
        });

        // 这里模拟app初始化广告数据
        ThreadUtils.runOnUiThreadDelayed(() -> {
            ad.notifyLoaded();
        }, 500);
    }

}
