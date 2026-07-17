package global.sud.gi.hello.app.ui.main;

import android.content.Intent;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.quickstart.QuickStartGameActivity;

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
        findViewById(R.id.btn_quick_start_game).setOnClickListener(v -> {
            startActivity(new Intent(this, QuickStartGameActivity.class));
        });
    }

}
