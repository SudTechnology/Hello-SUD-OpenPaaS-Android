package tech.sud.mgp.hello.runtime;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

import tech.sud.gip.runtime.core.ISUDRuntimeFSMGame;
import tech.sud.gip.runtime.core.ISUDRuntimeFSTAPP;
import tech.sud.gip.runtime.core.ISUDRuntimeListenerInitSDK;
import tech.sud.gip.runtime.core.SUDRuntime;
import tech.sud.gip.runtime.core.SUDRuntimeGameAudioSession;
import tech.sud.gip.runtime.core.SUDRuntimeGameCoreHandle;
import tech.sud.gip.runtime.core.SUDRuntimeGameHandle;
import tech.sud.gip.runtime.core.SUDRuntimeGameRuntime;
import tech.sud.gip.runtime.core.SUDRuntimeInitSDKParamModel;
import tech.sud.gip.runtime.core.SUDRuntimeLoadGameParamModel;


public abstract class BaseRuntimeGameViewModel {

    private String TAG = "BaseRuntimeGameViewModel";
    private Activity mActivity;
    private SUDRuntimeGameRuntime mRuntime;
    private SUDRuntimeGameCoreHandle mCoreHandle;

    public Handler handler = new Handler(Looper.getMainLooper());
    private String mGameId;
    private String mGameUrl;
    private String mGamePkgVersion;
    private SUDRuntimeGameHandle mGameHandle;
    private Boolean _isGameStateChanging = false;
    private int _currentGameState = SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE;
    private int _expectGameState = SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE;
    private Boolean _isGameInstalled = false;
    private boolean isMute;
    private boolean isGameStarted;
    private AudioManager _audioManager;
    private ISUDRuntimeFSTAPP mISUDRuntimeFSTAPP;
    private AudioManager.OnAudioFocusChangeListener afChangeListener;
    public MutableLiveData<String> gameMGCommonGameFinishLiveData = new MutableLiveData<>();
    public MutableLiveData<String> gameStartedLiveData = new MutableLiveData<>();
    public MutableLiveData<ProgressModel> progressLiveData = new MutableLiveData<>();

    /**
     * 启动游戏
     *
     * @param activity       页面
     * @param gameId         游戏id
     * @param gameUrl        游戏包的url
     * @param gamePkgVersion 游戏包版本
     */
    public void switchGame(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mActivity = activity;
        if (Objects.equals(gameId, mGameId)) {
            return;
        }
        destroyGame();
        if (_audioManager == null) {
            _audioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        }
        mGameId = gameId;
        mGameUrl = gameUrl;
        mGamePkgVersion = gamePkgVersion;
        if (TextUtils.isEmpty(mGameId) || TextUtils.isEmpty(gameUrl) || TextUtils.isEmpty(gamePkgVersion)) {
            return;
        }
        login(activity, gameId, gameUrl, gamePkgVersion);
        onResume();
    }

    private void createRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        Bundle options = new Bundle();
        SUDRuntime.createRuntime(activity, options, new SUDRuntimeGameRuntime.RuntimeCreateListener() {
            @Override
            public void onSuccess(SUDRuntimeGameRuntime runtime) {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                mRuntime = runtime;
                loadCore(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(Throwable error) {
                logE("createRuntime fail:" + error);
                toastMsg("createRuntime fail:" + error);
                delayCreateRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void login(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (activity.isDestroyed() || TextUtils.isEmpty(mGameId)) {
            logD("login end idDestroyed:" + activity.isDestroyed() + " gameId:" + mGameId);
            return;
        }
        getCode(activity, getUserId(), getAppId(), new GameGetCodeListener() {
            @Override
            public void onSuccess(String code) {
                if (!gameId.equals(mGameId)) {
                    return;
                }
                initSdk(activity, gameId, gameUrl, gamePkgVersion, code);
            }

            @Override
            public void onFailed(int retCode, String retMsg) {
                logE("getCode onFailed:(" + retCode + ")" + retMsg);
                toastMsg("getCode onFailed:(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void delayLogin(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                login(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void initSdk(Activity activity, String gameId, String gameUrl, String gamePkgVersion, String code) {
        SUDRuntimeInitSDKParamModel initSDKParamModel = new SUDRuntimeInitSDKParamModel();
        initSDKParamModel.context = activity.getApplicationContext();
        initSDKParamModel.appId = getAppId();
        initSDKParamModel.appKey = getAppKey();
        initSDKParamModel.userId = getUserId();
        initSDKParamModel.code = code;

        SUDRuntime.initSDK(initSDKParamModel, new ISUDRuntimeListenerInitSDK() {
            @Override
            public void onSuccess() {
                createRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                logE("initSDK fail(" + retCode + ")" + retMsg);
                toastMsg("initSDK fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void loadGame(Activity activity, String gameId, String gamePkgVersion, String gameUrl) {
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        SUDRuntimeLoadGameParamModel loadGameParamModel = new SUDRuntimeLoadGameParamModel();
        loadGameParamModel.activity = activity;
        loadGameParamModel.userId = getUserId();
        loadGameParamModel.gameId = gameId;
        loadGameParamModel.pkgVersion = gamePkgVersion;
        loadGameParamModel.pkgUrl = gameUrl;
        mISUDRuntimeFSTAPP = mRuntime.loadPackage(loadGameParamModel, new ISUDRuntimeFSMGame() {
            @Override
            public void onSuccess() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                _isGameInstalled = true;
                _changeGameState(_expectGameState);
            }

            @Override
            public void onFailure(int retCode, String retMsg) {
                logE("loadGame fail(" + retCode + ")" + retMsg);
                toastMsg("loadGame fail(" + retCode + ")" + retMsg);
                delayLogin(activity, gameId, gameUrl, gamePkgVersion);
            }

            /**
             * 游戏加载进度(loadMG)
             * @param stage 阶段：start=1,loading=2,end=3
             * @param retCode 错误码：0成功
             * @param progress 进度：[0, 100]
             */
            @Override
            public void onGameLoadingProgress(int stage, int retCode, int progress) {
                progressLiveData.setValue(new ProgressModel(stage, retCode, progress));
            }
        });
    }

    private void delayCreateRuntime(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                createRuntime(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    private void loadCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        mRuntime.loadCore(null, new SUDRuntimeGameRuntime.CoreLoadListener() {
            @Override
            public void onSuccess(SUDRuntimeGameCoreHandle coreHandle) {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                mCoreHandle = coreHandle;
                loadGame(activity, gameId, gamePkgVersion, gameUrl);
                createGameInstance();
            }

            @Override
            public void onFailure(Throwable error) {
                logE("loadCore fail:" + error);
                toastMsg("loadCore fail:" + error);
                delayLoadCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        });
    }

    private void createGameInstance() {
        if (mGameHandle != null) {
            return;
        }
        // 创建游戏实例
        Bundle createOptions = new Bundle();
        createOptions.putString(SUDRuntimeGameHandle.KEY_GAME_USER_ID, getUserId());
        mRuntime.createGameHandle(mActivity, createOptions, mCoreHandle, new SUDRuntimeGameRuntime.GameHandleCreateListener() {
            @Override
            public void onSuccess(SUDRuntimeGameHandle handle) {
                mGameHandle = handle;
                onAddGameView(handle.getGameView());
                handle.setGameStateListener(_gameStateListener);
                _changeGameState(_expectGameState);
                initListener(handle);
                setMute(isMute);
                handle.getGameAudioSession().setGameQueryAudioOptionsListener(_audioListener);
                handle.setGameDrawFrameListener(new SUDRuntimeGameHandle.GameDrawFrameListener() {
                    @Override
                    public void onDrawFrame(long l) {
                        handle.setGameDrawFrameListener(null);
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                logE("createGameInstance fail:" + error);
                toastMsg("createGameInstance fail:" + error);
            }
        });
    }

    private final SUDRuntimeGameAudioSession.GameQueryAudioOptionsListener _audioListener = new SUDRuntimeGameAudioSession.GameQueryAudioOptionsListener() {
        @Override
        public void onQueryAudioOptions(SUDRuntimeGameAudioSession.GameQueryAudioOptionsHandle gameQueryAudioOptionsHandle, Bundle bundle) {
            // bundle 中参数
            // bundle.getBoolean(SUDRuntimeGameAudioSession.KEY_AUDIO_MIX_WITH_OTHER); 是否用扬声器播放，true 默认输出设备优先级：耳机 > 蓝牙 > 扬声器；false 用听筒播放
            // bundle.getBoolean(SUDRuntimeGameAudioSession.KEY_AUDIO_SPEAKER_ON); 音频是否支持与其他音频混播（包含其他应用、其他游戏实例的音频）
            if (afChangeListener == null) {
                afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                        // 不自动暂停音频
                    }
                };
                _audioManager.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
                );
            }
        }
    };

    private void initListener(SUDRuntimeGameHandle handle) {
        handle.setCustomCommandListener(new SUDRuntimeGameHandle.CustomCommandListener() {
            @Override
            public void onCallCustomCommand(SUDRuntimeGameHandle.CustomCommandHandle customCommandHandle, Bundle bundle) {
                logD("onCallCustomCommand :" + bundle);
                onGameStateChange(bundle);
                customCommandHandle.success();
            }

            @Override
            public void onCallCustomCommandSync(SUDRuntimeGameHandle.CustomCommandHandle customCommandHandle, Bundle bundle) {
                logD("onCallCustomCommandSync :" + bundle);
            }
        });
    }

    private void onGameStateChange(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String state = bundle.getString("0");
        String dataJson = bundle.getString("1");
        if ("mg_common_game_finish".equals(state)) {
            onGameMGCommonGameFinish(dataJson);
        }
    }

    private void onGameMGCommonGameFinish(String dataJson) {
        gameMGCommonGameFinishLiveData.setValue(dataJson);
    }

    private void delayLoadCore(Activity activity, String gameId, String gameUrl, String gamePkgVersion) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mGameId)) {
                    return;
                }
                loadCore(activity, gameId, gameUrl, gamePkgVersion);
            }
        }, 5000);
    }

    public void onStart() {
        _changeGameState(SUDRuntimeGameHandle.GAME_STATE_RUNNING);
    }

    public void onResume() {
        _changeGameState(SUDRuntimeGameHandle.GAME_STATE_PLAYING);
    }

    public void onPause() {
        _changeGameState(SUDRuntimeGameHandle.GAME_STATE_RUNNING);
    }

    public void onStop() {
        _changeGameState(SUDRuntimeGameHandle.GAME_STATE_WAITING);
    }

    /** 销毁游戏 */
    public void destroyGame() {
        logD("调用destroy gameId:" + mGameId);
        if (TextUtils.isEmpty(mGameId)) {
            return;
        }
        if (mGameHandle != null) {
            mGameHandle.destroy();
        }
        onRemoveGameView();
        mISUDRuntimeFSTAPP = null;
        mGameId = null;
        mGameUrl = null;
        mGamePkgVersion = null;
        mGameHandle = null;
        _isGameStateChanging = false;
        _currentGameState = SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE;
        _expectGameState = SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE;
        _isGameInstalled = false;
        isMute = false;
        isGameStarted = false;
        handler.removeCallbacksAndMessages(null);
        if (afChangeListener != null) {
            _audioManager.abandonAudioFocus(afChangeListener);
            afChangeListener = null;
        }
    }

    private final SUDRuntimeGameHandle.GameStateChangeListener _gameStateListener = new SUDRuntimeGameHandle.GameStateChangeListener() {
        @Override
        public void preStateChange(int fromState, int state) {
        }

        @Override
        public void onStateChanged(int fromState, int state) {
            logD("状态变化 gameId:" + mGameId + " 状态为：" + getStringState(state));
            if (!isGameStarted && state == SUDRuntimeGameHandle.GAME_STATE_PLAYING) {
                isGameStarted = true;
                gameStartedLiveData.setValue(null);
            }
            _currentGameState = state;
            _isGameStateChanging = false;
            _changeGameState(_expectGameState);
        }

        private String getStringState(int state) {
            switch (state) {
                case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE:
                    return "UNAVAILABLE";
                case SUDRuntimeGameHandle.GAME_STATE_WAITING:
                    return "WAITING";
                case SUDRuntimeGameHandle.GAME_STATE_RUNNING:
                    return "RUNNING";
                case SUDRuntimeGameHandle.GAME_STATE_PLAYING:
                    return "PLAYING";
                default:
                    return "UNKNOW:" + state;
            }
        }

        @Override
        public void onFailure(int fromState, int toState, Throwable error) {
            logE("game state change failed:" + " from=" + fromState + " to=" + toState + " error=" + error.getMessage());
        }
    };

    private void _changeGameState(int newState) {
        _expectGameState = newState;
        if (mGameHandle == null || !_isGameInstalled || _isGameStateChanging) {
            return;
        }
        logD("_changeGameState success: _currentGameState " + _currentGameState + " to " + newState);
        switch (_currentGameState) {
            case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE: {
                switch (newState) {
                    case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE:
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_WAITING:
                    case SUDRuntimeGameHandle.GAME_STATE_RUNNING:
                    case SUDRuntimeGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(SUDRuntimeGameHandle.KEY_GAME_START_OPTIONS_ENABLE_THIRD_SCRIPT, true);
                        mGameHandle.setGameStartOptions(mGameId, bundle);
                        mGameHandle.create();
                        break;
                }
                break;
            }
            case SUDRuntimeGameHandle.GAME_STATE_WAITING: {
                switch (newState) {
                    case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE:
                        _isGameStateChanging = true;
                        mGameHandle.destroy();
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_WAITING:
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_RUNNING:
                    case SUDRuntimeGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.start(null);
                        break;
                }
                break;
            }
            case SUDRuntimeGameHandle.GAME_STATE_RUNNING: {
                switch (newState) {
                    case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE:
                    case SUDRuntimeGameHandle.GAME_STATE_WAITING:
                        _isGameStateChanging = true;
                        mGameHandle.stop(null);
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_RUNNING:
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_PLAYING:
                        _isGameStateChanging = true;
                        mGameHandle.play();
                        break;
                }
                break;
            }
            case SUDRuntimeGameHandle.GAME_STATE_PLAYING: {
                switch (newState) {
                    case SUDRuntimeGameHandle.GAME_STATE_UNAVAILABLE:
                    case SUDRuntimeGameHandle.GAME_STATE_WAITING:
                    case SUDRuntimeGameHandle.GAME_STATE_RUNNING:
                        _isGameStateChanging = true;
                        mGameHandle.pause();
                        break;
                    case SUDRuntimeGameHandle.GAME_STATE_PLAYING:
                        break;
                }
                break;
            }
            default: {
                logE("_changeGameState fail: _currentGameState " + _currentGameState + " to " + newState);
            }
        }
    }

    public void setMute(boolean isMute) {
        this.isMute = isMute;
        if (mGameHandle != null) {
            SUDRuntimeGameAudioSession gameAudioSession = mGameHandle.getGameAudioSession();
            if (gameAudioSession != null) {
                gameAudioSession.mute(isMute);
            }
        }
    }

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
     * 将游戏View添加到页面中
     * Add the game view to the page.
     */
    protected abstract void onAddGameView(View gameView);

    /**
     * 将页面中的游戏View移除
     * Remove the game view from the page.
     */
    protected abstract void onRemoveGameView();

    /**
     * 游戏login(getCode)监听
     * Game login (getCode) listener
     */
    public interface GameGetCodeListener {
        void onSuccess(String code);

        void onFailed(int retCode, String retMsg);
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
