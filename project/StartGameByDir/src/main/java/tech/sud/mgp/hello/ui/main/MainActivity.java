package tech.sud.mgp.hello.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;

import global.sud.op.runtime.core.model.SUDOPGamePathType;
import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.ui.game.QuickStartGameActivity;

/**
 * 主页
 */
public class MainActivity extends BaseActivity {

    private TextView tvInfo;
    private EditText etPath;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvInfo = findViewById(R.id.tv_info);
        etPath = findViewById(R.id.et_path);
        initPermission();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                requestPermissions(permissions, 0);
            }
        }
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_select).setOnClickListener(v -> {
            onClickSelect();
        });
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            onClickStart();
        });
    }

    private void onClickSelect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ToastUtils.showLong("当前目录选择器不支持高于Android10的版本，请手动输入");
            return;
        } else {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            boolean isGranted = true;
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    break;
                }
            }
            if (!isGranted) {
                ToastUtils.showShort("没有拿到权限");
                return;
            }
        }

        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(this)
                .withFragmentManager(getFragmentManager())
                .allowCustomPath(true)
//                .withPredefinedPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();
        chooser.setOnSelectListener(path -> {
            if (TextUtils.isEmpty(path)) {
                return;
            }
            etPath.setText(path);
        });
        chooser.show();

    }

    private void onClickStart() {
        String path = getInputPath();
        File gameDir;
        if (TextUtils.isEmpty(path) || !(gameDir = new File(path)).exists()) {
            ToastUtils.showShort("游戏目录不存在");
            return;
        }

        GameModel gameModel = new GameModel();
        gameModel.gameName = gameDir.getParentFile().getName();
        gameModel.gameId = gameModel.gameName;
        gameModel.gamePkgVersion = "1.0.0";
        gameModel.gameUrl = gameDir.getAbsolutePath();
        gameModel.pathType = SUDOPGamePathType.DIR;
//        gameModel.gameUrl = "/sdcard/Download/aqua/app";
        QuickStartGameActivity.start(this, gameModel);
    }

    private String getInputPath() {
        Editable text = etPath.getText();
        if (text == null) {
            return null;
        }
        return text.toString();
    }

}
