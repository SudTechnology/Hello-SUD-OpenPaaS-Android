package tech.sud.mgp.hello.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
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
    private static final int _REQUEST_CODE_PICK_JSON = 20001;

    private File gamePkgDir;
    private TextView tvInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvInfo = findViewById(R.id.tv_info);
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

    private void onClickStart() {
        if (gamePkgDir == null) {
            ToastUtils.showShort("请先选择游戏包目录");
            return;
        }

        GameModel gameModel = new GameModel();
        gameModel.gameName = gamePkgDir.getParentFile().getName();
        gameModel.gameId = gameModel.gameName;
        gameModel.gamePkgVersion = "1.0.0";
        gameModel.gameUrl = gamePkgDir.getAbsolutePath();
        gameModel.pathType = SUDOPGamePathType.DIR;
//        gameModel.gameUrl = "/sdcard/Download/aqua/app";
        gameModel.gameUrl = "/sdcard/Download/hayypmaster25.oppo.nearme.gamecente";
        QuickStartGameActivity.start(this, gameModel);
    }

    private void onClickSelect() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("*/*");
//        startActivityForResult(intent, _REQUEST_CODE_PICK_JSON);

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
            File gamePkgDir = new File(path);
            if (!gamePkgDir.exists()) {
                ToastUtils.showShort("游戏包目录不存在");
                return;
            }
            this.gamePkgDir = gamePkgDir;
            tvInfo.setText("当前选择运行的游戏包路径为:" + gamePkgDir);
        });

        chooser.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != _REQUEST_CODE_PICK_JSON || resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        if (uri == null) {
            return;
        }
        File file = UriUtils.uri2File(uri);
        File gamePkgDir = file.getParentFile();
        LogUtils.d("file:" + file);
        if (gamePkgDir == null || !gamePkgDir.exists()) {
            ToastUtils.showShort("游戏包目录不存在");
            return;
        }
        this.gamePkgDir = gamePkgDir;
        tvInfo.setText("当前选择运行的游戏包路径为:" + gamePkgDir);
    }

}
