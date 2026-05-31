package global.sud.gi.hello.ui.main;

import android.content.Intent;
import android.widget.ArrayAdapter;

import com.blankj.utilcode.util.LogUtils;
import com.gyf.immersionbar.ImmersionBar;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.AppConfig;
import global.sud.gi.hello.common.base.BaseActivity;
import global.sud.gi.hello.common.utils.GlobalSP;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {
 
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtils.d("statusBarHeight:" + ImmersionBar.getStatusBarHeight(this));
        String[] envList = {"Dev", "Fat", "Sim", "Pro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_selected_item, envList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        AppConfig.SudGIP_APP_ID = GlobalSP.getSP().getString(GlobalSP.KEY_APP_ID, AppConfig.SudGIP_APP_ID);
        AppConfig.SudGIP_APP_KEY = GlobalSP.getSP().getString(GlobalSP.KEY_APP_KEY, AppConfig.SudGIP_APP_KEY);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_game_id).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameIdActivity.class));
        });
        findViewById(R.id.btn_game_signature).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameSignatureActivity.class));
        });
        findViewById(R.id.btn_load_old_game).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadOldGameActivity.class));
        });
    }

}
