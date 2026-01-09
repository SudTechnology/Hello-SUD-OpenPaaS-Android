package tech.sud.mgp.hello.runtime1;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import tech.sud.gip.SUDGIPWrapper.decorator.SUDFSMMGListener_;
import tech.sud.gip.SUDGIPWrapper.decorator.SUDRuntime1FSMGameDecorator_;
import tech.sud.gip.SUDGIPWrapper.decorator.SUDRuntime1FSTAPPDecorator_;
import tech.sud.gip.SUDGIPWrapper.model.GameConfigModel;
import tech.sud.gip.SUDGIPWrapper.model.GameViewInfoModel;
import tech.sud.gip.SUDGIPWrapper.utils.SUDJsonUtils_;
import tech.sud.gip.core.ISUDFSMStateHandle;
import tech.sud.gip.r1.core.ISUDRuntime1FSTAPP;
import tech.sud.gip.r1.core.ISUDRuntime1ListenerInitSDK;
import tech.sud.gip.r1.core.SUDRuntime1;
import tech.sud.gip.r1.core.SUDRuntime1InitSDKParamModel;
import tech.sud.gip.r1.core.SUDRuntime1LoadGameParamModel;

/**
 * 游戏业务逻辑抽象类
 * 1.自定义ViewModel继承此类，实现对应方法。(注意：onAddGameView()与onRemoveGameView()与页面有交互)
 * 2.外部调用switchGame()方法启动游戏
 * 3.页面销毁时调用onDestroy()
 * 4.此类是base类，通常情况下不需要修改此类，有时间的情况下了解清楚此类的实现逻辑即可
 * <p>
 * Game business logic abstract class
 * 1. Define a custom ViewModel that extends this class and implement the corresponding methods. (Note: onAddGameView() and onRemoveGameView() interact with the page)
 * 2. Call the switchGame() method externally to start the game.
 * 3. Call onDestroy() when the page is destroyed.
 * 4. This class is a base class, and typically, there's no need to modify it. If time allows, understanding the implementation logic of this class is sufficient.
 */
public abstract class BaseRuntime1GameViewModel implements SUDFSMMGListener_ {
    private String TAG = "BaseRuntime1GameViewModel";
    /**
     * 当前使用的游戏id
     * The currently used game ID
     */
    private String playingGameId;

    /**
     * app调用sdk的封装类
     * The app's wrapper class for calling the SDK.
     */
    public final SUDRuntime1FSTAPPDecorator_ sudFSTAPPDecorator = new SUDRuntime1FSTAPPDecorator_();

    /**
     * 用于处理游戏SDK部分回调业务
     * Used to handle game SDK callback business.
     */
    private final SUDRuntime1FSMGameDecorator_ sudFSMGameDecorator = new SUDRuntime1FSMGameDecorator_();

    /**
     * 游戏View
     * Game view
     */
    public View gameView;

    /**
     * 游戏配置
     * Game configuration
     */
    public GameConfigModel gameConfigModel = new GameConfigModel();
    protected final Handler handler = new Handler(Looper.getMainLooper());

    private String mGameUrl;
    private String mGamePkgVersion;
    private Activity mActivity;

    public MutableLiveData<ProgressModel> progressLiveData = new MutableLiveData<>();

    /**
     * 外部调用切换游戏，传不同的gameId即可加载不同的游戏
     * gameId传0 等同于关闭游戏
     * <p>
     * External call to switch games, pass a different gameId to load different games.
     * Passing 0 as gameId is equivalent to closing the game.
     *
     * @param activity 游戏所在页面，用作于生命周期判断
     *                 The page where the game is located, used for lifecycle judgment.
     * @param gameId   游戏id，传入不同的游戏id，即可加载不同的游戏，传null等同于关闭游戏
     *                 Game ID, passing a different gameId will load a different game. Passing null is equivalent to closing the game.
     */
    public void switchGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mActivity = activity;
        if (Objects.equals(playingGameId, gameId)) {
            return;
        }
        destroyGame();
        playingGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        if (TextUtils.isEmpty(gameId) || TextUtils.isEmpty(gameUrl) || TextUtils.isEmpty(gamePkgVersion)) {
            return;
        }
        login(activity, gameId);
    }

    /**
     * 第1步，获取短期令牌code，用于换取游戏Server访问APP Server的长期ssToken
     * 接入方客户端 调用 接入方服务端 login 获取 短期令牌code
     * 参考文档时序图：sud-mgp-doc(https://docs.sud.tech/zh-CN/app/Client/StartUp-Android.html)
     * <p>
     * Step 1, obtain a short-term token code for exchanging a long-term ssToken to access the APP Server from the game Server.
     * The client of the integration party calls the login endpoint on the integration party server to obtain a short-term token code.
     * Refer to the sequence diagram in the documentation: sud-mgp-doc (https://docs.sud.tech/en-US/app/Client/StartUp-Android.html)
     *
     * @param activity 游戏所在页面
     *                 The page where the game is located.
     * @param gameId   游戏id
     *                 Game ID.
     */
    private void login(Activity activity, String gameId) {
        if (activity.isDestroyed() || TextUtils.isEmpty(gameId)) {
            return;
        }
        // 请求登录code
        // Request login code
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!Objects.equals(playingGameId, gameId)) {
                    return;
                }
                initSdk(activity, gameId, code);
            }

            @Override
            public void onFailed(int retCode, String retMsg) {
                logE("getCode onFailed:(" + retCode + ")" + retMsg);
                toastMsg("getCode onFailed:(" + retCode + ")" + retMsg);
                delayLoadGame(activity, gameId);
            }
        });
    }

    /**
     * 第2步，初始化SudMGP sdk
     * <p>
     * Step 2, initialize the SudMGP SDK.
     *
     * @param activity 游戏所在页面
     *                 The page where the game is located.
     * @param gameId   游戏id
     *                 Game ID.
     * @param code     令牌
     *                 Token.
     */
    private void initSdk(Activity activity, String gameId, String code) {
        String appId = getAppId();
        String appKey = getAppKey();
        // 初始化sdk
        // Initialize the SDK
        SUDRuntime1InitSDKParamModel params = new SUDRuntime1InitSDKParamModel();
        params.context = activity;
        params.appId = appId;
        params.appKey = appKey;
        params.userId = getUserId();
        params.code = code;
        SUDRuntime1.initSDK(params, new ISUDRuntime1ListenerInitSDK() {
            @Override
            public void onSuccess() {
                loadGame(activity, code, gameId);
            }

            @Override
            public void onFailure(int errCode, String errMsg) {
                toastMsg("initSDK onFailure:" + errMsg + "(" + errCode + ")");
                delayLoadGame(activity, gameId);
            }
        });
    }

    /**
     * 第3步，加载游戏
     * APP和游戏的相互调用
     * ISUDFSTAPP：APP调用游戏的接口
     * ISUDFSMMG：游戏调APP的响应回调
     * <p>
     * Step 3, load the game.
     * Interaction between the APP and the game.
     * ISUDFSTAPP: Interface for the APP to call the game.
     * ISUDFSMMG: Callback for the game to call back to the APP.
     *
     * @param activity 游戏所在页面
     *                 The page where the game is located.
     * @param code     登录令牌
     *                 Login token.
     * @param gameId   游戏id
     *                 Game ID.
     */
    private void loadGame(Activity activity, String code, String gameId) {
        if (activity.isDestroyed() || !Objects.equals(gameId, playingGameId)) {
            return;
        }
        // 给装饰类设置回调
        // Set a callback for the decorator class.
        sudFSMGameDecorator.setSudFSMMGListener(this);

        // 调用游戏sdk加载游戏
        // Invoke the game SDK to load the game.
        SUDRuntime1LoadGameParamModel sudLoadMGParamModel = new SUDRuntime1LoadGameParamModel();
        sudLoadMGParamModel.activity = activity;
        sudLoadMGParamModel.userId = getUserId();
//        sudLoadMGParamModel.roomId = gameRoomId;
//        sudLoadMGParamModel.code = code;
        sudLoadMGParamModel.gameId = gameId;
        sudLoadMGParamModel.pkgVersion = mGamePkgVersion;
        sudLoadMGParamModel.pkgUrl = mGameUrl;
//        sudLoadMGParamModel.language = getLanguageCode();
        ISUDRuntime1FSTAPP iSudRuntime1FSTAPP = SUDRuntime1.loadGame(sudLoadMGParamModel, sudFSMGameDecorator);

        // 如果返回空，则代表参数问题或者非主线程
        // If null is returned, it indicates a parameter issue or a non-main thread.
        if (iSudRuntime1FSTAPP == null) {
            toastMsg("loadGame params error");
            delayLoadGame(activity, gameId);
            return;
        }

        // APP调用游戏接口的装饰类设置
        // Decorator class setup for APP calling game interfaces.
        sudFSTAPPDecorator.setISUDFSTAPP(iSudRuntime1FSTAPP);

        // 获取游戏视图，将其抛回Activity进行展示
        // Activity调用：gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        // Retrieve the game view and throw it back to the Activity for display.
        // Activity invocation：gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        gameView = iSudRuntime1FSTAPP.getGameView();
        onAddGameView(gameView);
    }

    /**
     * 游戏加载失败的时候，延迟一会再重新加载
     * <p>
     * When the game fails to load, delay for a while and then reload.
     *
     * @param activity 游戏所在页面
     *                 The page where the game is located.
     * @param gameId   游戏id
     *                 Game ID.
     */
    private void delayLoadGame(Activity activity, String gameId) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId);
            }
        }, 5000);
    }

    /**
     * 销毁游戏
     * Destroy the game.
     */
    public void destroyGame() {
        if (!TextUtils.isEmpty(playingGameId)) {
            sudFSTAPPDecorator.destroyGame();
            sudFSMGameDecorator.destroyGame();
            playingGameId = null;
            gameView = null;
            onRemoveGameView();
        }
    }

    // region 子类需要实现的方法 English:Methods to be implemented by subclasses.

    /**
     * 向接入方服务器获取code
     * Get the code from the integration party server.
     */
    protected abstract void getCode(Activity activity, String userId, String appId, GameGetCodeListener listener);

    /**
     * 设置当前用户id(接入方定义)
     * Set the current user ID (defined by the integration party).
     *
     * @return 返回用户id
     * Returns the user ID.
     */
    protected abstract String getUserId();

    /**
     * 设置游戏所用的appId
     * Set the appId used by the game.
     *
     * @return 返回游戏服务appId
     * Returns the game service appId.
     */
    protected abstract String getAppId();

    /**
     * 设置游戏所用的appKey
     * Set the appKey used by the game.
     *
     * @return 返回游戏服务appKey
     * Returns the game service appKey.
     */
    protected abstract String getAppKey();

    /**
     * 设置游戏的语言代码
     * 参考文档：https://docs.sud.tech/zh-CN/app/Client/Languages/
     * <p>
     * Set the language code for the game.
     * Refer to the documentation: https://docs.sud.tech/en-US/app/Client/Languages/
     *
     * @return 返回语言代码
     * Returns the language code.
     */
    protected abstract String getLanguageCode();

    /**
     * 设置游戏的安全操作区域
     * Set the secure operation area for the game.
     *
     * @param gameViewInfoModel 游戏视图大小
     *                          gameViewInfoModel Game view size.
     */
    protected abstract void getGameRect(GameViewInfoModel gameViewInfoModel);

    /**
     * 将游戏View添加到页面中
     * Add the game view to the page.
     */
    protected abstract void onAddGameView(View gameView);

    /**
     * 将页面中的游戏View移除
     * Remove the game view from the page.
     */
    protected abstract void onRemoveGameView();

    // endregion 子类需要实现的方法 English：Methods that need to be implemented by subclasses.

    // region 游戏侧回调 English：Callback from the game side

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     * <p>
     * Game log
     * Minimum version: v1.1.30.xx
     */
    @Override
    public void onGameLog(String str) {
        SUDFSMMGListener_.super.onGameLog(str);
    }

    /**
     * 游戏开始
     * 最低版本：v1.1.30.xx
     * <p>
     * Game start
     * Minimum version: v1.1.30.xx
     */
    @Override
    public void onGameStarted() {
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     * <p>
     * Game destruction
     * Minimum version: v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
    }

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     * <p>
     * Code expired, needs to be implemented.
     * The APP integration party needs to call handle.success or handle.fail.
     *
     * @param dataJson {"code":"value"}
     */
    @Override
    public void onExpireCode(ISUDFSMStateHandle handle, String dataJson) {
    }

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   handle
     * @param dataJson {}
     */
    @Override
    public void onGetGameViewInfo(ISUDFSMStateHandle handle, String dataJson) {
        processOnGetGameViewInfo(gameView, handle);
    }

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     * <p>
     * Obtain game configuration, needs to be implemented.
     * The APP integration party needs to call handle.success or handle.fail.
     *
     * @param handle   handle
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    @Override
    public void onGetGameCfg(ISUDFSMStateHandle handle, String dataJson) {
        processOnGetGameCfg(handle, dataJson);
    }
    // endregion 游戏侧回调 English：Callback from the game side

    /**
     * 处理游戏视图信息(游戏安全区)
     * 文档：https://docs.sud.tech/zh-CN/app/Client/API/ISUDFSMMG/onGetGameViewInfo.html
     * <p>
     * Handle game view information (game safe area).
     * Documentation: https://docs.sud.tech/en-US/app/Client/API/ISUDFSMMG/onGetGameViewInfo.html
     */
    public void processOnGetGameViewInfo(View gameView, ISUDFSMStateHandle handle) {
        // 拿到游戏View的宽高
        // Get the width and height of the game view.
        int gameViewWidth = gameView.getMeasuredWidth();
        int gameViewHeight = gameView.getMeasuredHeight();
        if (gameViewWidth > 0 && gameViewHeight > 0) {
            notifyGameViewInfo(handle, gameViewWidth, gameViewHeight);
            return;
        }

        // 如果游戏View未加载完成，则监听加载完成时回调
        // If the game view is not loaded completely, listen for a callback when it finishes loading.
        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = gameView.getMeasuredWidth();
                int height = gameView.getMeasuredHeight();
                notifyGameViewInfo(handle, width, height);
            }
        });
    }

    /**
     * 通知游戏，游戏视图信息
     * Notify the game about the game view information.
     */
    private void notifyGameViewInfo(ISUDFSMStateHandle handle, int gameViewWidth, int gameViewHeight) {
        GameViewInfoModel gameViewInfoModel = new GameViewInfoModel();
        gameViewInfoModel.ret_code = 0;
        // 游戏View大小
        // Game view size
        gameViewInfoModel.view_size.width = gameViewWidth;
        gameViewInfoModel.view_size.height = gameViewHeight;

        // 游戏安全操作区域
        // Game secure operation area
        getGameRect(gameViewInfoModel);

        // 给游戏侧进行返回
        // Return to the game side.
        String json = SUDJsonUtils_.toJson(gameViewInfoModel);
        // 如果设置安全区有疑问，可将下面的日志打印出来，分析json数据
        // 正确的格式为：{"ret_code":0,"view_game_rect":{"bottom":156,"left":0,"right":0,"top":196},"view_size":{"height":1920,"width":1080}}
        // 如果发生debug版本游戏正常，release版本游戏不正常，请检查是否混淆了GameViewInfoModel类，导致json序列化类的成员发生了变化
        // Log.d("SudBaseGameViewModel", "notifyGameViewInfo:" + json);

        // If there are questions about setting the secure area, you can print the following log to analyze the JSON data.
        // The correct format is: {"ret_code":0,"view_game_rect":{"bottom":156,"left":0,"right":0,"top":196},"view_size":{"height":1920,"width":1080}}
        // If the game works normally in the debug version but not in the release version, check if the GameViewInfoModel class is obfuscated, causing changes in the serialized members of the JSON.
        // Log.d("SudBaseGameViewModel", "notifyGameViewInfo:" + json);

        handle.success(json);
    }

    public void onStart() {
        sudFSTAPPDecorator.startGame();
    }

    public void onResume() {
        // playGame和pauseGame要配对
        // playGame and pauseGame should be paired.
        sudFSTAPPDecorator.playGame();
    }

    public void onPause() {
        // playGame和pauseGame要配对
        // playGame and pauseGame should be paired.
        sudFSTAPPDecorator.pauseGame();
    }

    public void onStop() {
        sudFSTAPPDecorator.stopGame();
    }

    /**
     * 处理游戏配置
     * 文档：https://docs.sud.tech/zh-CN/app/Client/API/ISUDFSMMG/onGetGameCfg.html
     * <p>
     * Handle game configuration.
     * Documentation: https://docs.sud.tech/en-US/app/Client/API/ISUDFSMMG/onGetGameCfg.html
     */
    public void processOnGetGameCfg(ISUDFSMStateHandle handle, String dataJson) {
        handle.success(SUDJsonUtils_.toJson(gameConfigModel));
    }

    /**
     * 游戏login(getCode)监听
     * Game login (getCode) listener
     */
    public interface GameGetCodeListener {
        void onSuccess(String code);

        void onFailed(int retCode, String retMsg);
    }

    @Override
    public void onGameLoadingProgress(int stage, int retCode, int progress) {
        SUDFSMMGListener_.super.onGameLoadingProgress(stage, retCode, progress);
        progressLiveData.setValue(new ProgressModel(stage, retCode, progress));
        logD("onGameLoadingProgress:" + stage + " retCode:" + retCode + " progress:" + progress);
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
    }

    private void logW(String msg) {
        Log.w(TAG, msg);
    }

    private void logE(String msg) {
        Log.e(TAG, msg);
    }

    private void toastMsg(String msg) {
        if (mActivity != null) {
            Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
        }
    }

    public static class ProgressModel {
        public int stage; // 阶段：start=1,loading=2,end=3
        public int retCode; // 错误码：0成功
        public int progress; // 进度：[0, 100]

        public ProgressModel(int stage, int retCode, int progress) {
            this.stage = stage;
            this.retCode = retCode;
            this.progress = progress;
        }
    }

}
