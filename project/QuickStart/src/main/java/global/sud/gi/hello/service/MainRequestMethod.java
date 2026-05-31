package global.sud.gi.hello.service;

import global.sud.gi.hello.common.http.param.BaseResponse;
import global.sud.gi.hello.common.http.param.IBaseUrl;
import global.sud.gi.hello.service.req.CreateOrderReq;
import global.sud.gi.hello.service.req.GameLoginReq;
import global.sud.gi.hello.service.req.GetUserProfileReq;
import global.sud.gi.hello.service.req.MockPaymentReq;
import global.sud.gi.hello.service.req.PaymentReq;
import global.sud.gi.hello.service.req.QueryOrderReq;
import global.sud.gi.hello.service.req.ValidatePaymentReq;
import global.sud.gi.hello.service.resp.CreateOrderResp;
import global.sud.gi.hello.service.resp.GameLoginResp;
import global.sud.gi.hello.service.resp.GetUserProfileResp;
import global.sud.gi.hello.service.resp.MockPaymentResp;
import global.sud.gi.hello.service.resp.PaymentResp;
import global.sud.gi.hello.service.resp.QueryOrderResp;
import global.sud.gi.hello.service.resp.ValidatePaymentResp;
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

}
