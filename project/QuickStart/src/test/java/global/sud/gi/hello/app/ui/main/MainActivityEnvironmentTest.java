package global.sud.gi.hello.app.ui.main;

import android.app.Application;
import android.widget.TextView;

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
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.base.AppConfig;
import global.sud.gi.hello.app.common.http.param.BaseUrlManager;
import global.sud.gi.hello.app.common.utils.GlobalSP;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28, application = Application.class)
public class MainActivityEnvironmentTest {

    private ActivityController<MainActivity> controller;

    @Before
    public void setUp() {
        Utils.init(RuntimeEnvironment.getApplication());
        GlobalSP.getSP().clear();
    }

    @After
    public void tearDown() {
        if (controller != null) {
            controller.destroy();
        }
        GlobalSP.getSP().clear();
    }

    @Test
    public void headerShowsPackagedVersions() throws Exception {
        controller = Robolectric.buildActivity(MainActivity.class).create().start().resume();
        TextView buildInfo = controller.get().findViewById(R.id.sud_demo_build_info);
        String aarVersion = String.valueOf(Class.forName("global.sud.mediation.version.Version").getField("kVersion").get(null));
        assertEquals("APK: " + BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE + "\nAAR: " + aarVersion, buildInfo.getText().toString());
    }

    @Test
    public void startupAlwaysUsesProductionDespiteSavedEnvironmentAndCredentials() {
        String productionAppId = AppConfig.SudGIP_APP_ID;
        String productionAppKey = AppConfig.SudGIP_APP_KEY;

        // 包括全新安装和旧版本保存过的所有环境。
        for (int position = -1; position < 4; position++) {
            GlobalSP.getSP().clear();
            if (position >= 0) {
                GlobalSP.getSP().put("key_game_env", position);
                GlobalSP.getSP().put("key_app_id", "custom-id");
                GlobalSP.getSP().put("key_app_key", "custom-key");
            }

            controller = Robolectric.buildActivity(MainActivity.class).create().start().resume();
            assertEquals(productionAppId, AppConfig.SudGIP_APP_ID);
            assertEquals(productionAppKey, AppConfig.SudGIP_APP_KEY);
            assertEquals("https://hello-op.sud.ltd/", BaseUrlManager.getBaseUrl());

            controller.pause().stop().destroy();
            controller = null;
        }
    }
}
