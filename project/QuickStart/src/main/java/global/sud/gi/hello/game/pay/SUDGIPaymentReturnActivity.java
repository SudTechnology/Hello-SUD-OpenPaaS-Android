package global.sud.gi.hello.game.pay;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;

import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.app.ui.splash.SplashActiivty;

/** 支付网页回跳入口；支付结果仍由原支付弹窗向 App Server 查单确认。 */
public class SUDGIPaymentReturnActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri uri = intent == null ? null : intent.getData();
        // 显式 Intent 可以绕过 intent-filter，因此在入口再次校验，不转发外部参数。
        if (intent == null || !Intent.ACTION_VIEW.equals(intent.getAction()) || uri == null
                || !BuildConfig.PAYMENT_RETURN_SCHEME.equals(uri.getScheme())
                || !"pay".equals(uri.getAuthority()) || !"/result".equals(uri.getPath())) {
            finish();
            return;
        }

        if (!restoreAppActivity()) {
            startActivity(new Intent(this, SplashActiivty.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        finish();
    }

    private boolean restoreAppActivity() {
        // 现有生命周期工具按最近访问顺序返回本进程页面，不包含外部浏览器。
        for (Activity activity : ActivityUtils.getActivityList()) {
            if (activity instanceof SUDGIPaymentReturnActivity || activity.isFinishing() || activity.isDestroyed()) {
                continue;
            }
            // 从原页面的任务栈发起：移除上方的支付网页，但复用原页面和支付弹窗。
            // 只移动整个任务栈会把同栈的浏览器再次置前；启动主页则会销毁游戏。
            Intent returnIntent = new Intent(activity, activity.getClass());
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(returnIntent);
            return true;
        }
        return false;
    }
}
