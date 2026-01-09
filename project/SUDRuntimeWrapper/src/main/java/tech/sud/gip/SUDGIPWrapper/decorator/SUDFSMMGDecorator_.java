/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.gip.SUDGIPWrapper.decorator;

import tech.sud.gip.SUDGIPWrapper.state.SUDGIPMGState_;
import tech.sud.gip.SUDGIPWrapper.utils.ISUDFSMStateHandleUtils_;
import tech.sud.gip.SUDGIPWrapper.utils.SUDJsonUtils_;
import tech.sud.gip.core.ISUDFSMMG;
import tech.sud.gip.core.ISUDFSMStateHandle;

/**
 * ISUDFSMMG 游戏调APP回调装饰类
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISUDFSMMG.html
 */
public class SUDFSMMGDecorator_ implements ISUDFSMMG {

    // 回调
    private SUDFSMMGListener_ sudFSMMGListener;

    // 数据状态封装
    private final SUDFSMMGCache_ sudFSMMGCache = new SUDFSMMGCache_();

    /**
     * 设置回调
     *
     * @param listener 监听器
     */
    public void setSudFSMMGListener(SUDFSMMGListener_ listener) {
        sudFSMMGListener = listener;
    }

    /**
     * 游戏日志
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameLog(String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLog(dataJson);
        }
    }

    /**
     * 游戏加载进度
     *
     * @param stage    阶段：start=1,loading=2,end=3
     * @param retCode  错误码：0成功
     * @param progress 进度：[0, 100]
     */
    @Override
    public void onGameLoadingProgress(int stage, int retCode, int progress) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLoadingProgress(stage, retCode, progress);
        }
    }

    /**
     * 游戏开始
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameStarted() {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameStarted();
        }
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameDestroyed();
        }
    }

    /**
     * Code过期，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param dataJson {"code":"value"}
     */
    @Override
    public void onExpireCode(ISUDFSMStateHandle handle, String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onExpireCode(handle, dataJson);
        }
    }

    /**
     * 获取游戏View信息，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     */
    @Override
    public void onGetGameViewInfo(ISUDFSMStateHandle handle, String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameViewInfo(handle, dataJson);
        }
    }

    /**
     * 获取游戏Config，需要实现
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param dataJson {}
     *                 最低版本：v1.1.30.xx
     */
    @Override
    public void onGetGameCfg(ISUDFSMStateHandle handle, String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGetGameCfg(handle, dataJson);
        }
    }

    /**
     * 游戏状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onGameStateChange(ISUDFSMStateHandle handle, String state, String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null && listener.onGameStateChange(handle, state, dataJson)) {
            return;
        }
        switch (state) {
            case SUDGIPMGState_.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                SUDGIPMGState_.MGCommonPublicMessage mgCommonPublicMessage = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPublicMessage.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                SUDGIPMGState_.MGCommonKeyWordToHit mgCommonKeyWordToHit = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                SUDGIPMGState_.MGCommonGameSettle mgCommonGameSettle = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSettle.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                SUDGIPMGState_.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_STATE: // 10. 游戏状态
                SUDGIPMGState_.MGCommonGameState mgCommonGameState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                SUDGIPMGState_.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                SUDGIPMGState_.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                SUDGIPMGState_.MGCommonGameSoundList mgCommonGameSoundList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSoundList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                SUDGIPMGState_.MGCommonGameSound mgCommonGameSound = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSound.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                SUDGIPMGState_.MGCommonGameBgMusicState mgCommonGameBgMusicState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                SUDGIPMGState_.MGCommonGameSoundState mgCommonGameSoundState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSoundState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                SUDGIPMGState_.MGCommonGameASR mgCommonGameASR = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameASR.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                SUDGIPMGState_.MGCommonSelfMicrophone mgCommonSelfMicrophone = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                SUDGIPMGState_.MGCommonSelfHeadphone mgCommonSelfHeadphone = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                SUDGIPMGState_.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                SUDGIPMGState_.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_NETWORK_STATE: // 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
                SUDGIPMGState_.MGCommonGameNetworkState mgCommonGameNetworkState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameNetworkState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameNetworkState(handle, mgCommonGameNetworkState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_GET_SCORE: // 23. 游戏通知app获取积分
                SUDGIPMGState_.MGCommonGameGetScore mgCommonGameScore = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameGetScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameGetScore(handle, mgCommonGameScore);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SET_SCORE: // 24. 游戏通知app带入积分
                SUDGIPMGState_.MGCommonGameSetScore mgCommonGameSetScore = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSetScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSetScore(handle, mgCommonGameSetScore);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_CREATE_ORDER: // 25. 创建订单
                SUDGIPMGState_.MGCommonGameCreateOrder mgCommonGameCreateOrder = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameCreateOrder.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameCreateOrder(handle, mgCommonGameCreateOrder);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_ROLE_ID: // 26. 游戏通知app玩家角色(仅对狼人杀有效)
                SUDGIPMGState_.MGCommonPlayerRoleId mgCommonPlayerRoleId = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerRoleId.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPlayerRoleId(handle, mgCommonPlayerRoleId);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_POOP: // 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
                SUDGIPMGState_.MGCommonSelfClickPoop mgCommonSelfClickPoop = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickPoop.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickPoop(handle, mgCommonSelfClickPoop);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_GOOD: // 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
                SUDGIPMGState_.MGCommonSelfClickGood mgCommonSelfClickGood = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickGood.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGood(handle, mgCommonSelfClickGood);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_FPS: // 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
                SUDGIPMGState_.MGCommonGameFps mgCommonGameFps = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameFps.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameFps(handle, mgCommonGameFps);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_ALERT: // 30. 游戏通知app游戏弹框
                SUDGIPMGState_.MGCommonAlert mgCommonAlert = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonAlert.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAlert(handle, mgCommonAlert);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_WORST_TEAMMATE: // 31. 游戏通知app最坑队友（只支持友尽闯关）
                SUDGIPMGState_.MGCommonWorstTeammate mgCommonWorstTeammate = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonWorstTeammate.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonWorstTeammate(handle, mgCommonWorstTeammate);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_OVER_TIP: // 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
                SUDGIPMGState_.MGCommonGameOverTip mgCommonGameOverTip = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameOverTip.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameOverTip(handle, mgCommonGameOverTip);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_COLOR: // 33. 游戏通知app玩家颜色（只支持友尽闯关）
                SUDGIPMGState_.MGCommonGamePlayerColor mgCommonGamePlayerColor = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerColor.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerColor(handle, mgCommonGamePlayerColor);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_ICON_POSITION: // 34. 游戏通知app玩家头像的坐标（只支持ludo）
                SUDGIPMGState_.MGCommonGamePlayerIconPosition mgCommonGamePlayerIconPosition = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerIconPosition.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerIconPosition(handle, mgCommonGamePlayerIconPosition);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_EXIT_GAME_BTN: // 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
                SUDGIPMGState_.MGCommonSelfClickExitGameBtn mgCommonSelfClickExitGameBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickExitGameBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickExitGameBtn(handle, mgCommonSelfClickExitGameBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_IS_APP_CHIP: // 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
                SUDGIPMGState_.MGCommonGameIsAppChip mgCommonGameIsAppChip = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameIsAppChip.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameIsAppChip(handle, mgCommonGameIsAppChip);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_RULE: // 37. 游戏通知app当前游戏的设置信息
                SUDGIPMGState_.MGCommonGameRule mgCommonGameRule = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameRule.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameRule(handle, mgCommonGameRule);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SETTINGS: // 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
                SUDGIPMGState_.MGCommonGameSettings mgCommonGameSettings = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSettings.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettings(handle, mgCommonGameSettings);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_MONEY_NOT_ENOUGH: // 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
                SUDGIPMGState_.MGCommonGameMoneyNotEnough mgCommonGameMoneyNotEnough = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameMoneyNotEnough.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameMoneyNotEnough(handle, mgCommonGameMoneyNotEnough);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_UI_CUSTOM_CONFIG: // 40. 游戏通知app下发定制ui配置表（只支持ludo）
                SUDGIPMGState_.MGCommonGameUiCustomConfig mgCommonGameUiCustomConfig = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameUiCustomConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameUiCustomConfig(handle, mgCommonGameUiCustomConfig);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SET_CLICK_RECT: // 41. 设置app提供给游戏可点击区域(赛车)
                SUDGIPMGState_.MGCommonSetClickRect mgCommonSetClickRect = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSetClickRect(handle, mgCommonSetClickRect);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_USERS_INFO: // 42. 通知app提供对应uids列表玩家的数据(赛车)
                SUDGIPMGState_.MGCommonUsersInfo mgCommonUsersInfo = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonUsersInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonUsersInfo(handle, mgCommonUsersInfo);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PREPARE_FINISH: // 43. 通知app游戏前期准备完成(赛车)
                SUDGIPMGState_.MGCommonGamePrepareFinish mgCommonGamePrepareFinish = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePrepareFinish(handle, mgCommonGamePrepareFinish);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SHOW_GAME_SCENE: // 44. 通知app游戏主界面已显示(赛车)
                SUDGIPMGState_.MGCommonShowGameScene mgCommonShowGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonShowGameScene(handle, mgCommonShowGameScene);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_HIDE_GAME_SCENE: // 45. 通知app游戏主界面已隐藏(赛车)
                SUDGIPMGState_.MGCommonHideGameScene mgCommonHideGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonHideGameScene(handle, mgCommonHideGameScene);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_GOLD_BTN: // 46. 通知app点击了游戏的金币按钮(赛车)
                SUDGIPMGState_.MGCommonSelfClickGoldBtn mgCommonSelfClickGoldBtn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickGoldBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGoldBtn(handle, mgCommonSelfClickGoldBtn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PIECE_ARRIVE_END: // 47. 通知app棋子到达终点(ludo)
                SUDGIPMGState_.MGCommonGamePieceArriveEnd mgCommonGamePieceArriveEnd = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePieceArriveEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePieceArriveEnd(handle, mgCommonGamePieceArriveEnd);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_MANAGED_STATE: // 48. 通知app玩家是否托管
                SUDGIPMGState_.MGCommonGamePlayerManagedState mgCommonGamePlayerManagedState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerManagedState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerManagedState(handle, mgCommonGamePlayerManagedState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_SEND_BURST_WORD: // 49. 游戏向app发送爆词
                SUDGIPMGState_.MGCommonGameSendBurstWord mgCommonGameSendBurstWord = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameSendBurstWord.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSendBurstWord(handle, mgCommonGameSendBurstWord);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_RANKS: // 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
                SUDGIPMGState_.MGCommonGamePlayerRanks mgCommonGamePlayerRanks = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerRanks.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerRanks(handle, mgCommonGamePlayerRanks);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_PAIR_SINGULAR: // 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
                SUDGIPMGState_.MGCommonGamePlayerPairSingular mgCommonGamePlayerPairSingular = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerPairSingular.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPairSingular(handle, mgCommonGamePlayerPairSingular);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_MONOPOLY_CARDS: // 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
                SUDGIPMGState_.MGCommonGamePlayerMonopolyCards mgCommonGamePlayerMonopolyCards = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerMonopolyCards.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMonopolyCards(handle, mgCommonGamePlayerMonopolyCards);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_SCORES: // 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
                SUDGIPMGState_.MGCommonGamePlayerScores mgCommonGamePlayerScores = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerScores.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerScores(handle, mgCommonGamePlayerScores);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_DESTROY_GAME_SCENE: // 54. 游戏通知app销毁游戏（只支持部分概率类游戏）
                SUDGIPMGState_.MGCommonDestroyGameScene mgCommonDestroyGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonDestroyGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonDestroyGameScene(handle, mgCommonDestroyGameScene);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_BILLIARDS_HIT_STATE: // 55. 游戏通知app击球状态（只支持桌球）
                SUDGIPMGState_.MGCommonGameBilliardsHitState mgCommonGameBilliardsHitState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameBilliardsHitState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBilliardsHitState(handle, mgCommonGameBilliardsHitState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_PROPS_CARDS: // 56. 游戏向app发送获取玩家持有的指定点数道具卡（只支持飞行棋）
                SUDGIPMGState_.MGCommonGamePlayerPropsCards mgCommonGamePlayerPropsCards = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerPropsCards.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPropsCards(handle, mgCommonGamePlayerPropsCards);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_INFO_X: // 57. 游戏向app发送获游戏通用数据
                SUDGIPMGState_.MGCommonGameInfoX mgCommonGameInfoX = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameInfoX.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameInfoX(handle, mgCommonGameInfoX);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_AI_MODEL_MESSAGE: // 通知app开启ai大模型
                SUDGIPMGState_.MGCommonAiModelMessage mgCommonAiModelMessage = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonAiModelMessage.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiModelMessage(handle, mgCommonAiModelMessage);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_AI_MESSAGE: // 通知app ai消息
                SUDGIPMGState_.MGCommonAiMessage mgCommonAiMessage = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonAiMessage.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiMessage(handle, mgCommonAiMessage);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_AI_LARGE_SCALE_MODEL_MSG: // 64. 通知app ai大模型消息
                SUDGIPMGState_.MGCommonAiLargeScaleModelMsg mgCommonAiLargeScaleModelMsg = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonAiLargeScaleModelMsg.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAiLargeScaleModelMsg(handle, mgCommonAiLargeScaleModelMsg);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_PLAYER_MIC_STATE: // 65. 通知app可以开始推送麦克说话状态
                SUDGIPMGState_.MGCommonGamePlayerMicState mgCommonGamePlayerMicState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGamePlayerMicState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMicState(handle, mgCommonGamePlayerMicState);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_DISCO_ACTION: // 1. 元宇宙砂砂舞指令回调
                SUDGIPMGState_.MGCommonGameDiscoAction mgCommonGameDiscoAction = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameDiscoAction.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoAction(handle, mgCommonGameDiscoAction);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_DISCO_ACTION_END: // 2. 元宇宙砂砂舞指令动作结束通知
                SUDGIPMGState_.MGCommonGameDiscoActionEnd mgCommonGameDiscoActionEnd = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameDiscoActionEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoActionEnd(handle, mgCommonGameDiscoActionEnd);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_CONFIG: // 1. 礼物配置文件(火箭)
                SUDGIPMGState_.MGCustomRocketConfig mgCustomRocketConfig = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketConfig(handle, mgCustomRocketConfig);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_MODEL_LIST: // 2. 拥有模型列表(火箭)
                SUDGIPMGState_.MGCustomRocketModelList mgCustomRocketModelList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketModelList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketModelList(handle, mgCustomRocketModelList);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_COMPONENT_LIST: // 3. 拥有组件列表(火箭)
                SUDGIPMGState_.MGCustomRocketComponentList mgCustomRocketComponentList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketComponentList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketComponentList(handle, mgCustomRocketComponentList);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_USER_INFO: // 4. 获取用户信息(火箭)
                SUDGIPMGState_.MGCustomRocketUserInfo mgCustomRocketUserInfo = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketUserInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserInfo(handle, mgCustomRocketUserInfo);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_ORDER_RECORD_LIST: // 5. 订单记录列表(火箭)
                SUDGIPMGState_.MGCustomRocketOrderRecordList mgCustomRocketOrderRecordList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketOrderRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketOrderRecordList(handle, mgCustomRocketOrderRecordList);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_ROOM_RECORD_LIST: // 6. 展馆内列表(火箭)
                SUDGIPMGState_.MGCustomRocketRoomRecordList mgCustomRocketRoomRecordList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketRoomRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketRoomRecordList(handle, mgCustomRocketRoomRecordList);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_USER_RECORD_LIST: // 7. 展馆内玩家送出记录(火箭)
                SUDGIPMGState_.MGCustomRocketUserRecordList mgCustomRocketUserRecordList = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketUserRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserRecordList(handle, mgCustomRocketUserRecordList);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_SET_DEFAULT_MODEL: // 8. 设置默认模型(火箭)
                SUDGIPMGState_.MGCustomRocketSetDefaultModel mgCustomRocketSetDefaultSeat = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketSetDefaultModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetDefaultModel(handle, mgCustomRocketSetDefaultSeat);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE: // 9. 动态计算一键发送价格(火箭)
                SUDGIPMGState_.MGCustomRocketDynamicFirePrice mgCustomRocketDynamicFirePrice = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketDynamicFirePrice.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketDynamicFirePrice(handle, mgCustomRocketDynamicFirePrice);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_FIRE_MODEL: // 10. 一键发送(火箭)
                SUDGIPMGState_.MGCustomRocketFireModel mGCustomRocketFireModel = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketFireModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFireModel(handle, mGCustomRocketFireModel);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_CREATE_MODEL: // 11. 新组装模型(火箭)
                SUDGIPMGState_.MGCustomRocketCreateModel mgCustomRocketCreateModel = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketCreateModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketCreateModel(handle, mgCustomRocketCreateModel);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_REPLACE_COMPONENT: // 12. 模型更换组件(火箭)
                SUDGIPMGState_.MGCustomRocketReplaceComponent mgCustomRocketReplaceComponent = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketReplaceComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketReplaceComponent(handle, mgCustomRocketReplaceComponent);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_BUY_COMPONENT: // 13. 购买组件(火箭)
                SUDGIPMGState_.MGCustomRocketBuyComponent mgCustomRocketBuyComponent = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketBuyComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketBuyComponent(handle, mgCustomRocketBuyComponent);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_PLAY_EFFECT_START: // 14. 播放效果开始(火箭)
                SUDGIPMGState_.MGCustomRocketPlayEffectStart mgCustomRocketPlayEffectStart = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketPlayEffectStart.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectStart(handle, mgCustomRocketPlayEffectStart);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_PLAY_EFFECT_FINISH: // 15. 播放效果完成(火箭)
                SUDGIPMGState_.MGCustomRocketPlayEffectFinish mgCustomRocketPlayEffectFinish = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketPlayEffectFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectFinish(handle, mgCustomRocketPlayEffectFinish);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_VERIFY_SIGN: // 16. 验证签名合规(火箭)
                SUDGIPMGState_.MGCustomRocketVerifySign mgCustomRocketVerifySign = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketVerifySign.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketVerifySign(handle, mgCustomRocketVerifySign);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_UPLOAD_MODEL_ICON: // 17. 上传icon(火箭)
                SUDGIPMGState_.MGCustomRocketUploadModelIcon mgCustomRocketUploadModelIcon = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketUploadModelIcon.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUploadModelIcon(handle, mgCustomRocketUploadModelIcon);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_PREPARE_FINISH: // 18. 前期准备完成(火箭)
                SUDGIPMGState_.MGCustomRocketPrepareFinish mgCustomRocketPrepareFinish = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketPrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPrepareFinish(handle, mgCustomRocketPrepareFinish);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_SHOW_GAME_SCENE: // 19. 火箭主界面已显示(火箭)
                SUDGIPMGState_.MGCustomRocketShowGameScene mgCustomRocketShowGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketShowGameScene(handle, mgCustomRocketShowGameScene);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_HIDE_GAME_SCENE: // 20. 火箭主界面已隐藏(火箭)
                SUDGIPMGState_.MGCustomRocketHideGameScene mgCustomRocketHideGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketHideGameScene(handle, mgCustomRocketHideGameScene);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_CLICK_LOCK_COMPONENT: // 21. 点击锁住组件(火箭)
                SUDGIPMGState_.MGCustomRocketClickLockComponent mgCustomRocketClickLockComponent = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketClickLockComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketClickLockComponent(handle, mgCustomRocketClickLockComponent);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_FLY_CLICK: // 22. 火箭效果飞行点击(火箭)
                SUDGIPMGState_.MGCustomRocketFlyClick mgCustomRocketFlyClick = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketFlyClick.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyClick(handle, mgCustomRocketFlyClick);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_FLY_END: // 23. 火箭效果飞行结束(火箭)
                SUDGIPMGState_.MGCustomRocketFlyEnd mgCustomRocketFlyEnd = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketFlyEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyEnd(handle, mgCustomRocketFlyEnd);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_SET_CLICK_RECT: // 24. 设置点击区域(火箭)
                SUDGIPMGState_.MGCustomRocketSetClickRect mgCustomRocketSetClickRect = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetClickRect(handle, mgCustomRocketSetClickRect);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_ROCKET_SAVE_SIGN_COLOR: // 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名 模型
                SUDGIPMGState_.MGCustomRocketSaveSignColor mgCustomRocketSaveSignColor = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomRocketSaveSignColor.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSaveSignColor(handle, mgCustomRocketSaveSignColor);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_DEFUALT_STATE: // 1. 设置界面默认状态(棒球)
                SUDGIPMGState_.MGBaseballDefaultState mgBaseballDefaultState = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballDefaultState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballDefaultState(handle, mgBaseballDefaultState);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_PREPARE_FINISH: // 2. 前期准备完成(棒球)
                SUDGIPMGState_.MGBaseballPrepareFinish mgBaseballPrepareFinish = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballPrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballPrepareFinish(handle, mgBaseballPrepareFinish);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_SHOW_GAME_SCENE: // 3. 主界面已显示(棒球)
                SUDGIPMGState_.MGBaseballShowGameScene mgBaseballShowGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballShowGameScene(handle, mgBaseballShowGameScene);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_HIDE_GAME_SCENE: // 4. 主界面已隐藏(棒球)
                SUDGIPMGState_.MGBaseballHideGameScene mgBaseballHideGameScene = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballHideGameScene(handle, mgBaseballHideGameScene);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_RANKING: // 5. 查询排行榜数据(棒球)
                SUDGIPMGState_.MGBaseballRanking mgBaseballRanking = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballRanking.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRanking(handle, mgBaseballRanking);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_MY_RANKING: // 6. 查询我的排名(棒球)
                SUDGIPMGState_.MGBaseballMyRanking mgBaseballMyRanking = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballMyRanking.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballMyRanking(handle, mgBaseballMyRanking);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_RANGE_INFO: // 7. 查询当前距离我的前后玩家数据(棒球)
                SUDGIPMGState_.MGBaseballRangeInfo mgBaseballRangeInfo = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballRangeInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRangeInfo(handle, mgBaseballRangeInfo);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_SET_CLICK_RECT: // 8. 设置app提供给游戏可点击区域(棒球)
                SUDGIPMGState_.MGBaseballSetClickRect mgBaseballSetClickRect = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSetClickRect(handle, mgBaseballSetClickRect);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_TEXT_CONFIG: // 9. 获取文本配置数据(棒球)
                SUDGIPMGState_.MGBaseballTextConfig mgBaseballTextConfig = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballTextConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballTextConfig(handle, mgBaseballTextConfig);
                }
                break;
            case SUDGIPMGState_.MG_BASEBALL_SEND_DISTANCE: // 10. 球落地, 通知距离(棒球)
                SUDGIPMGState_.MGBaseballSendDistance mgBaseballSendDistance = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGBaseballSendDistance.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSendDistance(handle, mgBaseballSendDistance);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_CR_ROOM_INIT_DATA: // 1. 请求房间数据
                SUDGIPMGState_.MGCustomCrRoomInitData mgCustomCrRoomInitData = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomCrRoomInitData.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrRoomInitData(handle, mgCustomCrRoomInitData);
                }
                break;
            case SUDGIPMGState_.MG_CUSTOM_CR_CLICK_SEAT: // 2. 点击主播位或老板位通知
                SUDGIPMGState_.MGCustomCrClickSeat mgCustomCrClickSeat = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCustomCrClickSeat.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrClickSeat(handle, mgCustomCrClickSeat);
                }
                break;
            case SUDGIPMGState_.MG_HAPPY_GOAT_CHAT: // 1. 文本/语音聊天结果
                SUDGIPMGState_.MGHappyGoatChat mgHappyGoatChat = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGHappyGoatChat.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onGameMGHappyGoatChat(handle, mgHappyGoatChat);
                }
                break;
            default:
                ISUDFSMStateHandleUtils_.handleSuccess(handle);
                break;
        }
    }

    /**
     * 游戏玩家状态变化
     * APP接入方需要调用handle.success或handle.fail
     *
     * @param handle   操作
     * @param userId   用户id
     * @param state    状态命令
     * @param dataJson 状态值
     */
    @Override
    public void onPlayerStateChange(ISUDFSMStateHandle handle, String userId, String state, String dataJson) {
        SUDFSMMGListener_ listener = sudFSMMGListener;
        if (listener != null && listener.onPlayerStateChange(handle, userId, state, dataJson)) {
            return;
        }
        switch (state) {
            case SUDGIPMGState_.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                SUDGIPMGState_.MGCommonPlayerIn mgCommonPlayerIn = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                SUDGIPMGState_.MGCommonPlayerReady mgCommonPlayerReady = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                SUDGIPMGState_.MGCommonPlayerCaptain mgCommonPlayerCaptain = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                SUDGIPMGState_.MGCommonPlayerPlaying mgCommonPlayerPlaying = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                SUDGIPMGState_.MGCommonPlayerOnline mgCommonPlayerOnline = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerOnline.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                SUDGIPMGState_.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像
                SUDGIPMGState_.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                SUDGIPMGState_.MGCommonSelfDieStatus mgCommonSelfDieStatus = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                SUDGIPMGState_.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                SUDGIPMGState_.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                SUDGIPMGState_.MGCommonGameCountdownTime mgCommonGameCountdownTime = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case SUDGIPMGState_.MG_COMMON_SELF_OB_STATUS: // 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
                SUDGIPMGState_.MGCommonSelfObStatus mgCommonSelfObStatus = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGCommonSelfObStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfObStatus(handle, userId, mgCommonSelfObStatus);
                }
                break;
            case SUDGIPMGState_.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                SUDGIPMGState_.MGDGSelecting mgdgSelecting = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGDGSelecting.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case SUDGIPMGState_.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                SUDGIPMGState_.MGDGPainting mgdgPainting = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGDGPainting.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case SUDGIPMGState_.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                SUDGIPMGState_.MGDGErroranswer mgdgErroranswer = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGDGErroranswer.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case SUDGIPMGState_.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                SUDGIPMGState_.MGDGTotalscore mgdgTotalscore = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGDGTotalscore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case SUDGIPMGState_.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                SUDGIPMGState_.MGDGScore mgdgScore = SUDJsonUtils_.fromJson(dataJson, SUDGIPMGState_.MGDGScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils_.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGScore(handle, userId, mgdgScore);
                }
                break;
            default:
                ISUDFSMStateHandleUtils_.handleSuccess(handle);
                break;
        }
    }

    /** 获取队长userId */
    public String getCaptainUserId() {
        return sudFSMMGCache.getCaptainUserId();
    }

    // 返回该玩家是否正在游戏中
    public boolean playerIsPlaying(String userId) {
        return sudFSMMGCache.playerIsPlaying(userId);
    }

    // 返回该玩家是否已准备
    public boolean playerIsReady(String userId) {
        return sudFSMMGCache.playerIsReady(userId);
    }

    // 返回该玩家是否已加入了游戏
    public boolean playerIsIn(String userId) {
        return sudFSMMGCache.playerIsIn(userId);
    }

    // 获取当前游戏中的人数
    public int getPlayerInNumber() {
        return sudFSMMGCache.getPlayerInNumber();
    }

    // 是否数字炸弹
    public boolean isHitBomb() {
        return sudFSMMGCache.isHitBomb();
    }

    // 销毁游戏
    public void destroyMG() {
        sudFSMMGCache.destroyMG();
        sudFSMMGListener = null;
    }

    /**
     * 返回当前游戏的状态，数值参数{@link SUDGIPMGState_.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGCache.getGameState();
    }

    /** 获取缓存的状态 */
    public SUDFSMMGCache_ getSudFSMMGCache() {
        return sudFSMMGCache;
    }

}
