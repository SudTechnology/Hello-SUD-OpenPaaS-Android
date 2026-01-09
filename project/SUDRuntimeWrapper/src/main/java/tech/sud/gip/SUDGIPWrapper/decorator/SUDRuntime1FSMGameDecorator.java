/*
 * Copyright © Sud.Tech
 * https://sud.tech
 */

package tech.sud.gip.SUDGIPWrapper.decorator;

import tech.sud.gip.SUDGIPWrapper.state.SUDGIPMGState;
import tech.sud.gip.SUDGIPWrapper.utils.ISUDFSMStateHandleUtils;
import tech.sud.gip.SUDGIPWrapper.utils.SUDJsonUtils;
import tech.sud.gip.core.ISUDFSMStateHandle;
import tech.sud.gip.r1.core.ISUDRuntime1FSMGame;

/**
 * ISUDFSMMG 游戏调APP回调装饰类
 * 参考文档：https://docs.sud.tech/zh-CN/app/Client/API/ISUDFSMMG.html
 */
public class SUDRuntime1FSMGameDecorator implements ISUDRuntime1FSMGame {

    // 回调
    private SUDFSMMGListener sudFSMMGListener;

    // 数据状态封装
    private final SUDFSMMGCache sudFSMMGCache = new SUDFSMMGCache();

    /**
     * 设置回调
     *
     * @param listener 监听器
     */
    public void setSudFSMMGListener(SUDFSMMGListener listener) {
        sudFSMMGListener = listener;
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
        SUDFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameLoadingProgress(stage, retCode, progress);
        }
    }

    /**
     * 游戏销毁
     * 最低版本：v1.1.30.xx
     */
    @Override
    public void onGameDestroyed() {
        SUDFSMMGListener listener = sudFSMMGListener;
        if (listener != null) {
            listener.onGameDestroyed();
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
        SUDFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onGameStateChange(handle, state, dataJson)) {
            return;
        }
        switch (state) {
            case SUDGIPMGState.MG_COMMON_PUBLIC_MESSAGE: // 1. 公屏消息
                SUDGIPMGState.MGCommonPublicMessage mgCommonPublicMessage = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPublicMessage.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPublicMessage(handle, mgCommonPublicMessage);
                }
                break;
            case SUDGIPMGState.MG_COMMON_KEY_WORD_TO_HIT: // 2. 关键词状态
                SUDGIPMGState.MGCommonKeyWordToHit mgCommonKeyWordToHit = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonKeyWordToHit.class);
                sudFSMMGCache.onGameMGCommonKeyWordToHit(mgCommonKeyWordToHit);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonKeyWordToHit(handle, mgCommonKeyWordToHit);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SETTLE: // 3. 游戏结算状态
                SUDGIPMGState.MGCommonGameSettle mgCommonGameSettle = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSettle.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettle(handle, mgCommonGameSettle);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_JOIN_BTN: // 4. 加入游戏按钮点击状态
                SUDGIPMGState.MGCommonSelfClickJoinBtn mgCommonSelfClickJoinBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickJoinBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickJoinBtn(handle, mgCommonSelfClickJoinBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_CANCEL_JOIN_BTN: // 5. 取消加入(退出)游戏按钮点击状态
                SUDGIPMGState.MGCommonSelfClickCancelJoinBtn selfClickCancelJoinBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickCancelJoinBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelJoinBtn(handle, selfClickCancelJoinBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_READY_BTN: // 6. 准备按钮点击状态
                SUDGIPMGState.MGCommonSelfClickReadyBtn mgCommonSelfClickReadyBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickReadyBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickReadyBtn(handle, mgCommonSelfClickReadyBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_CANCEL_READY_BTN: // 7. 取消准备按钮点击状态
                SUDGIPMGState.MGCommonSelfClickCancelReadyBtn mgCommonSelfClickCancelReadyBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickCancelReadyBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickCancelReadyBtn(handle, mgCommonSelfClickCancelReadyBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_START_BTN: // 8. 开始游戏按钮点击状态
                SUDGIPMGState.MGCommonSelfClickStartBtn mgCommonSelfClickStartBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickStartBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickStartBtn(handle, mgCommonSelfClickStartBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_SHARE_BTN: // 9. 分享按钮点击状态
                SUDGIPMGState.MGCommonSelfClickShareBtn mgCommonSelfClickShareBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickShareBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickShareBtn(handle, mgCommonSelfClickShareBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_STATE: // 10. 游戏状态
                SUDGIPMGState.MGCommonGameState mgCommonGameState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameState.class);
                sudFSMMGCache.onGameMGCommonGameState(mgCommonGameState);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameState(handle, mgCommonGameState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_CLOSE_BTN: // 11. 结算界面关闭按钮点击状态（2021-12-27新增）
                SUDGIPMGState.MGCommonSelfClickGameSettleCloseBtn mgCommonSelfClickGameSettleCloseBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickGameSettleCloseBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleCloseBtn(handle, mgCommonSelfClickGameSettleCloseBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_GAME_SETTLE_AGAIN_BTN: // 12. 结算界面再来一局按钮点击状态（2021-12-27新增）
                SUDGIPMGState.MGCommonSelfClickGameSettleAgainBtn mgCommonSelfClickGameSettleAgainBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickGameSettleAgainBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGameSettleAgainBtn(handle, mgCommonSelfClickGameSettleAgainBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SOUND_LIST: // 13. 游戏上报游戏中的声音列表（2021-12-30新增，现在只支持碰碰我最强）
                SUDGIPMGState.MGCommonGameSoundList mgCommonGameSoundList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSoundList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundList(handle, mgCommonGameSoundList);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SOUND: // 14. 游通知app层播放声音（2021-12-30新增，现在只支持碰碰我最强）
                SUDGIPMGState.MGCommonGameSound mgCommonGameSound = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSound.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSound(handle, mgCommonGameSound);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_BG_MUSIC_STATE: // 15. 游戏通知app层播放背景音乐状态（2022-01-07新增，现在只支持碰碰我最强）
                SUDGIPMGState.MGCommonGameBgMusicState mgCommonGameBgMusicState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameBgMusicState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBgMusicState(handle, mgCommonGameBgMusicState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SOUND_STATE: // 16. 游戏通知app层播放音效的状态（2022-01-07新增，现在只支持碰碰我最强）
                SUDGIPMGState.MGCommonGameSoundState mgCommonGameSoundState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSoundState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSoundState(handle, mgCommonGameSoundState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_ASR: // 17. ASR状态(开启和关闭语音识别状态，v1.1.45.xx 版本新增)
                SUDGIPMGState.MGCommonGameASR mgCommonGameASR = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameASR.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameASR(handle, mgCommonGameASR);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_MICROPHONE: // 18. 麦克风状态（2022-02-08新增）
                SUDGIPMGState.MGCommonSelfMicrophone mgCommonSelfMicrophone = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfMicrophone.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfMicrophone(handle, mgCommonSelfMicrophone);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_HEADPHONE: // 19. 耳机（听筒，扬声器）状态（2022-02-08新增）
                SUDGIPMGState.MGCommonSelfHeadphone mgCommonSelfHeadphone = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfHeadphone.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfHeadphone(handle, mgCommonSelfHeadphone);
                }
                break;
            case SUDGIPMGState.MG_COMMON_APP_COMMON_SELF_X_RESP: // 20. App通用状态操作结果错误码（2022-05-10新增）
                SUDGIPMGState.MGCommonAPPCommonSelfXResp mgCommonAPPCommonSelfXResp = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonAPPCommonSelfXResp.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAPPCommonSelfXResp(handle, mgCommonAPPCommonSelfXResp);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_ADD_AI_PLAYERS: // 21. 游戏通知app层添加陪玩机器人是否成功（2022-05-17新增）
                SUDGIPMGState.MGCommonGameAddAIPlayers mgCommonGameAddAIPlayers = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameAddAIPlayers.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameAddAIPlayers(handle, mgCommonGameAddAIPlayers);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_NETWORK_STATE: // 22. 游戏通知app层添当前网络连接状态（2022-06-21新增）
                SUDGIPMGState.MGCommonGameNetworkState mgCommonGameNetworkState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameNetworkState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameNetworkState(handle, mgCommonGameNetworkState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_GET_SCORE: // 23. 游戏通知app获取积分
                SUDGIPMGState.MGCommonGameGetScore mgCommonGameScore = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameGetScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameGetScore(handle, mgCommonGameScore);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SET_SCORE: // 24. 游戏通知app带入积分
                SUDGIPMGState.MGCommonGameSetScore mgCommonGameSetScore = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSetScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSetScore(handle, mgCommonGameSetScore);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_CREATE_ORDER: // 25. 创建订单
                SUDGIPMGState.MGCommonGameCreateOrder mgCommonGameCreateOrder = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameCreateOrder.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameCreateOrder(handle, mgCommonGameCreateOrder);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_ROLE_ID: // 26. 游戏通知app玩家角色(仅对狼人杀有效)
                SUDGIPMGState.MGCommonPlayerRoleId mgCommonPlayerRoleId = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerRoleId.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonPlayerRoleId(handle, mgCommonPlayerRoleId);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_POOP: // 27. 游戏通知app玩家被扔便便(你画我猜，你说我猜，友尽闯关有效)
                SUDGIPMGState.MGCommonSelfClickPoop mgCommonSelfClickPoop = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickPoop.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickPoop(handle, mgCommonSelfClickPoop);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_GOOD: // 28. 游戏通知app玩家被点赞(你画我猜，你说我猜，友尽闯关有效)
                SUDGIPMGState.MGCommonSelfClickGood mgCommonSelfClickGood = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickGood.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGood(handle, mgCommonSelfClickGood);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_FPS: // 29. 游戏通知app游戏FPS(仅对碰碰，多米诺骨牌，飞镖达人生效)
                SUDGIPMGState.MGCommonGameFps mgCommonGameFps = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameFps.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameFps(handle, mgCommonGameFps);
                }
                break;
            case SUDGIPMGState.MG_COMMON_ALERT: // 30. 游戏通知app游戏弹框
                SUDGIPMGState.MGCommonAlert mgCommonAlert = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonAlert.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonAlert(handle, mgCommonAlert);
                }
                break;
            case SUDGIPMGState.MG_COMMON_WORST_TEAMMATE: // 31. 游戏通知app最坑队友（只支持友尽闯关）
                SUDGIPMGState.MGCommonWorstTeammate mgCommonWorstTeammate = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonWorstTeammate.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonWorstTeammate(handle, mgCommonWorstTeammate);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_OVER_TIP: // 32. 游戏通知app因玩家逃跑导致游戏结束（只支持友尽闯关）
                SUDGIPMGState.MGCommonGameOverTip mgCommonGameOverTip = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameOverTip.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameOverTip(handle, mgCommonGameOverTip);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_COLOR: // 33. 游戏通知app玩家颜色（只支持友尽闯关）
                SUDGIPMGState.MGCommonGamePlayerColor mgCommonGamePlayerColor = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerColor.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerColor(handle, mgCommonGamePlayerColor);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_ICON_POSITION: // 34. 游戏通知app玩家头像的坐标（只支持ludo）
                SUDGIPMGState.MGCommonGamePlayerIconPosition mgCommonGamePlayerIconPosition = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerIconPosition.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerIconPosition(handle, mgCommonGamePlayerIconPosition);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_EXIT_GAME_BTN: // 35. 游戏通知app退出游戏（只支持teenpattipro 与 德州pro）
                SUDGIPMGState.MGCommonSelfClickExitGameBtn mgCommonSelfClickExitGameBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickExitGameBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickExitGameBtn(handle, mgCommonSelfClickExitGameBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_IS_APP_CHIP: // 36. 游戏通知app是否要开启带入积分（只支持teenpattipro 与 德州pro）
                SUDGIPMGState.MGCommonGameIsAppChip mgCommonGameIsAppChip = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameIsAppChip.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameIsAppChip(handle, mgCommonGameIsAppChip);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_RULE: // 37. 游戏通知app当前游戏的设置信息（只支持德州pro，teenpatti pro）
                SUDGIPMGState.MGCommonGameRule mgCommonGameRule = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameRule.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameRule(handle, mgCommonGameRule);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SETTINGS: // 38. 游戏通知app进行玩法设置（只支持德州pro，teenpatti pro）
                SUDGIPMGState.MGCommonGameSettings mgCommonGameSettings = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSettings.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSettings(handle, mgCommonGameSettings);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_MONEY_NOT_ENOUGH: // 39. 游戏通知app钱币不足（只支持德州pro，teenpatti pro）
                SUDGIPMGState.MGCommonGameMoneyNotEnough mgCommonGameMoneyNotEnough = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameMoneyNotEnough.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameMoneyNotEnough(handle, mgCommonGameMoneyNotEnough);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_UI_CUSTOM_CONFIG: // 40. 游戏通知app下发定制ui配置表（只支持ludo）
                SUDGIPMGState.MGCommonGameUiCustomConfig mgCommonGameUiCustomConfig = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameUiCustomConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameUiCustomConfig(handle, mgCommonGameUiCustomConfig);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SET_CLICK_RECT: // 41. 设置app提供给游戏可点击区域(赛车)
                SUDGIPMGState.MGCommonSetClickRect mgCommonSetClickRect = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSetClickRect(handle, mgCommonSetClickRect);
                }
                break;
            case SUDGIPMGState.MG_COMMON_USERS_INFO: // 42. 通知app提供对应uids列表玩家的数据(赛车)
                SUDGIPMGState.MGCommonUsersInfo mgCommonUsersInfo = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonUsersInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonUsersInfo(handle, mgCommonUsersInfo);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PREPARE_FINISH: // 43. 通知app游戏前期准备完成(赛车)
                SUDGIPMGState.MGCommonGamePrepareFinish mgCommonGamePrepareFinish = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePrepareFinish(handle, mgCommonGamePrepareFinish);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SHOW_GAME_SCENE: // 44. 通知app游戏主界面已显示(赛车)
                SUDGIPMGState.MGCommonShowGameScene mgCommonShowGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonShowGameScene(handle, mgCommonShowGameScene);
                }
                break;
            case SUDGIPMGState.MG_COMMON_HIDE_GAME_SCENE: // 45. 通知app游戏主界面已隐藏(赛车)
                SUDGIPMGState.MGCommonHideGameScene mgCommonHideGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonHideGameScene(handle, mgCommonHideGameScene);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_GOLD_BTN: // 46. 通知app点击了游戏的金币按钮(赛车)
                SUDGIPMGState.MGCommonSelfClickGoldBtn mgCommonSelfClickGoldBtn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickGoldBtn.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonSelfClickGoldBtn(handle, mgCommonSelfClickGoldBtn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PIECE_ARRIVE_END: // 47. 通知app棋子到达终点(ludo)
                SUDGIPMGState.MGCommonGamePieceArriveEnd mgCommonGamePieceArriveEnd = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePieceArriveEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePieceArriveEnd(handle, mgCommonGamePieceArriveEnd);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_MANAGED_STATE: // 48. 通知app玩家是否托管
                SUDGIPMGState.MGCommonGamePlayerManagedState mgCommonGamePlayerManagedState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerManagedState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerManagedState(handle, mgCommonGamePlayerManagedState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_SEND_BURST_WORD: // 49. 游戏向app发送爆词
                SUDGIPMGState.MGCommonGameSendBurstWord mgCommonGameSendBurstWord = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameSendBurstWord.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameSendBurstWord(handle, mgCommonGameSendBurstWord);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_RANKS: // 50. 游戏向app发送玩家实时排名（只支持怪物消消乐）
                SUDGIPMGState.MGCommonGamePlayerRanks mgCommonGamePlayerRanks = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerRanks.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerRanks(handle, mgCommonGamePlayerRanks);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_PAIR_SINGULAR: // 51. 游戏向app发送玩家即时变化的单双牌（只支持okey101）
                SUDGIPMGState.MGCommonGamePlayerPairSingular mgCommonGamePlayerPairSingular = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerPairSingular.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPairSingular(handle, mgCommonGamePlayerPairSingular);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_MONOPOLY_CARDS: // 52. 游戏向app发送获取玩家持有的道具卡（只支持大富翁）
                SUDGIPMGState.MGCommonGamePlayerMonopolyCards mgCommonGamePlayerMonopolyCards = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerMonopolyCards.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerMonopolyCards(handle, mgCommonGamePlayerMonopolyCards);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_SCORES: // 53. 游戏向app发送玩家实时积分（只支持怪物消消乐）
                SUDGIPMGState.MGCommonGamePlayerScores mgCommonGamePlayerScores = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerScores.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerScores(handle, mgCommonGamePlayerScores);
                }
                break;
            case SUDGIPMGState.MG_COMMON_DESTROY_GAME_SCENE: // 54. 游戏通知app销毁游戏（只支持部分概率类游戏）
                SUDGIPMGState.MGCommonDestroyGameScene mgCommonDestroyGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonDestroyGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonDestroyGameScene(handle, mgCommonDestroyGameScene);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_BILLIARDS_HIT_STATE: // 55. 游戏通知app击球状态（只支持桌球）
                SUDGIPMGState.MGCommonGameBilliardsHitState mgCommonGameBilliardsHitState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameBilliardsHitState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameBilliardsHitState(handle, mgCommonGameBilliardsHitState);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_PLAYER_PROPS_CARDS: // 56. 游戏向app发送获取玩家持有的指定点数道具卡（只支持飞行棋）
                SUDGIPMGState.MGCommonGamePlayerPropsCards mgCommonGamePlayerPropsCards = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGamePlayerPropsCards.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGamePlayerPropsCards(handle, mgCommonGamePlayerPropsCards);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_DISCO_ACTION: // 1. 元宇宙砂砂舞指令回调
                SUDGIPMGState.MGCommonGameDiscoAction mgCommonGameDiscoAction = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameDiscoAction.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoAction(handle, mgCommonGameDiscoAction);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_DISCO_ACTION_END: // 2. 元宇宙砂砂舞指令动作结束通知
                SUDGIPMGState.MGCommonGameDiscoActionEnd mgCommonGameDiscoActionEnd = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameDiscoActionEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCommonGameDiscoActionEnd(handle, mgCommonGameDiscoActionEnd);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_CONFIG: // 1. 礼物配置文件(火箭)
                SUDGIPMGState.MGCustomRocketConfig mgCustomRocketConfig = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketConfig(handle, mgCustomRocketConfig);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_MODEL_LIST: // 2. 拥有模型列表(火箭)
                SUDGIPMGState.MGCustomRocketModelList mgCustomRocketModelList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketModelList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketModelList(handle, mgCustomRocketModelList);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_COMPONENT_LIST: // 3. 拥有组件列表(火箭)
                SUDGIPMGState.MGCustomRocketComponentList mgCustomRocketComponentList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketComponentList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketComponentList(handle, mgCustomRocketComponentList);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_USER_INFO: // 4. 获取用户信息(火箭)
                SUDGIPMGState.MGCustomRocketUserInfo mgCustomRocketUserInfo = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketUserInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserInfo(handle, mgCustomRocketUserInfo);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_ORDER_RECORD_LIST: // 5. 订单记录列表(火箭)
                SUDGIPMGState.MGCustomRocketOrderRecordList mgCustomRocketOrderRecordList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketOrderRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketOrderRecordList(handle, mgCustomRocketOrderRecordList);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_ROOM_RECORD_LIST: // 6. 展馆内列表(火箭)
                SUDGIPMGState.MGCustomRocketRoomRecordList mgCustomRocketRoomRecordList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketRoomRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketRoomRecordList(handle, mgCustomRocketRoomRecordList);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_USER_RECORD_LIST: // 7. 展馆内玩家送出记录(火箭)
                SUDGIPMGState.MGCustomRocketUserRecordList mgCustomRocketUserRecordList = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketUserRecordList.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUserRecordList(handle, mgCustomRocketUserRecordList);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_SET_DEFAULT_MODEL: // 8. 设置默认模型(火箭)
                SUDGIPMGState.MGCustomRocketSetDefaultModel mgCustomRocketSetDefaultSeat = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketSetDefaultModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetDefaultModel(handle, mgCustomRocketSetDefaultSeat);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_DYNAMIC_FIRE_PRICE: // 9. 动态计算一键发送价格(火箭)
                SUDGIPMGState.MGCustomRocketDynamicFirePrice mgCustomRocketDynamicFirePrice = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketDynamicFirePrice.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketDynamicFirePrice(handle, mgCustomRocketDynamicFirePrice);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_FIRE_MODEL: // 10. 一键发送(火箭)
                SUDGIPMGState.MGCustomRocketFireModel mGCustomRocketFireModel = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketFireModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFireModel(handle, mGCustomRocketFireModel);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_CREATE_MODEL: // 11. 新组装模型(火箭)
                SUDGIPMGState.MGCustomRocketCreateModel mgCustomRocketCreateModel = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketCreateModel.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketCreateModel(handle, mgCustomRocketCreateModel);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_REPLACE_COMPONENT: // 12. 模型更换组件(火箭)
                SUDGIPMGState.MGCustomRocketReplaceComponent mgCustomRocketReplaceComponent = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketReplaceComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketReplaceComponent(handle, mgCustomRocketReplaceComponent);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_BUY_COMPONENT: // 13. 购买组件(火箭)
                SUDGIPMGState.MGCustomRocketBuyComponent mgCustomRocketBuyComponent = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketBuyComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketBuyComponent(handle, mgCustomRocketBuyComponent);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_START: // 14. 播放效果开始(火箭)
                SUDGIPMGState.MGCustomRocketPlayEffectStart mgCustomRocketPlayEffectStart = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketPlayEffectStart.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectStart(handle, mgCustomRocketPlayEffectStart);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_PLAY_EFFECT_FINISH: // 15. 播放效果完成(火箭)
                SUDGIPMGState.MGCustomRocketPlayEffectFinish mgCustomRocketPlayEffectFinish = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketPlayEffectFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPlayEffectFinish(handle, mgCustomRocketPlayEffectFinish);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_VERIFY_SIGN: // 16. 验证签名合规(火箭)
                SUDGIPMGState.MGCustomRocketVerifySign mgCustomRocketVerifySign = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketVerifySign.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketVerifySign(handle, mgCustomRocketVerifySign);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_UPLOAD_MODEL_ICON: // 17. 上传icon(火箭)
                SUDGIPMGState.MGCustomRocketUploadModelIcon mgCustomRocketUploadModelIcon = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketUploadModelIcon.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketUploadModelIcon(handle, mgCustomRocketUploadModelIcon);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_PREPARE_FINISH: // 18. 前期准备完成(火箭)
                SUDGIPMGState.MGCustomRocketPrepareFinish mgCustomRocketPrepareFinish = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketPrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketPrepareFinish(handle, mgCustomRocketPrepareFinish);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_SHOW_GAME_SCENE: // 19. 火箭主界面已显示(火箭)
                SUDGIPMGState.MGCustomRocketShowGameScene mgCustomRocketShowGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketShowGameScene(handle, mgCustomRocketShowGameScene);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_HIDE_GAME_SCENE: // 20. 火箭主界面已隐藏(火箭)
                SUDGIPMGState.MGCustomRocketHideGameScene mgCustomRocketHideGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketHideGameScene(handle, mgCustomRocketHideGameScene);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_CLICK_LOCK_COMPONENT: // 21. 点击锁住组件(火箭)
                SUDGIPMGState.MGCustomRocketClickLockComponent mgCustomRocketClickLockComponent = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketClickLockComponent.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketClickLockComponent(handle, mgCustomRocketClickLockComponent);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_FLY_CLICK: // 22. 火箭效果飞行点击(火箭)
                SUDGIPMGState.MGCustomRocketFlyClick mgCustomRocketFlyClick = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketFlyClick.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyClick(handle, mgCustomRocketFlyClick);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_FLY_END: // 23. 火箭效果飞行结束(火箭)
                SUDGIPMGState.MGCustomRocketFlyEnd mgCustomRocketFlyEnd = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketFlyEnd.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketFlyEnd(handle, mgCustomRocketFlyEnd);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_SET_CLICK_RECT: // 24. 设置点击区域(火箭)
                SUDGIPMGState.MGCustomRocketSetClickRect mgCustomRocketSetClickRect = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSetClickRect(handle, mgCustomRocketSetClickRect);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_ROCKET_SAVE_SIGN_COLOR: // 25. 颜色和签名自定义改到装配间的模式，保存颜色或签名 模型
                SUDGIPMGState.MGCustomRocketSaveSignColor mgCustomRocketSaveSignColor = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomRocketSaveSignColor.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomRocketSaveSignColor(handle, mgCustomRocketSaveSignColor);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_DEFUALT_STATE: // 1. 设置界面默认状态(棒球)
                SUDGIPMGState.MGBaseballDefaultState mgBaseballDefaultState = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballDefaultState.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballDefaultState(handle, mgBaseballDefaultState);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_PREPARE_FINISH: // 2. 前期准备完成(棒球)
                SUDGIPMGState.MGBaseballPrepareFinish mgBaseballPrepareFinish = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballPrepareFinish.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballPrepareFinish(handle, mgBaseballPrepareFinish);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_SHOW_GAME_SCENE: // 3. 主界面已显示(棒球)
                SUDGIPMGState.MGBaseballShowGameScene mgBaseballShowGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballShowGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballShowGameScene(handle, mgBaseballShowGameScene);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_HIDE_GAME_SCENE: // 4. 主界面已隐藏(棒球)
                SUDGIPMGState.MGBaseballHideGameScene mgBaseballHideGameScene = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballHideGameScene.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballHideGameScene(handle, mgBaseballHideGameScene);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_RANKING: // 5. 查询排行榜数据(棒球)
                SUDGIPMGState.MGBaseballRanking mgBaseballRanking = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballRanking.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRanking(handle, mgBaseballRanking);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_MY_RANKING: // 6. 查询我的排名(棒球)
                SUDGIPMGState.MGBaseballMyRanking mgBaseballMyRanking = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballMyRanking.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballMyRanking(handle, mgBaseballMyRanking);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_RANGE_INFO: // 7. 查询当前距离我的前后玩家数据(棒球)
                SUDGIPMGState.MGBaseballRangeInfo mgBaseballRangeInfo = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballRangeInfo.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballRangeInfo(handle, mgBaseballRangeInfo);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_SET_CLICK_RECT: // 8. 设置app提供给游戏可点击区域(棒球)
                SUDGIPMGState.MGBaseballSetClickRect mgBaseballSetClickRect = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballSetClickRect.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSetClickRect(handle, mgBaseballSetClickRect);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_TEXT_CONFIG: // 9. 获取文本配置数据(棒球)
                SUDGIPMGState.MGBaseballTextConfig mgBaseballTextConfig = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballTextConfig.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballTextConfig(handle, mgBaseballTextConfig);
                }
                break;
            case SUDGIPMGState.MG_BASEBALL_SEND_DISTANCE: // 10. 球落地, 通知距离(棒球)
                SUDGIPMGState.MGBaseballSendDistance mgBaseballSendDistance = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGBaseballSendDistance.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGBaseballSendDistance(handle, mgBaseballSendDistance);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_CR_ROOM_INIT_DATA: // 1. 请求房间数据
                SUDGIPMGState.MGCustomCrRoomInitData mgCustomCrRoomInitData = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomCrRoomInitData.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrRoomInitData(handle, mgCustomCrRoomInitData);
                }
                break;
            case SUDGIPMGState.MG_CUSTOM_CR_CLICK_SEAT: // 2. 点击主播位或老板位通知
                SUDGIPMGState.MGCustomCrClickSeat mgCustomCrClickSeat = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCustomCrClickSeat.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onGameMGCustomCrClickSeat(handle, mgCustomCrClickSeat);
                }
                break;
            default:
                ISUDFSMStateHandleUtils.handleSuccess(handle);
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
        SUDFSMMGListener listener = sudFSMMGListener;
        if (listener != null && listener.onPlayerStateChange(handle, userId, state, dataJson)) {
            return;
        }
        switch (state) {
            case SUDGIPMGState.MG_COMMON_PLAYER_IN: // 1.加入状态（已修改）
                SUDGIPMGState.MGCommonPlayerIn mgCommonPlayerIn = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerIn.class);
                sudFSMMGCache.onPlayerMGCommonPlayerIn(userId, mgCommonPlayerIn);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerIn(handle, userId, mgCommonPlayerIn);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_READY: // 2.准备状态（已修改）
                SUDGIPMGState.MGCommonPlayerReady mgCommonPlayerReady = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerReady.class);
                sudFSMMGCache.onPlayerMGCommonPlayerReady(userId, mgCommonPlayerReady);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerReady(handle, userId, mgCommonPlayerReady);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_CAPTAIN: // 3.队长状态（已修改）
                SUDGIPMGState.MGCommonPlayerCaptain mgCommonPlayerCaptain = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerCaptain.class);
                sudFSMMGCache.onPlayerMGCommonPlayerCaptain(userId, mgCommonPlayerCaptain);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerCaptain(handle, userId, mgCommonPlayerCaptain);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_PLAYING: // 4.游戏状态（已修改）
                SUDGIPMGState.MGCommonPlayerPlaying mgCommonPlayerPlaying = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerPlaying.class);
                sudFSMMGCache.onPlayerMGCommonPlayerPlaying(userId, mgCommonPlayerPlaying);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerPlaying(handle, userId, mgCommonPlayerPlaying);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_ONLINE: // 5.玩家在线状态
                SUDGIPMGState.MGCommonPlayerOnline mgCommonPlayerOnline = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerOnline.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerOnline(handle, userId, mgCommonPlayerOnline);
                }
                break;
            case SUDGIPMGState.MG_COMMON_PLAYER_CHANGE_SEAT: // 6.玩家换游戏位状态
                SUDGIPMGState.MGCommonPlayerChangeSeat mgCommonPlayerChangeSeat = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonPlayerChangeSeat.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonPlayerChangeSeat(handle, userId, mgCommonPlayerChangeSeat);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_CLICK_GAME_PLAYER_ICON: // 7. 游戏通知app点击玩家头像
                SUDGIPMGState.MGCommonSelfClickGamePlayerIcon mgCommonSelfClickGamePlayerIcon = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfClickGamePlayerIcon.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfClickGamePlayerIcon(handle, userId, mgCommonSelfClickGamePlayerIcon);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_DIE_STATUS: // 8. 游戏通知app玩家死亡状态（2022-04-24新增）
                SUDGIPMGState.MGCommonSelfDieStatus mgCommonSelfDieStatus = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfDieStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfDieStatus(handle, userId, mgCommonSelfDieStatus);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_TURN_STATUS: // 9. 游戏通知app轮到玩家出手状态（2022-04-24新增）
                SUDGIPMGState.MGCommonSelfTurnStatus mgCommonSelfTurnStatus = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfTurnStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfTurnStatus(handle, userId, mgCommonSelfTurnStatus);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_SELECT_STATUS: // 10. 游戏通知app玩家选择状态（2022-04-24新增）
                SUDGIPMGState.MGCommonSelfSelectStatus mgCommonSelfSelectStatus = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfSelectStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfSelectStatus(handle, userId, mgCommonSelfSelectStatus);
                }
                break;
            case SUDGIPMGState.MG_COMMON_GAME_COUNTDOWN_TIME: // 11. 游戏通知app层当前游戏剩余时间（2022-05-23新增，目前UMO生效）
                SUDGIPMGState.MGCommonGameCountdownTime mgCommonGameCountdownTime = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonGameCountdownTime.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonGameCountdownTime(handle, userId, mgCommonGameCountdownTime);
                }
                break;
            case SUDGIPMGState.MG_COMMON_SELF_OB_STATUS: // 12. 游戏通知app层当前玩家死亡后变成ob视角（2022-08-23新增，目前狼人杀生效）
                SUDGIPMGState.MGCommonSelfObStatus mgCommonSelfObStatus = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGCommonSelfObStatus.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGCommonSelfObStatus(handle, userId, mgCommonSelfObStatus);
                }
                break;
            case SUDGIPMGState.MG_DG_SELECTING: // 1. 选词中状态（已修改）
                SUDGIPMGState.MGDGSelecting mgdgSelecting = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGDGSelecting.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGSelecting(handle, userId, mgdgSelecting);
                }
                break;
            case SUDGIPMGState.MG_DG_PAINTING: // 2. 作画中状态（已修改）
                SUDGIPMGState.MGDGPainting mgdgPainting = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGDGPainting.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGPainting(handle, userId, mgdgPainting);
                }
                break;
            case SUDGIPMGState.MG_DG_ERRORANSWER: // 3. 显示错误答案状态（已修改）
                SUDGIPMGState.MGDGErroranswer mgdgErroranswer = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGDGErroranswer.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGErroranswer(handle, userId, mgdgErroranswer);
                }
                break;
            case SUDGIPMGState.MG_DG_TOTALSCORE: // 4. 显示总积分状态（已修改）
                SUDGIPMGState.MGDGTotalscore mgdgTotalscore = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGDGTotalscore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGTotalscore(handle, userId, mgdgTotalscore);
                }
                break;
            case SUDGIPMGState.MG_DG_SCORE: // 5. 本次获得积分状态（已修改）
                SUDGIPMGState.MGDGScore mgdgScore = SUDJsonUtils.fromJson(dataJson, SUDGIPMGState.MGDGScore.class);
                if (listener == null) {
                    ISUDFSMStateHandleUtils.handleSuccess(handle);
                } else {
                    listener.onPlayerMGDGScore(handle, userId, mgdgScore);
                }
                break;
            default:
                ISUDFSMStateHandleUtils.handleSuccess(handle);
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
    public void destroyGame() {
        sudFSMMGCache.destroyMG();
        sudFSMMGListener = null;
    }

    /**
     * 返回当前游戏的状态，数值参数{@link SUDGIPMGState.MGCommonGameState}
     */
    public int getGameState() {
        return sudFSMMGCache.getGameState();
    }

    /** 获取缓存的状态 */
    public SUDFSMMGCache getSudFSMMGCache() {
        return sudFSMMGCache;
    }

}
