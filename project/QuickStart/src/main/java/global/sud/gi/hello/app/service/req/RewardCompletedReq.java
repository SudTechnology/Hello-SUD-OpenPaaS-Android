package global.sud.gi.hello.app.service.req;

public class RewardCompletedReq {
    /**
     * 1	COMPLETED	已完播（视频关闭且完整播放完毕）
     * 2	INCOMPLETE	未完播（视频关闭但未播放完毕）
     */
    public int status; // 广告位类型
    public String transaction_id; // 物料下发时获取的app交易id
    public String sud_transaction_id; // app向sud op svr请求后获取到的sud交易id
}
