package global.sud.gi.hello.game.pay;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.app.ui.splash.SplashActiivty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class SUDGIPaymentReturnActivityTest {

    private Application application;
    private ActivityController<SUDGIPaymentReturnActivity> controller;
    private ActivityController<SUDGITestGameActivity> gameController;
    private ActivityController<SUDGITestHomeActivity> homeController;

    @Before
    public void setUp() {
        application = RuntimeEnvironment.getApplication();
        Utils.init(application);
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.destroy();
        }
        if (gameController != null) {
            gameController.destroy();
        }
        if (homeController != null) {
            homeController.destroy();
        }
    }

    @Test
    public void manifestResolvesConfiguredPaymentReturnOnly() {
        PackageManager packageManager = application.getPackageManager();
        ResolveInfo resolved = packageManager.resolveActivity(paymentReturn("?out_trade_no=test-order"), PackageManager.MATCH_DEFAULT_ONLY);
        assertNotNull(resolved);
        assertEquals(SUDGIPaymentReturnActivity.class.getName(), resolved.activityInfo.name);
        assertTrue(resolved.activityInfo.exported);
        assertNull(packageManager.resolveActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(BuildConfig.PAYMENT_RETURN_SCHEME + "://pay/other")), PackageManager.MATCH_DEFAULT_ONLY));
        assertNull(packageManager.resolveActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("unregistered://pay/result")), PackageManager.MATCH_DEFAULT_ONLY));
    }

    @Test
    public void warmReturnClearsBrowserAboveExistingGameInsteadOfMovingWholeTask() {
        homeController = Robolectric.buildActivity(SUDGITestHomeActivity.class).create();
        gameController = Robolectric.buildActivity(SUDGITestGameActivity.class).create();
        Activity game = gameController.get();

        SUDGIPaymentReturnActivity activity = create(paymentReturn("?out_trade_no=test-order"));

        // 外部浏览器不在本进程的生命周期列表中，回跳必须从原游戏发起清栈。
        assertTrue(ActivityUtils.getActivityList().contains(game));
        Intent returnIntent = shadowOf(game).getNextStartedActivity();
        assertNotNull(returnIntent);
        assertEquals(new ComponentName(application, SUDGITestGameActivity.class), returnIntent.getComponent());
        assertEquals(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP, returnIntent.getFlags());
        assertNull(returnIntent.getData());
        assertNull(returnIntent.getExtras());
        assertFalse(game.isFinishing());
        assertNull(shadowOf(homeController.get()).getNextStartedActivity());
        assertNull(shadowOf(activity).getNextStartedActivity());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void coldReturnLaunchesAppWithoutForwardingPaymentClaims() {
        Intent incoming = paymentReturn("?out_trade_no=test-order&success=true");
        incoming.putExtra("payment_success", true);
        SUDGIPaymentReturnActivity activity = create(incoming);

        Intent launch = shadowOf(activity).getNextStartedActivity();
        assertNotNull(launch);
        assertEquals(new ComponentName(application, SplashActiivty.class), launch.getComponent());
        assertEquals(Intent.FLAG_ACTIVITY_NEW_TASK, launch.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK);
        assertNull(launch.getData());
        assertNull(launch.getExtras());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void finishingGameFallsBackToAppLaunch() {
        gameController = Robolectric.buildActivity(SUDGITestGameActivity.class).create();
        gameController.get().finish();
        SUDGIPaymentReturnActivity activity = create(paymentReturn(""));
        assertNotNull(shadowOf(activity).getNextStartedActivity());
        assertNull(shadowOf(gameController.get()).getNextStartedActivity());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void destroyedGameFallsBackToAppLaunch() {
        gameController = Robolectric.buildActivity(SUDGITestGameActivity.class).create();
        gameController.destroy();
        gameController = null;
        SUDGIPaymentReturnActivity activity = create(paymentReturn(""));
        assertNotNull(shadowOf(activity).getNextStartedActivity());
        assertTrue(activity.isFinishing());
    }

    @Test
    public void explicitIntentCannotBypassUriValidation() {
        String[] invalidUris = {"https://pay/result", BuildConfig.PAYMENT_RETURN_SCHEME + "://other/result",
                BuildConfig.PAYMENT_RETURN_SCHEME + "://pay/other", BuildConfig.PAYMENT_RETURN_SCHEME + "://user@pay/result",
                BuildConfig.PAYMENT_RETURN_SCHEME + "://pay:80/result"};
        for (String uri : invalidUris) {
            SUDGIPaymentReturnActivity activity = create(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
            assertTrue(uri, activity.isFinishing());
            assertNull(uri, shadowOf(activity).getNextStartedActivity());
            controller.destroy();
            controller = null;
        }
    }

    @Test
    public void missingDataIsRejected() {
        SUDGIPaymentReturnActivity activity = create(new Intent(Intent.ACTION_VIEW));
        assertTrue(activity.isFinishing());
        assertNull(shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void wrongActionIsRejected() {
        Intent incoming = paymentReturn("");
        incoming.setAction(Intent.ACTION_MAIN);
        SUDGIPaymentReturnActivity activity = create(incoming);
        assertTrue(activity.isFinishing());
        assertNull(shadowOf(activity).getNextStartedActivity());
    }

    private Intent paymentReturn(String query) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.PAYMENT_RETURN_SCHEME + "://pay/result" + query))
                .addCategory(Intent.CATEGORY_BROWSABLE);
    }

    private SUDGIPaymentReturnActivity create(Intent intent) {
        controller = Robolectric.buildActivity(SUDGIPaymentReturnActivity.class, intent).create();
        return controller.get();
    }

    public static class SUDGITestGameActivity extends Activity {
    }

    public static class SUDGITestHomeActivity extends Activity {
    }
}
