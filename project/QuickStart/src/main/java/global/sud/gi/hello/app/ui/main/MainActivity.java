package global.sud.gi.hello.app.ui.main;

import android.content.Intent;
import android.widget.TextView;

import global.sud.gi.hello.BuildConfig;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.SUDGILanguageConfig;

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
        TextView buildInfoView = findViewById(R.id.sud_demo_build_info);
        buildInfoView.setText("APK: " + BuildConfig.VERSION_NAME + " " + BuildConfig.BUILD_TYPE + "\nAAR: " + getSUDMAarVersion());
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_game_id).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameIdActivity.class));
        });
        findViewById(R.id.btn_get_game_list).setOnClickListener(v -> {
            startActivity(new Intent(this, GameListAPITestActivity.class));
        });
        findViewById(R.id.sud_op_selection_game_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, GameListAPITestActivity.class);
            intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_MODE, true);
            startActivity(intent);
        });
        findViewById(R.id.sud_op_selection_list).setOnClickListener(v -> {
            Intent intent = new Intent(this, GameListAPITestActivity.class);
            intent.putExtra(GameListAPITestActivity.EXTRA_SELECTION_LIST_MODE, true);
            startActivity(intent);
        });
        findViewById(R.id.sud_gi_language_entry).setOnClickListener(view ->
                startActivity(new Intent(this, SUDGILanguageActivity.class)));
        findViewById(R.id.btn_game_signature).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadGameSignatureActivity.class));
        });
        findViewById(R.id.btn_load_old_game).setOnClickListener(v -> {
            startActivity(new Intent(this, LoadOldGameActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView languageEntry = findViewById(R.id.sud_gi_language_entry);
        languageEntry.setText(getString(R.string.sud_gi_language_entry,
                SUDGILanguageConfig.getSelectedLabel(this)));
    }

    private String getSUDMAarVersion() {
        try {
            Class<?> versionClass = Class.forName("global.sud.mediation.version.Version");
            return String.valueOf(versionClass.getField("kVersion").get(null));
        } catch (ReflectiveOperationException | LinkageError e) {
            return "not included";
        }
    }

}
