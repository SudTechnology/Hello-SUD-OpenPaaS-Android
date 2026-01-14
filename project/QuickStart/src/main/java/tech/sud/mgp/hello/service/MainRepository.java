package tech.sud.mgp.hello.service;

import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.List;

import tech.sud.mgp.hello.R;
import tech.sud.mgp.hello.common.http.param.BaseUrlManager;
import tech.sud.mgp.hello.common.http.retrofit.RetrofitManager;
import tech.sud.mgp.hello.common.http.rx.RxCallback;
import tech.sud.mgp.hello.common.http.rx.RxUtils;
import tech.sud.mgp.hello.ui.main.GameModel;

public class MainRepository {

    private static final MainRequestMethod method = RetrofitManager.createMethod(MainRequestMethod.class);

    /** 获取游戏列表 */
    public static List<GameModel> getRuntimeGameList() {
        ArrayList<GameModel> list = new ArrayList<>();
        list.add(buildGameModel("game.runtime1", "Runtime1", "https://hello-sud-plus.sudden.ltd/ad/resource/rungame/performance.sp",
                "1.1", R.drawable.ppwzq, R.drawable.ic_ppwzq, 1));

        list.add(buildGameModel("sud.game.flappy.bird", "Runtime2-FlappyBird", "https://hello-sud-plus.sudden.ltd/ad/resource/game/FlappyBird.cpk",
                "1.1", R.drawable.fbdr, R.drawable.ic_fbdr, 2));

        list.add(buildGameModel("sud.game.flappy.linkclear", "Runtime2-linkclear", "https://hello-sud-plus.sudden.ltd/ad/resource/game/linkclear.cpk",
                "1.6", R.drawable.nhwc, R.drawable.ic_nhwc, 2));

        list.add(buildGameModel("game.runtime1_assets", "Runtime1_assets", "ass_performance.sp",
                "1.1", R.drawable.lrs, R.drawable.ic_lrs, 1));

        list.add(buildGameModel("game.runtime2_assets", "Runtime2_assets", "ass_FlappyBird.cpk",
                "1.1", R.drawable.ddsh, R.drawable.ic_ddsh, 2));
        return list;
    }

    /** 构建GameModel */
    public static GameModel buildGameModel(String gameId, String gameName, String gameUrl, String gamePkgVersion, int homeGamePic, int gamePic, int runtime) {
        GameModel model = new GameModel();
        model.gameId = gameId;
        model.gameName = gameName;
        model.gameUrl = gameUrl;
        model.gamePkgVersion = gamePkgVersion;
        model.homeGamePic = homeGamePic;
        model.gamePic = gamePic;
        model.runtime = runtime;
        return model;
    }

    /**
     * 接入方客户端调用接入方服务端获取短期令牌code（getCode）
     * { 接入方服务端仓库：https://github.com/SudTechnology/hello-sud-java }
     * ------ 暂时不使用此方法，改为使用okhttp直接请求数据
     *
     * @param owner    生命周期对象
     * @param userId   用户id
     * @param appId    SudMGP appId
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String userId, String appId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.user_id = userId;
        req.app_id = appId;
        method.gameLogin(BaseUrlManager.getGameBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
