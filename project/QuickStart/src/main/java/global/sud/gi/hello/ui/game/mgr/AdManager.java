package global.sud.gi.hello.ui.game.mgr;

import android.app.Activity;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.blankj.utilcode.util.ToastUtils;

import java.util.HashMap;
import java.util.Map;

import global.sud.gi.hello.ui.game.widget.view.ad.BannerAdView;
import global.sud.op.runtime.core.ad.SUDOPBannerAd;

public class AdManager {

    private ConstraintLayout container;
    private Activity activity;
    private Map<Object, View> viewMap = new HashMap<>(); // 存储每个广告id

    public AdManager(Activity activity, ConstraintLayout container) {
        this.activity = activity;
        this.container = container;
    }

    public void createBannerAd(SUDOPBannerAd bannerAd) {
        bannerAd.setOperateListener(new SUDOPBannerAd.OperateListener() {
            @Override
            public void show() {
                View view = viewMap.get(bannerAd);
                if (view == null) {
                    view = new BannerAdView(activity);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(bannerAd.style.width, bannerAd.style.height);
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    params.setMarginStart(bannerAd.style.left);
                    params.topMargin = bannerAd.style.top;
                    container.addView(view, params);
                    viewMap.put(bannerAd, view);
                }
            }

            @Override
            public void hide() {
                ToastUtils.showLong("隐藏banner广告");
            }

            @Override
            public void destroy() {
                ToastUtils.showLong("销毁banner广告");
            }
        });
        bannerAd.notifyDidLoad();
    }

}
