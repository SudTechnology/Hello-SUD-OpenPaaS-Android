package global.sud.gi.hello.game;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.blankj.utilcode.util.ToastUtils;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

import global.sud.gi.SUDGIWrapper.base.BaseGameViewModel;
import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.ViewUtils;
import global.sud.gi.hello.app.ui.game.GameInformationActivity;
import global.sud.gi.hello.app.ui.game.GameInformationDialog;
import global.sud.gi.hello.app.ui.main.model.GameModel;
import global.sud.op.runtime.client.model.MenuButtonBoundingClientRect;
import global.sud.op.runtime.core.SUDOP;
import global.sud.op.runtime.core.listener.SUDOPGetGameInformationListener;
import global.sud.op.runtime.core.model.SUDOPGameInformationModel;
import global.sud.op.runtime.core.model.SUDOPGamePathType;
import me.jessyan.autosize.AutoSize;
import me.jessyan.autosize.internal.CustomAdapt;

/**
 * 游戏页面
 * Game page
 */
public class GameActivity extends BaseActivity implements CustomAdapt {

    private String gameId;
    private String gameUrl;
    private String gamePkgVersion;
    private SUDOPGamePathType pathType;
    private String manifestJson;
    private final AppGameViewModel gameViewModel = new AppGameViewModel();
    private FrameLayout gameContainer;
    private ConstraintLayout adContainer;
    private ConstraintLayout topContainer;

    private String userId;

    private SUDOPGameInformationModel gameInformationModel;
    private boolean isRequestingGameInfo;
    private int oldOrientation = -1;
    private View containerMore;

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model) {
        start(context, model, null);
    }

    /**
     * 外部调用，打开游戏页面
     * External call to open the game page.
     */
    public static void start(Context context, GameModel model, String userId) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra("GameModel", model);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    @Override
    protected void setStatusBar() {
        updateStatusBar();
    }

    @Override
    protected boolean beforeSetContentView() {
        GameModel model = (GameModel) getIntent().getSerializableExtra("GameModel");
        if (model == null) {
            return true;
        }
        userId = getIntent().getStringExtra("userId");
        gameId = model.gameId;
        gameUrl = model.gameUrl;
        gamePkgVersion = model.gamePkgVersion;
        pathType = model.pathType;
        manifestJson = model.manifestJson;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gameContainer = findViewById(R.id.game_container); // 获取游戏View容器 English: Retrieve the game view container.
        adContainer = findViewById(R.id.ad_container);
        topContainer = findViewById(R.id.top_container);

        containerMore = findViewById(R.id.container_more);
        ViewUtils.addMarginTop(containerMore, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        if (!TextUtils.isEmpty(userId)) {
            gameViewModel.userId = userId;
        }

        // 设置WrappedClient
        gameViewModel.setSUDOPWrappedClient(new AppSUDOPWrappedClient(gameViewModel, this, gameContainer, adContainer, topContainer) {
            @Override
            public MenuButtonBoundingClientRect getMenuButtonBoundingClientRect() {
                MenuButtonBoundingClientRect rect = new MenuButtonBoundingClientRect();
                rect.width = containerMore.getMeasuredWidth();
                rect.height = containerMore.getMeasuredHeight();
                rect.top = containerMore.getTop();
                rect.right = containerMore.getRight();
                rect.bottom = containerMore.getBottom();
                rect.left = containerMore.getLeft();
                return rect;
            }
        });

        // 调用此方法，加载对应的游戏，开发者可根据业务决定什么时候加载游戏。
        // Call this method to load the corresponding game. Developers can decide when to load the game based on their business logic.
        gameViewModel.switchGame(this, gameId, pathType, gameUrl, gamePkgVersion, manifestJson);
        updateStatusBar();
    }

    @Override
    protected void setListeners() {
        super.setListeners();
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

        gameViewModel.progressLiveData.observe(this, new Observer<BaseGameViewModel.ProgressModel>() {
            @Override
            public void onChanged(BaseGameViewModel.ProgressModel model) {
                // Game loading progress changes
            }
        });

        gameViewModel.queryExitLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                finish();
            }
        });

        findViewById(R.id.iv_close).setOnClickListener(v -> finish());
        findViewById(R.id.iv_more).setOnClickListener(this::onClickMore);
    }

    private void onClickMore(View view) {
        if (gameInformationModel != null) {
            showGameInformationDialog(gameInformationModel);
            return;
        }
        if (isRequestingGameInfo) {
            return;
        }
        isRequestingGameInfo = true;
        SUDOP.getGameInformation(gameId, new SUDOPGetGameInformationListener() {
            @Override
            public void onSuccess(SUDOPGameInformationModel model) {
                gameInformationModel = model;
                showGameInformationDialog(model);
                isRequestingGameInfo = false;
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                ToastUtils.showShort("error retCode:" + retCode + " retMsg:" + retMsg);
                isRequestingGameInfo = false;
            }
        });
    }

    private void showGameInformationDialog(SUDOPGameInformationModel model) {
        if (model == null) {
            return;
        }
        GameInformationDialog dialog = GameInformationDialog.newInstance(model);
        dialog.setOnClickInformationListener(new GameInformationDialog.OnClickInformationListener() {
            @Override
            public void onClickRestart() {
                restartGame();
            }

            @Override
            public void onClickInformation() {
                GameInformationActivity.start(context, model);
            }
        });
        dialog.show(getSupportFragmentManager(), null);
    }

    private void restartGame() {
        gameViewModel.destroyGame();
        gameViewModel.switchGame(this, gameId, pathType, gameUrl, gamePkgVersion, manifestJson);
    }

    private void updateStatusBar() {
        int newOrientation = getResources().getConfiguration().orientation;
        if (oldOrientation == newOrientation) {
            return;
        }
        oldOrientation = newOrientation;
        // 这个沉浸式状态栏的使用是APP的业务，对于游戏而言不是必须的
        // The use of the immersive status bar is part of the app's functionality and is not essential for games.
        if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            ImmersionBar.with(this).statusBarColor(R.color.transparent).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).init();
        } else {
            ImmersionBar.with(this).fullScreen(true).hideBar(BarHide.FLAG_HIDE_BAR).init();
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
//        gameViewModel.destroyGame();
//        super.onBackPressed();
    }

    @Override
    public void finish() {
        gameViewModel.destroyGame();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.destroyGame();
    }

    @Override
    public boolean isBaseOnWidth() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
//        Point screenSize = DensityUtils.getAppScreenSize();
//        return screenSize.x < screenSize.y;
    }

    @Override
    public float getSizeInDp() {
        return 375;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AutoSize.autoConvertDensityOfCustomAdapt(this, this);
        updateStatusBar();
    }

}
