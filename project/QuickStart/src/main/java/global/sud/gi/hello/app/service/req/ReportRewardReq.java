package global.sud.gi.hello.app.service.req;

public class ReportRewardReq {
    public String transaction_id; // 物料下发时获取的app交易id
    public String sud_transaction_id; // app向sud op svr请求后获取到的sud交易id
    public String reward_item; // 激励类型
    public int reward_amount; // 激励数量
}
