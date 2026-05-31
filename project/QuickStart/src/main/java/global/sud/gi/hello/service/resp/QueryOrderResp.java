package global.sud.gi.hello.service.resp;

public class QueryOrderResp {
    public String sud_trade_no; // SUD 交易单号
    public String trade_no; // 支付宝内部交易单号

    //    /** 1-待支付：订单创建完成，未唤起收银台/未付款 */
//    WAIT_PAY(1, "待支付"),
//    /** 2-支付处理中：已跳转收银台，用户操作中 */
//    PAY_PROCESSING(2, "支付处理中"),
//    /** 3-支付成功：扣款完成 */
//    PAY_SUCCESS(3, "支付成功"),
//    /** 4-支付失败：余额不足/密码错误/风控拦截 */
//    PAY_FAIL(4, "支付失败"),
//    /** 5-订单超时关闭：15分钟未支付自动过期 */
//    TIME_OUT_CLOSE(5, "超时关闭"),
//    /** 6-主动撤销：用户主动取消订单 */
//    USER_CANCEL(6, "主动撤销"),
//    /** 7-退款中：道具未消耗，发起退款 */
//    REFUNDING(7, "退款中"),
//    /** 8-全额退款完成 */
//    REFUND_SUCCESS(8, "退款完成");
    public int order_status; // 本地 DB 订单状态码
}
