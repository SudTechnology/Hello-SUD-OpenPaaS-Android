package global.sud.gi.hello.app.service.req;

public class GetMeterialReq {
    /**
     * REWARDED_VIDEO(0, "激励视频广告"),
     * BANNER(1, "横幅广告"),
     * INTERSTITIAL(2, "插屏广告"),
     * CUSTOM(3, "自定义广告"),
     * BOX(4, "盒子"),
     * SPLASH(5, "开屏广告");
     */
    public int slot_code; // 广告位类型
    public String user_id; // 用户id
    public String app_id;
}
