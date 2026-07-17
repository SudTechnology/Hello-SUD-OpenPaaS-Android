package global.sud.gi.hello.game.ad.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MiniGameNativeAdView extends FrameLayout {

    private ImageView iconView;
    private TextView titleView;
    private TextView descView;
    private TextView ctaView;
    private TextView closeView;
    private TextView footerView;

    public MiniGameNativeAdView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        setPadding(dp(12), dp(4), dp(12), dp(6));

        // 外层深绿色背景
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#005C4E"));
        bg.setCornerRadius(dp(14));
        setBackground(bg);

        // 关闭按钮
        closeView = new TextView(context);
        closeView.setText("×");
        closeView.setTextColor(Color.WHITE);
        closeView.setTextSize(16);
        closeView.setGravity(Gravity.CENTER);
        closeView.setAlpha(0.85f);

        LayoutParams closeLp = new LayoutParams(dp(28), dp(28));
        closeLp.gravity = Gravity.RIGHT | Gravity.TOP;
        closeLp.rightMargin = dp(4);
        closeLp.topMargin = dp(0);
        addView(closeView, closeLp);

        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
            }
        });

        // 白色广告卡片
        LinearLayout cardLayout = new LinearLayout(context);
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setGravity(Gravity.CENTER_VERTICAL);
        cardLayout.setPadding(dp(10), dp(8), dp(10), dp(8));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(dp(8));
        cardLayout.setBackground(cardBg);

        LayoutParams cardLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                dp(62)
        );
        cardLp.gravity = Gravity.BOTTOM;
        cardLp.leftMargin = dp(0);
        cardLp.rightMargin = dp(0);
        cardLp.bottomMargin = dp(16);
        addView(cardLayout, cardLp);

        // 左侧广告图片
        iconView = new ImageView(context);
        iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        GradientDrawable iconBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#64C7FF"),
                        Color.parseColor("#4D8CFF")
                }
        );
        iconBg.setCornerRadius(dp(4));
        iconView.setBackground(iconBg);

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(dp(72), dp(46));
        cardLayout.addView(iconView, iconLp);

        // 中间文字区域
        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.setPadding(dp(10), 0, dp(8), 0);

        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        cardLayout.addView(textLayout, textLp);

        LinearLayout titleRow = new LinearLayout(context);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        textLayout.addView(titleRow, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(24)
        ));

        titleView = new TextView(context);
        titleView.setText("应用名称");
        titleView.setTextColor(Color.parseColor("#333333"));
        titleView.setTextSize(14);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setSingleLine(true);

        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        titleRow.addView(titleView, titleLp);

        TextView adTagView = new TextView(context);
        adTagView.setText("广告");
        adTagView.setTextColor(Color.WHITE);
        adTagView.setTextSize(9);
        adTagView.setGravity(Gravity.CENTER);
        adTagView.setPadding(dp(4), 0, dp(4), 0);

        GradientDrawable tagBg = new GradientDrawable();
        tagBg.setColor(Color.parseColor("#DADADA"));
        tagBg.setCornerRadius(dp(2));
        adTagView.setBackground(tagBg);

        LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(15)
        );
        titleRow.addView(adTagView, tagLp);

        descView = new TextView(context);
        descView.setText("应用介绍");
        descView.setTextColor(Color.parseColor("#999999"));
        descView.setTextSize(12);
        descView.setSingleLine(true);
        textLayout.addView(descView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(22)
        ));

        // 安装按钮
        ctaView = new TextView(context);
        ctaView.setText("安装");
        ctaView.setTextColor(Color.WHITE);
        ctaView.setTextSize(13);
        ctaView.setGravity(Gravity.CENTER);
        ctaView.setTypeface(Typeface.DEFAULT_BOLD);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.BLACK);
        btnBg.setCornerRadius(dp(18));
        ctaView.setBackground(btnBg);

        LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(dp(56), dp(32));
        cardLayout.addView(ctaView, btnLp);

        // 底部备案/版本/隐私等文字
        footerView = new TextView(context);
        footerView.setText("3.1.5  |  XXXX有限公司  |  隐私  |  权限");
        footerView.setTextColor(Color.parseColor("#D8D8D8"));
        footerView.setTextSize(10);
        footerView.setGravity(Gravity.CENTER);
        footerView.setSingleLine(true);

        LayoutParams footerLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                dp(16)
        );
        footerLp.gravity = Gravity.BOTTOM;
        footerLp.bottomMargin = dp(0);
        addView(footerView, footerLp);
    }

    public void setAdData(String title, String desc, Drawable icon, String ctaText) {
        if (title != null) {
            titleView.setText(title);
        }

        if (desc != null) {
            descView.setText(desc);
        }

        if (icon != null) {
            iconView.setImageDrawable(icon);
        }

        if (ctaText != null) {
            ctaView.setText(ctaText);
        }
    }

    public void setFooterText(String text) {
        if (text != null) {
            footerView.setText(text);
        }
    }

    public void setOnAdClickListener(OnClickListener listener) {
        if (listener != null) {
            ctaView.setOnClickListener(listener);
            iconView.setOnClickListener(listener);
            titleView.setOnClickListener(listener);
            descView.setOnClickListener(listener);
        }
    }

    public void setOnCloseClickListener(OnClickListener listener) {
        closeView.setOnClickListener(listener);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
