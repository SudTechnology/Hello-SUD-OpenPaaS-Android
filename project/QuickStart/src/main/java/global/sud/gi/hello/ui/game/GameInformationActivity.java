package global.sud.gi.hello.ui.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseActivity;
import global.sud.gi.hello.common.utils.ImageLoader;
import global.sud.op.runtime.core.model.SUDOPGameInformationModel;

public class GameInformationActivity extends BaseActivity {

    public static final String EXTRA_GAME_INFO = "game_info";

    private ImageView ivBack;
    private ImageView ivGameIcon;

    private TextView tvGameName;
    private TextView tvSubjectNameTop;
    private TextView tvGameIntroduction;

    private TextView tvSubjectName;
    private TextView tvSubjectType;
    private TextView tvGameId;
    private TextView tvCategory;
    private TextView tvUpdateTime;
    private TextView tvServicePhone;
    private TextView tvServiceEmail;

    private TextView tvPrivacyContent;
    private TextView tvServiceStatement;

    private SUDOPGameInformationModel gameInfo;

    public static void start(Context context, SUDOPGameInformationModel model) {
        Intent intent = new Intent(context, GameInformationActivity.class);
        intent.putExtra(EXTRA_GAME_INFO, model);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        Object obj = getIntent().getSerializableExtra(EXTRA_GAME_INFO);
        if (obj instanceof SUDOPGameInformationModel) {
            gameInfo = (SUDOPGameInformationModel) obj;
        }
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_information;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ivBack = findViewById(R.id.iv_back);
        ivGameIcon = findViewById(R.id.iv_game_icon);

        tvGameName = findViewById(R.id.tv_game_name);
        tvSubjectNameTop = findViewById(R.id.tv_subject_name_top);
        tvGameIntroduction = findViewById(R.id.tv_game_introduction);

        tvSubjectName = findViewById(R.id.tv_subject_name);
        tvSubjectType = findViewById(R.id.tv_subject_type);
        tvGameId = findViewById(R.id.tv_game_id);
        tvCategory = findViewById(R.id.tv_category);
        tvUpdateTime = findViewById(R.id.tv_update_time);
        tvServicePhone = findViewById(R.id.tv_service_phone);
        tvServiceEmail = findViewById(R.id.tv_service_email);

        tvPrivacyContent = findViewById(R.id.tv_privacy_content);
        tvServiceStatement = findViewById(R.id.tv_service_statement);

        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void initData() {
        super.initData();
        bindData(gameInfo);
    }

    private void bindData(SUDOPGameInformationModel data) {
        if (data == null) {
            return;
        }

        String gameName = getMultiLanguageText(data.game_name);
        String introduction = getMultiLanguageText(data.game_introduction);

        tvGameName.setText(emptyToDefault(gameName, "--"));
        tvSubjectNameTop.setText(emptyToDefault(data.subject_name, "--"));
        tvGameIntroduction.setText(emptyToDefault(introduction, "--"));

        tvSubjectName.setText(emptyToDefault(data.subject_name, "--"));
        tvSubjectType.setText(getSubjectTypeText(data.subject_type));
        tvGameId.setText(emptyToDefault(data.game_id, "--"));
        tvCategory.setText(getCategoryText(data.category));
        tvUpdateTime.setText(formatTime(data.update_time));
        tvServicePhone.setText(emptyToDefault(data.service_phone, "--"));
        tvServiceEmail.setText(emptyToDefault(data.service_email, "--"));

        setPrivacyText(gameName, data.privacy_policy_url);

        tvServiceStatement.setText(
                "本服务由开发者向用户提供，开发者对服务信息内容、数据资料及其运营行为等的真实性、合法性及有效性承担全部责任。OpenPaaS向开发者提供技术支持服务。"
        );

        loadGameIcon(data.game_icon);
    }

    /**
     * 加载游戏图标。
     * <p>
     * 如果你项目里用了 Glide，可以打开下面代码：
     * <p>
     * implementation 'com.github.bumptech.glide:glide:4.16.0'
     */
    private void loadGameIcon(String iconUrl) {
        ImageLoader.loadImage(ivGameIcon, iconUrl);
    }

    private void setPrivacyText(String gameName, String privacyUrl) {
        String name = emptyToDefault(gameName, "本游戏");
        String linkText = "《" + name + "的隐私保护指引》";
        String fullText = "开发者严格按照 " + linkText + " 处理你的个人信息。";

        SpannableString spannableString = new SpannableString(fullText);

        int start = fullText.indexOf(linkText);
        int end = start + linkText.length();

        if (start >= 0) {
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (privacyUrl == null || privacyUrl.length() == 0) {
                        return;
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl));
                        startActivity(intent);
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#1989FA"));
                    ds.setUnderlineText(false);
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        tvPrivacyContent.setText(spannableString);
        tvPrivacyContent.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrivacyContent.setHighlightColor(Color.TRANSPARENT);
    }

    private String getMultiLanguageText(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        Locale locale = Locale.getDefault();

        String language = locale.getLanguage();
        String country = locale.getCountry();

        String key1 = language + "-" + country;
        String key2 = language + "_" + country;
        String key3 = language;

        if (map.containsKey(key1)) {
            return map.get(key1);
        }

        if (map.containsKey(key2)) {
            return map.get(key2);
        }

        if (map.containsKey(key3)) {
            return map.get(key3);
        }

        if (map.containsKey("zh-CN")) {
            return map.get("zh-CN");
        }

        if (map.containsKey("zh_CN")) {
            return map.get("zh_CN");
        }

        if (map.containsKey("en-US")) {
            return map.get("en-US");
        }

        for (String value : map.values()) {
            return value;
        }

        return "";
    }

    private String getSubjectTypeText(int type) {
        switch (type) {
            case 0:
                return "个人";
            case 1:
                return "企业";
            default:
                return "--";
        }
    }

    private String getCategoryText(int category) {
        switch (category) {
            case 1:
                return "角色扮演";
            case 2:
                return "经营策略";
            case 3:
                return "休闲益智";
            case 4:
                return "动作冒险";
            case 5:
                return "射击游戏";
            case 6:
                return "体育竞速";
            case 7:
                return "棋牌游戏";
            case 8:
                return "音乐舞蹈";
            default:
                return "--";
        }
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "--";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            return sdf.format(new Date(timeMillis));
        } catch (Exception e) {
            return "--";
        }
    }

    private String emptyToDefault(String value, String defaultValue) {
        if (value == null || value.trim().length() == 0) {
            return defaultValue;
        }
        return value;
    }
}
