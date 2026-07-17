package global.sud.gi.hello.game.ad.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GamePortalAdView extends FrameLayout {

    private FrameLayout panelView;

    private OnCloseClickListener onCloseClickListener;
    private OnAdItemClickListener onAdItemClickListener;

    private final String[] adNames = {
            "炸飞小兔兔",
            "迷你方块...",
            "装扮爱豆",
            "超级课代表",
            "寿司奥利给",
            "削木头贼6",
            "吞食天地",
            "修脚大师",
            "全民爆索..."
    };

    private final String[] iconTexts = {
            "兔",
            "方",
            "妆",
            "课",
            "寿",
            "剑",
            "鱼",
            "脚",
            "梗"
    };

    private final int[] iconColors = {
            Color.parseColor("#18B7FF"),
            Color.parseColor("#4FC3F7"),
            Color.parseColor("#F8A5D8"),
            Color.parseColor("#FF7043"),
            Color.parseColor("#FFB300"),
            Color.parseColor("#1976D2"),
            Color.parseColor("#1565C0"),
            Color.parseColor("#4FC3F7"),
            Color.parseColor("#FFD54F")
    };

    public GamePortalAdView(Context context) {
        super(context);
        init();
    }

    public GamePortalAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GamePortalAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);
        setClipChildren(false);
        setClipToPadding(false);

        // 整个 View 是铺满屏幕的，这里做半透明遮罩
        setBackgroundColor(Color.parseColor("#99000000"));

        buildPanel();
    }

    /**
     * 构建中间白色九宫格弹窗
     */
    private void buildPanel() {
        panelView = new FrameLayout(getContext());
        panelView.setClickable(true);
        panelView.setClipChildren(false);
        panelView.setClipToPadding(false);

        GradientDrawable panelBg = new GradientDrawable();
        panelBg.setColor(Color.WHITE);
        panelBg.setCornerRadius(dp(18));
        panelView.setBackground(panelBg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            panelView.setElevation(dp(10));
        }

        FrameLayout.LayoutParams panelLp = new FrameLayout.LayoutParams(
                dp(280),
                dp(305)
        );
        panelLp.gravity = Gravity.CENTER;

        addView(panelView, panelLp);

        buildTitle();
        buildCloseButton();
        buildGrid();
    }

    /**
     * 顶部黄色标题
     */
    private void buildTitle() {
        TextView titleView = new TextView(getContext());
        titleView.setText("精品小游戏推荐");
        titleView.setTextColor(Color.parseColor("#4A3A00"));
        titleView.setTextSize(13);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setGravity(Gravity.CENTER);
        titleView.setIncludeFontPadding(false);

        GradientDrawable titleBg = new GradientDrawable();
        titleBg.setColor(Color.parseColor("#FFD51F"));
        titleBg.setCornerRadius(dp(2));
        titleView.setBackground(titleBg);

        FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(
                dp(155),
                dp(25)
        );
        titleLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titleLp.topMargin = 0;

        panelView.addView(titleView, titleLp);
    }

    /**
     * 右上角关闭按钮
     */
    private void buildCloseButton() {
        TextView closeView = new TextView(getContext());
        closeView.setText("×");
        closeView.setTextColor(Color.parseColor("#D0D0D0"));
        closeView.setTextSize(24);
        closeView.setGravity(Gravity.CENTER);
        closeView.setIncludeFontPadding(false);
        closeView.setClickable(true);

        FrameLayout.LayoutParams closeLp = new FrameLayout.LayoutParams(
                dp(42),
                dp(42)
        );
        closeLp.gravity = Gravity.TOP | Gravity.END;
        closeLp.topMargin = dp(4);
        closeLp.rightMargin = dp(4);

        panelView.addView(closeView, closeLp);

        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCloseClickListener != null) {
                    onCloseClickListener.onCloseClick(GamePortalAdView.this);
                }
            }
        });
    }

    /**
     * 九宫格区域
     */
    private void buildGrid() {
        LinearLayout gridContainer = new LinearLayout(getContext());
        gridContainer.setOrientation(LinearLayout.VERTICAL);
        gridContainer.setGravity(Gravity.CENTER);
        gridContainer.setPadding(dp(20), dp(12), dp(20), dp(16));

        FrameLayout.LayoutParams gridLp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        gridLp.topMargin = dp(32);

        panelView.addView(gridContainer, gridLp);

        int index = 0;

        for (int row = 0; row < 3; row++) {
            LinearLayout rowLayout = new LinearLayout(getContext());
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    0,
                    1
            );

            gridContainer.addView(rowLayout, rowLp);

            for (int col = 0; col < 3; col++) {
                View itemView = createAdItem(index);

                LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1
                );

                rowLayout.addView(itemView, itemLp);
                index++;
            }
        }
    }

    /**
     * 单个广告资源位
     */
    private View createAdItem(final int index) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setPadding(dp(4), dp(4), dp(4), dp(2));
        itemLayout.setClickable(true);

        TextView iconView = new TextView(getContext());
        iconView.setText(iconTexts[index]);
        iconView.setTextColor(Color.WHITE);
        iconView.setTextSize(22);
        iconView.setTypeface(Typeface.DEFAULT_BOLD);
        iconView.setGravity(Gravity.CENTER);
        iconView.setIncludeFontPadding(false);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(iconColors[index]);
        iconBg.setCornerRadius(dp(10));
        iconView.setBackground(iconBg);

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                dp(52),
                dp(52)
        );
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;

        itemLayout.addView(iconView, iconLp);

        TextView nameView = new TextView(getContext());
        nameView.setText(adNames[index]);
        nameView.setTextColor(Color.parseColor("#333333"));
        nameView.setTextSize(11);
        nameView.setGravity(Gravity.CENTER);
        nameView.setSingleLine(true);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setIncludeFontPadding(false);

        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                dp(22)
        );
        nameLp.topMargin = dp(4);

        itemLayout.addView(nameView, nameLp);

        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onAdItemClickListener != null) {
                    onAdItemClickListener.onAdItemClick(index, adNames[index]);
                }
            }
        });

        return itemLayout;
    }

    /**
     * 设置弹窗在整个全屏 View 中的位置。
     * 默认是居中。
     */
    public void setPanelGravity(int gravity) {
        LayoutParams lp = (LayoutParams) panelView.getLayoutParams();
        lp.gravity = gravity;
        panelView.setLayoutParams(lp);
    }

    /**
     * 设置弹窗偏移。
     * 比如想比居中更靠上一点，可以传 yOffsetPx = -100。
     */
    public void setPanelOffset(int xOffsetPx, int yOffsetPx) {
        LayoutParams lp = (LayoutParams) panelView.getLayoutParams();
        lp.leftMargin = xOffsetPx;
        lp.topMargin = yOffsetPx;
        panelView.setLayoutParams(lp);
    }

    /**
     * 设置弹窗大小，单位 px。
     */
    public void setPanelSize(int widthPx, int heightPx) {
        LayoutParams lp = (LayoutParams) panelView.getLayoutParams();
        lp.width = widthPx;
        lp.height = heightPx;
        panelView.setLayoutParams(lp);
    }

    public void setOnCloseClickListener(OnCloseClickListener listener) {
        this.onCloseClickListener = listener;
    }

    public void setOnAdItemClickListener(OnAdItemClickListener listener) {
        this.onAdItemClickListener = listener;
    }

    public interface OnCloseClickListener {
        void onCloseClick(GamePortalAdView view);
    }

    public interface OnAdItemClickListener {
        void onAdItemClick(int position, String name);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int dp(Context context, float value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}