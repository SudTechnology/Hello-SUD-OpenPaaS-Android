package global.sud.gi.hello.app.ui.game;

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

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import global.sud.gi.hello.R;
import global.sud.gi.hello.app.common.base.BaseActivity;
import global.sud.gi.hello.app.common.utils.ImageLoader;
import global.sud.gi.hello.app.common.utils.LanguageUtils;
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

        setPrivacyText(gameName, getMultiLanguageText(data.privacyPolicy));

        tvServiceStatement.setText(R.string.game_info_statement_content);

        loadGameIcon(getMultiLanguageText(data.icon));
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
        String name = emptyToDefault(gameName, getString(R.string.game_info_default_game_name));
        // FSI/PDI 让游戏名独立判断书写方向，避免被外层文案改变混排顺序。
        String isolatedName = "\u2068" + name + "\u2069";
        String linkText = getString(R.string.game_info_privacy_guide, isolatedName);
        String fullText = getString(R.string.game_info_privacy_handling, linkText);

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
        return LanguageUtils.getMultiLanguageText(context, map);
    }

    private String getSubjectTypeText(int type) {
        switch (type) {
            case 0:
                return getString(R.string.game_info_subject_personal);
            case 1:
                return getString(R.string.game_info_subject_company);
            default:
                return "--";
        }
    }

    private String getCategoryText(int category) {
        switch (category) {
            case 1:
                return getString(R.string.game_info_category_role_playing);
            case 2:
                return getString(R.string.game_info_category_strategy);
            case 3:
                return getString(R.string.game_info_category_casual);
            case 4:
                return getString(R.string.game_info_category_action);
            case 5:
                return getString(R.string.game_info_category_shooter);
            case 6:
                return getString(R.string.game_info_category_sports);
            case 7:
                return getString(R.string.game_info_category_board);
            case 8:
                return getString(R.string.game_info_category_music);
            default:
                return "--";
        }
    }

    private String formatTime(long timeMillis) {
        if (timeMillis <= 0) {
            return "--";
        }

        try {
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            return DateFormat.getDateInstance(DateFormat.MEDIUM, locale).format(new Date(timeMillis));
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
