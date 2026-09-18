package global.sud.gi.hello.app.service.resp;

public class GetMeterialResp {
    public String transaction_id; // app侧的交易id，用于后续和上报的sud trans id绑定
    public String plan_id; // 广告计划id
    public String slot_code; // 广告类型
    public String title; // 广告名
    public String media_type; // 广告类型
    public String media_url; // 物料url
}
