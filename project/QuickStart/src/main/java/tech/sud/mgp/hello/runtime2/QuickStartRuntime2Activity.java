package tech.sud.mgp.hello.runtime2;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import java.util.Locale;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.base.BaseActivity;
import tech.sud.mgp.hello.common.http.param.RetCode;
import tech.sud.mgp.hello.common.utils.ViewUtils;
import tech.sud.mgp.hello.ui.game.widget.GameModeDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomMoreDialog;
import tech.sud.mgp.hello.ui.game.widget.GameRoomTopView;
import tech.sud.mgp.hello.ui.main.GameModel;

/**
 * 游戏页面
 * Game page
 */
public class QuickStartRuntime2Activity extends BaseActivity {

    private String gameId;
    private String gameUrl;
    private String gamePkgVersion;
    private GameRoomTopView topView;
    private final QuickStartRuntime2GameViewModel gameViewModel = new QuickStartRuntime2GameViewModel();
    private TextView tvProgress;

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model) {
        Intent intent = new Intent(context, QuickStartRuntime2Activity.class);
        intent.putExtra("CrGameModel", model);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        GameModel model = (GameModel) getIntent().getSerializableExtra("CrGameModel");
        if (model == null) {
            return true;
        }
        gameId = model.gameId;
        gameUrl = model.gameUrl;
        gamePkgVersion = model.gamePkgVersion;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        topView = findViewById(R.id.room_top_view);
        tvProgress = findViewById(R.id.tv_progress);

        ViewUtils.addMarginTop(topView, ImmersionBar.getStatusBarHeight(this));

        topView.setName(getString(R.string.room_name));
    }

    @Override
    protected void initData() {
        super.initData();

        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        // Call this method to load the corresponding game. Developers can decide when to load the game based on their business logic.
        gameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        FrameLayout gameContainer = findViewById(R.id.game_container); // 获取游戏View容器 English: Retrieve the game view container.
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) { // 在关闭游戏时，把游戏View给移除 English: When closing the game, remove the game view.
                    gameContainer.removeAllViews();
                } else { // 把游戏View添加到容器内 English: Add the game view to the container.
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        gameViewModel.progressLiveData.observe(this, new Observer<BaseRuntime2GameViewModel.ProgressModel>() {
            @Override
            public void onChanged(BaseRuntime2GameViewModel.ProgressModel model) {
                updateProgress(model);
            }
        });

        // 选择游戏的点击监听
        // Click listener for selecting the game.
        topView.setSelectGameClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGameModeDialog();
            }
        });

        // 更多按钮的点击监听
        // Click listener for the 'More' button.
        topView.setMoreOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDialog();
            }
        });
    }

    private void updateProgress(BaseRuntime2GameViewModel.ProgressModel model) {
        if (model == null) {
            return;
        }
        if (model.stage == 3) {
            tvProgress.setVisibility(View.GONE);
        } else {
            tvProgress.setVisibility(View.VISIBLE);
            if (model.retCode == RetCode.SUCCESS) {
                tvProgress.setText(String.format(Locale.US, "加载百分比为:%d%%", model.progress));
            } else {
                tvProgress.setText(String.format(Locale.US, "加载失败，code：%d", model.retCode));
            }
        }
    }

    private void showMoreDialog() {
        GameRoomMoreDialog dialog = new GameRoomMoreDialog();
        dialog.setExitOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                gameViewModel.destroyGame();
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void showGameModeDialog() {
        GameModeDialog dialog = new GameModeDialog();
        dialog.setPlayingGameId(gameId);
        dialog.setRuntime(2);
        dialog.setSelectGameListener(new GameModeDialog.SelectGameListener() {
            @Override
            public void onSelectGame(GameModel model) {
                changeGame(model);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void changeGame(GameModel model) {
        gameId = model.gameId;
        gameUrl = model.gameUrl;
        gamePkgVersion = model.gamePkgVersion;
        tvProgress.setVisibility(View.GONE);
        gameViewModel.destroyGame();
        gameViewModel.switchGame(this, gameId, gameUrl, gamePkgVersion);
    }

    private void updateStatusBar() {
        // 这个沉浸式状态栏的使用是APP的业务，对于游戏而言不是必须的
        // The use of the immersive status bar is part of the app's functionality and is not essential for games.
        if (!TextUtils.isEmpty(gameId)) { // 玩着游戏 English: Playing the game.
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_SHOW_BAR).init();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            updateStatusBar();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusBar();
        // 注意：要在此处调用onResume()方法
        // Note: Call the onResume() method here.
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注意：要在此处调用onPause()方法
        // Note: Call the onPause() method here.
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    @Override
    public void onBackPressed() {
        // 注意：需要保证页面销毁之前，先调用游戏的销毁方法
        // 如果有其他地方调用finish()，那么也要在finish()之前，先调用游戏的销毁方法

        // Note: Ensure that the game's destruction method is called before the page is destroyed.
        // If finish() is called elsewhere, make sure to call the game's destruction method before finish().

        gameViewModel.destroyGame();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.destroyGame();
    }

}
