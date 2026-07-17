package global.sud.gi.hello.app.ui.main;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.widget.view.SimpleTextWatcher;
import global.sud.gi.hello.app.ui.main.model.GameModel;
import global.sud.gi.hello.app.ui.main.widget.SmokeSetTimeDialog;
import global.sud.gi.hello.app.ui.main.widget.SmokeTestDialog;
import global.sud.gi.hello.game.AppGameViewModel;
import global.sud.gi.hello.game.GameActivity;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.listener.SUDOPUninitSDKListener;
import global.sud.op.runtime.core.model.SUDOPGamePathType;

public class SmokeTestActivity extends BaseActivity {

    private List<GameModel> mDatas = new ArrayList<>();
    private MyAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEtUserId;
    private String mUserId = AppGameViewModel.userId;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isLoopSwitchGame;
    private int mStartInterval = 5;
    private int mStopInterval = 10;
    private GameModel mSelectedGameInfo;
    private boolean nextLoopIsStartGame;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_open_pass;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEtUserId = findViewById(R.id.et_user_id);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void initData() {
        super.initData();
        mAdapter = new MyAdapter(mDatas);
        mRecyclerView.setAdapter(mAdapter);
        mEtUserId.setText(mUserId);

        initList();
    }

    private void initList() {
        GameModel info = new GameModel();
        info.gameName = "flappybird";
        info.gameId = "2058069222793666561";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "op_demo";
        info.gameId = "2042865437426663426";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "linkclear";
        info.gameId = "2058085523675144193";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "bumper";
        info.gameId = "2058097391185682434";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "goast";
        info.gameId = "2058117904074194945";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "test";
        info.gameId = "2058812815757189122";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "打飞机";
        info.gameId = "2058756828891357186";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "暗黑斩";
        info.gameId = "2058799907522387970";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator3D 物理测试例";
        info.gameId = "2058837595260624897";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Canvas API 测试例";
        info.gameId = "2058846770103492610";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "WebGL Aqua 压测";
        info.gameId = "2059127382186041346";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "WebGL Compute 压测";
        info.gameId = "2059160210865037313";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "WebGL Jellyfish 压测";
        info.gameId = "2059167877130002433";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Three.js Performance Renderbundle";
        info.gameId = "2059168919066419201";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator3D 测试例(1)(3.7.3-webgl2.0)";
        info.gameId = "2059125270345588738";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator3D 测试例(2)(3.1.0-webgl2.0)";
        info.gameId = "2058836358930800641";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Runtime V3 API 测试例";
        info.gameId = "2059548740013707266";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Egret Ball 压测";
        info.gameId = "2059548326761517057";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator3D 性能压测";
        info.gameId = "2059547061428080641";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator Bunny 压测(NR)";
        info.gameId = "2059546568635109377";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);

        info = new GameModel();
        info.gameName = "Creator Bunny 压测(Creator 2.0)";
        info.gameId = "2059541686733688833";
        info.pathType = SUDOPGamePathType.GAME_ID;
        mDatas.add(info);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                startGame(mAdapter.getItem(position));
            }
        });
        mEtUserId.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                mUserId = s == null ? null : s.toString();
            }
        });
        findViewById(R.id.btn_uninit_sdk).setOnClickListener(v -> {
            SUDOP.uninitSDK(new SUDOPUninitSDKListener() {
                @Override
                public void onSuccess() {
                    ToastUtils.showLong("uninitSDK 成功");
                }

                @Override
                public void onFailure(int code, String msg) {
                    ToastUtils.showLong("uninitSDK 失败：(" + code + ")" + msg);
                }
            });
        });
        findViewById(R.id.btn_start_loop).setOnClickListener(v -> {
            SmokeSetTimeDialog dialog = new SmokeSetTimeDialog();
            dialog.mStartInterval = mStartInterval;
            dialog.mStopInterval = mStopInterval;
            dialog.setOnStartListener(new SmokeSetTimeDialog.OnStartListener() {
                @Override
                public void onStart(int startInterval, int stopInterval) {
                    mStartInterval = startInterval;
                    mStopInterval = stopInterval;
                    isLoopSwitchGame = true;
                    startLoopGame();
                }
            });
            dialog.show(getSupportFragmentManager(), null);
        });
        findViewById(R.id.btn_stop_loop).setOnClickListener(v -> {
            isLoopSwitchGame = false;
            stopLoopGame();
            ToastUtils.showLong("关闭循环");
        });
        findViewById(R.id.btn_select_params).setOnClickListener(v -> {
            SmokeTestDialog testSudRuntimeDialog = new SmokeTestDialog();
            testSudRuntimeDialog.mStartInterval = mStartInterval;
            testSudRuntimeDialog.mStopInterval = mStopInterval;
            testSudRuntimeDialog.setDatas(mDatas);
            testSudRuntimeDialog.setOnStartListener(new SmokeTestDialog.OnStartListener() {
                @Override
                public void onStart(int startInterval, int stopInterval, GameModel info) {
                    mStartInterval = startInterval;
                    mStopInterval = stopInterval;
                    mSelectedGameInfo = info;
                    isLoopSwitchGame = true;
                    startLoopGame();
                }
            });
            testSudRuntimeDialog.show(getSupportFragmentManager(), null);
        });
    }

    private void startLoopGame() {
        stopLoopGame();
        int second;
        if (nextLoopIsStartGame && mStartInterval > 0) {
            second = mStartInterval;
        } else if (mStopInterval > 0) {
            second = mStopInterval;
        } else {
            second = new Random().nextInt(20) + 1; // 1 - 20秒，随机开关
        }

        ToastUtils.showLong("下一次开关时间:" + second + "秒");
        mHandler.postDelayed(mLoopSwitchGameTask, second * 1000);
    }

    private void stopLoopGame() {
        mHandler.removeCallbacks(mLoopSwitchGameTask);
    }

    private void startGame(GameModel item) {
        GameActivity.start(this, item, mUserId);
    }

    private class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {

        public MyAdapter(@Nullable List<GameModel> data) {
            super(R.layout.item_name_list, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder model, GameModel info) {
            model.setText(R.id.tv_title, info.gameName);
        }

    }

    private Runnable mLoopSwitchGameTask = new Runnable() {
        @Override
        public void run() {
            switchGame();
        }
    };

    private void switchGame() {
        if (!isLoopSwitchGame) {
            return;
        }
        Activity topActivity = ActivityUtils.getTopActivity();
        if (topActivity instanceof GameActivity) {
            nextLoopIsStartGame = true;
            topActivity.finish();
        } else {
            nextLoopIsStartGame = false;
            if (mSelectedGameInfo == null) {
                List<GameModel> list = mAdapter.getData();
                int size = list.size();
                if (size > 0) {
                    int position = new Random().nextInt(size);
                    GameModel GameModel = list.get(position);
                    startGame(GameModel);
                }
            } else {
                startGame(mSelectedGameInfo);
            }
        }
        startLoopGame();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopLoopGame();
    }

}
