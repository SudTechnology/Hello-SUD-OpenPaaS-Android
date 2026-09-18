package global.sud.gi.hello.app.service;

import androidx.lifecycle.LifecycleOwner;

import global.sud.gi.hello.app.common.http.param.BaseUrlManager;
import global.sud.gi.hello.app.common.http.retrofit.RetrofitManager;
import global.sud.gi.hello.app.common.http.rx.RxCallback;
import global.sud.gi.hello.app.common.http.rx.RxUtils;
import global.sud.gi.hello.app.service.req.CreateOrderReq;
import global.sud.gi.hello.app.service.req.GameLoginReq;
import global.sud.gi.hello.app.service.req.GetMeterialReq;
import global.sud.gi.hello.app.service.req.GetUserProfileReq;
import global.sud.gi.hello.app.service.req.MockPaymentReq;
import global.sud.gi.hello.app.service.req.PaymentReq;
import global.sud.gi.hello.app.service.req.QueryOrderReq;
import global.sud.gi.hello.app.service.req.ReportRewardReq;
import global.sud.gi.hello.app.service.req.RewardCompletedReq;
import global.sud.gi.hello.app.service.req.ValidatePaymentReq;
import global.sud.gi.hello.app.service.resp.CreateOrderResp;
import global.sud.gi.hello.app.service.resp.GameLoginResp;
import global.sud.gi.hello.app.service.resp.GetMeterialResp;
import global.sud.gi.hello.app.service.resp.GetUserProfileResp;
import global.sud.gi.hello.app.service.resp.MockPaymentResp;
import global.sud.gi.hello.app.service.resp.PaymentResp;
import global.sud.gi.hello.app.service.resp.QueryOrderResp;
import global.sud.gi.hello.app.service.resp.ReportRewardResp;
import global.sud.gi.hello.app.service.resp.ValidatePaymentResp;

public class MainRepository {

    private static final MainRequestMethod method = RetrofitManager.createMethod(MainRequestMethod.class);

    /**
     * 接入方客户端调用接入方服务端获取短期令牌code（getCode）
     * { 接入方服务端仓库：https://github.com/SudTechnology/hello-sud-java }
     * ------ 暂时不使用此方法，改为使用okhttp直接请求数据
     *
     * @param owner    生命周期对象
     * @param appId    SudMGP appId
     * @param userId   用户id
     * @param callback 回调
     */
    public static void login(LifecycleOwner owner, String appId, String userId, RxCallback<GameLoginResp> callback) {
        GameLoginReq req = new GameLoginReq();
        req.app_id = appId;
        req.user_id = userId;
        method.gameLogin(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void getUserProfile(LifecycleOwner owner, String appId, String userId, String encrypted_data, RxCallback<GetUserProfileResp> callback) {
        GetUserProfileReq req = new GetUserProfileReq();
        req.app_id = appId;
        req.user_id = userId;
        req.encrypted_data = encrypted_data;
        method.getUserProfile(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void mockPayment(LifecycleOwner owner, String appId, String userId, String sud_trade_no, String action, RxCallback<MockPaymentResp> callback) {
        MockPaymentReq req = new MockPaymentReq();
        req.app_id = appId;
        req.user_id = userId;
        req.sud_trade_no = sud_trade_no;
        req.action = action;
        method.mockPayment(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void validatePayment(LifecycleOwner owner, String appId, String userId, String sign_data, String signature, RxCallback<ValidatePaymentResp> callback) {
        ValidatePaymentReq req = new ValidatePaymentReq();
        req.app_id = appId;
        req.user_id = userId;
        req.sign_data = sign_data;
        req.signature = signature;
        method.validatePayment(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void createOrder(LifecycleOwner owner, String appId, String userId, String sign_data, String signature, RxCallback<CreateOrderResp> callback) {
        CreateOrderReq req = new CreateOrderReq();
        req.app_id = appId;
        req.user_id = userId;
        req.sign_data = sign_data;
        req.signature = signature;
        method.createOrder(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void payment(LifecycleOwner owner, String sud_trade_no, RxCallback<PaymentResp> callback) {
        PaymentReq req = new PaymentReq();
        req.sud_trade_no = sud_trade_no;
        method.payment(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void queryOrder(LifecycleOwner owner, String sud_trade_no, RxCallback<QueryOrderResp> callback) {
        QueryOrderReq req = new QueryOrderReq();
        req.sud_trade_no = sud_trade_no;
        method.queryOrder(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void getAdMeterial(LifecycleOwner owner, int slot_code, String user_id, String app_id, RxCallback<GetMeterialResp> callback) {
        GetMeterialReq req = new GetMeterialReq();
        req.slot_code = slot_code;
        req.user_id = user_id;
        req.app_id = app_id;
        method.getAdMeterial(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void reportReward(LifecycleOwner owner, String transaction_id, String sud_transaction_id,
                                    String reward_item, int reward_amount, RxCallback<ReportRewardResp> callback) {
        ReportRewardReq req = new ReportRewardReq();
        req.transaction_id = transaction_id;
        req.sud_transaction_id = sud_transaction_id;
        req.reward_item = reward_item;
        req.reward_amount = reward_amount;
        method.reportReward(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

    public static void rewardCompleted(LifecycleOwner owner, int status, String transaction_id,
                                       String sud_transaction_id, RxCallback<Object> callback) {
        RewardCompletedReq req = new RewardCompletedReq();
        req.status = status;
        req.transaction_id = transaction_id;
        req.sud_transaction_id = sud_transaction_id;
        method.rewardCompleted(BaseUrlManager.getBaseUrl(), req)
                .compose(RxUtils.schedulers(owner))
                .subscribe(callback);
    }

}
