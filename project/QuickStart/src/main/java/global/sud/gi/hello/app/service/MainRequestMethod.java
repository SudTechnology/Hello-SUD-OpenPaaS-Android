package global.sud.gi.hello.app.service;

import global.sud.gi.hello.app.common.http.param.BaseResponse;
import global.sud.gi.hello.app.common.http.param.IBaseUrl;
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
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 网络请求方法和地址
 */
public interface MainRequestMethod {

    /** 游戏登录URL */
    String GAME_LOGIN = "v1/app/generate/user/signature";

    /** 获取用户敏感信息 */
    String USER_PROFILE = "v1/app/get/user/profile";

    /** 模拟支付 */
    String MOCK_PAYMENT = "v1/app/pay/mock";

    /** 验证订单 */
    String VALIDATE_PAYMENT = "v1/app/pay/validate";

    /** 创建订单 */
    String CREATE_ORDER = "v1/app/order/create";

    /** 支付 */
    String PAYMENT = "v1/app/pay/wap/pay";

    /** 查询订单 */
    String QUERY_ORDER = "v1/app/pay/wap/query";

    /** app根据广告位请求物料 */
    String GET_AD_METERIAL = "v1/app/service/ads/material/query";

    /** 上报激励发放记录：客户端在唤起激励视频并绑定 SUD 交易后调用。 */
    String REPORT_REWARD = "v1/app/service/ads/ad-txn/reward";

    /** 完成交易：app 收到 SUD 平台事件通知后调用，将交易与激励记录更新为已完成。 */
    String REWARD_COMPLETED = "v1/app/service/ads/ad-txn/complete";

    /** 游戏登录 */
    @POST(GAME_LOGIN)
    Observable<BaseResponse<GameLoginResp>> gameLogin(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GameLoginReq req);

    /** 获取用户敏感信息 */
    @POST(USER_PROFILE)
    Observable<BaseResponse<GetUserProfileResp>> getUserProfile(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GetUserProfileReq req);

    /** 模拟支付 */
    @POST(MOCK_PAYMENT)
    Observable<BaseResponse<MockPaymentResp>> mockPayment(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body MockPaymentReq req);

    /** 验证订单 */
    @POST(VALIDATE_PAYMENT)
    Observable<BaseResponse<ValidatePaymentResp>> validatePayment(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body ValidatePaymentReq req);

    /** 创建订单 */
    @POST(CREATE_ORDER)
    Observable<BaseResponse<CreateOrderResp>> createOrder(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body CreateOrderReq req);

    /** 支付 */
    @POST(PAYMENT)
    Observable<BaseResponse<PaymentResp>> payment(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body PaymentReq req);

    /** 查询订单 */
    @POST(QUERY_ORDER)
    Observable<BaseResponse<QueryOrderResp>> queryOrder(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body QueryOrderReq req);

    /** app根据广告位请求物料 */
    @POST(GET_AD_METERIAL)
    Observable<BaseResponse<GetMeterialResp>> getAdMeterial(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body GetMeterialReq req);

    /** 上报激励发放记录：客户端在唤起激励视频并绑定 SUD 交易后调用。 */
    @POST(REPORT_REWARD)
    Observable<BaseResponse<ReportRewardResp>> reportReward(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body ReportRewardReq req);

    /** 完成交易：app 收到 SUD 平台事件通知后调用，将交易与激励记录更新为已完成。 */
    @POST(REWARD_COMPLETED)
    Observable<BaseResponse<Object>> rewardCompleted(@Header(IBaseUrl.KEY_BASE_URL) String baseUrl, @Body RewardCompletedReq req);

}
