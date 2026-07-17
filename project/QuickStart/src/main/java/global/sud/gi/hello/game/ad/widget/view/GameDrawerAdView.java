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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameDrawerAdView extends FrameLayout {

    private FrameLayout drawerContainer;
    private FrameLayout contentPanel;
    private FrameLayout handleView;
    private TextView handleTextView;
    private LinearLayout gridContainer;

    private int drawerTopPx = 0;

    private int contentWidthPx;
    private int handleWidthPx;
    private int drawerHeightPx;

    private boolean expanded = true;
    private boolean laidOut = false;

    private OnDrawerAdItemClickListener onDrawerAdItemClickListener;

    private final List<DrawerAdItem> adItems = new ArrayList<>();

    public GameDrawerAdView(Context context) {
        super(context);
        init();
    }

    public GameDrawerAdView(Context context, int topPx) {
        super(context);
        this.drawerTopPx = topPx;
        init();
    }

    public GameDrawerAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameDrawerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setClipChildren(false);
        setClipToPadding(false);

        // 注意：这里不要 setClickable(true)
        // 这样全屏铺满时，抽屉外区域尽量不拦截游戏自己的点击事件。
        setClickable(false);

        contentWidthPx = dp(158);
        handleWidthPx = dp(30);
        drawerHeightPx = dp(250);

        initDefaultItems();
        buildDrawer();

        post(new Runnable() {
            @Override
            public void run() {
                laidOut = true;
                applyDrawerState(false);
            }
        });
    }

    private void initDefaultItems() {
        adItems.clear();

        adItems.add(new DrawerAdItem("宇宙梦幻球", 0, Color.parseColor("#8E44AD"), "宇"));
        adItems.add(new DrawerAdItem("旋风甲壳虫", 0, Color.parseColor("#2ECC71"), "虫"));
        adItems.add(new DrawerAdItem("战区英雄", 0, Color.parseColor("#E67E22"), "战"));
        adItems.add(new DrawerAdItem("旅行世界", 0, Color.parseColor("#3498DB"), "旅"));
        adItems.add(new DrawerAdItem("超级奶牛", 0, Color.parseColor("#1ABC9C"), "牛"));
        adItems.add(new DrawerAdItem("别踩小方块", 0, Color.parseColor("#F1C40F"), "方"));
    }

    private void buildDrawer() {
        drawerContainer = new FrameLayout(getContext());
        drawerContainer.setClipChildren(false);
        drawerContainer.setClipToPadding(false);
        drawerContainer.setClickable(false);

        FrameLayout.LayoutParams drawerLp = new FrameLayout.LayoutParams(
                contentWidthPx + handleWidthPx,
                drawerHeightPx
        );
        drawerLp.gravity = Gravity.START | Gravity.TOP;
        drawerLp.topMargin = drawerTopPx;

        addView(drawerContainer, drawerLp);

        buildContentPanel();
        buildHandle();
    }

    private void buildContentPanel() {
        contentPanel = new FrameLayout(getContext());
        contentPanel.setClickable(true);
        contentPanel.setClipChildren(false);
        contentPanel.setClipToPadding(false);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#B3000000"));
        bg.setCornerRadii(new float[]{
                0, 0,
                dp(8), dp(8),
                dp(8), dp(8),
                0, 0
        });
        contentPanel.setBackground(bg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            contentPanel.setElevation(dp(6));
        }

        FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(
                contentWidthPx,
                drawerHeightPx
        );
        contentLp.gravity = Gravity.START | Gravity.TOP;

        drawerContainer.addView(contentPanel, contentLp);

        buildTitle();
        buildGrid();
    }

    private void buildTitle() {
        TextView titleView = new TextView(getContext());
        titleView.setText("精品小游戏");
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(10);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setGravity(Gravity.CENTER);
        titleView.setIncludeFontPadding(false);

        GradientDrawable titleBg = new GradientDrawable();
        titleBg.setColor(Color.parseColor("#FF6A00"));
        titleBg.setCornerRadius(dp(2));
        titleView.setBackground(titleBg);

        FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(
                dp(82),
                dp(22)
        );
        titleLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titleLp.topMargin = dp(0);

        contentPanel.addView(titleView, titleLp);
    }

    private void buildGrid() {
        gridContainer = new LinearLayout(getContext());
        gridContainer.setOrientation(LinearLayout.VERTICAL);
        gridContainer.setGravity(Gravity.CENTER);
        gridContainer.setPadding(dp(10), dp(10), dp(10), dp(8));

        FrameLayout.LayoutParams gridLp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        gridLp.topMargin = dp(24);

        contentPanel.addView(gridContainer, gridLp);

        refreshGrid();
    }

    private void refreshGrid() {
        if (gridContainer == null) {
            return;
        }

        gridContainer.removeAllViews();

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

            for (int col = 0; col < 2; col++) {
                if (index < adItems.size()) {
                    View itemView = createItemView(index, adItems.get(index));

                    LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                            0,
                            LayoutParams.MATCH_PARENT,
                            1
                    );

                    rowLayout.addView(itemView, itemLp);
                } else {
                    View emptyView = new View(getContext());

                    LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                            0,
                            LayoutParams.MATCH_PARENT,
                            1
                    );

                    rowLayout.addView(emptyView, itemLp);
                }

                index++;
            }
        }
    }

    private View createItemView(final int position, final DrawerAdItem item) {
        LinearLayout itemLayout = new LinearLayout(getContext());
        itemLayout.setOrientation(LinearLayout.VERTICAL);
        itemLayout.setGravity(Gravity.CENTER);
        itemLayout.setClickable(true);
        itemLayout.setPadding(dp(2), dp(3), dp(2), dp(2));

        FrameLayout iconWrap = new FrameLayout(getContext());

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(item.iconBgColor);
        iconBg.setCornerRadius(dp(8));
        iconWrap.setBackground(iconBg);

        LinearLayout.LayoutParams iconWrapLp = new LinearLayout.LayoutParams(
                dp(42),
                dp(42)
        );
        iconWrapLp.gravity = Gravity.CENTER_HORIZONTAL;

        itemLayout.addView(iconWrap, iconWrapLp);

        if (item.iconResId != 0) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(item.iconResId);

            FrameLayout.LayoutParams imageLp = new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );

            iconWrap.addView(imageView, imageLp);
        } else {
            TextView iconText = new TextView(getContext());
            iconText.setText(item.iconText);
            iconText.setTextColor(Color.WHITE);
            iconText.setTextSize(18);
            iconText.setTypeface(Typeface.DEFAULT_BOLD);
            iconText.setGravity(Gravity.CENTER);
            iconText.setIncludeFontPadding(false);

            FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );

            iconWrap.addView(iconText, textLp);
        }

        TextView nameView = new TextView(getContext());
        nameView.setText(item.title);
        nameView.setTextColor(Color.WHITE);
        nameView.setTextSize(9);
        nameView.setGravity(Gravity.CENTER);
        nameView.setSingleLine(true);
        nameView.setEllipsize(TextUtils.TruncateAt.END);
        nameView.setIncludeFontPadding(false);

        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                dp(18)
        );
        nameLp.topMargin = dp(3);

        itemLayout.addView(nameView, nameLp);

        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDrawerAdItemClickListener != null) {
                    onDrawerAdItemClickListener.onDrawerAdItemClick(position, item);
                }
            }
        });

        return itemLayout;
    }

    private void buildHandle() {
        handleView = new FrameLayout(getContext());
        handleView.setClickable(true);

        GradientDrawable handleBg = new GradientDrawable();
        handleBg.setColor(Color.parseColor("#B3000000"));
        handleBg.setCornerRadii(new float[]{
                0, 0,
                dp(8), dp(8),
                dp(8), dp(8),
                0, 0
        });
        handleView.setBackground(handleBg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            handleView.setElevation(dp(7));
        }

        FrameLayout.LayoutParams handleLp = new FrameLayout.LayoutParams(
                handleWidthPx,
                dp(86)
        );
        handleLp.gravity = Gravity.END | Gravity.CENTER_VERTICAL;

        drawerContainer.addView(handleView, handleLp);

        handleTextView = new TextView(getContext());
        handleTextView.setTextColor(Color.WHITE);
        handleTextView.setTextSize(11);
        handleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        handleTextView.setGravity(Gravity.CENTER);
        handleTextView.setIncludeFontPadding(false);

        FrameLayout.LayoutParams textLp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );

        handleView.addView(handleTextView, textLp);

        handleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        updateHandleText();
    }

    private void updateHandleText() {
        if (handleTextView == null) {
            return;
        }

        if (expanded) {
            handleTextView.setText("‹");
            handleTextView.setTextSize(24);
        } else {
            handleTextView.setText("更\n多\n好\n玩");
            handleTextView.setTextSize(10);
        }
    }

    public void toggle() {
        if (expanded) {
            collapse(true);
        } else {
            expand(true);
        }
    }

    public void expand() {
        expand(true);
    }

    public void collapse() {
        collapse(true);
    }

    public void expand(boolean animate) {
        expanded = true;
        applyDrawerState(animate);
    }

    public void collapse(boolean animate) {
        expanded = false;
        applyDrawerState(animate);
    }

    private void applyDrawerState(boolean animate) {
        if (drawerContainer == null) {
            return;
        }

        updateHandleText();

        float targetTranslationX = expanded ? 0 : -contentWidthPx;

        if (!laidOut || !animate) {
            drawerContainer.animate().cancel();
            drawerContainer.setTranslationX(targetTranslationX);
            return;
        }

        drawerContainer.animate()
                .translationX(targetTranslationX)
                .setDuration(220)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    public boolean isExpanded() {
        return expanded;
    }

    /**
     * 设置抽屉距离父 View 顶部的位置。
     * 单位：px。
     */
    public void setDrawerTopPx(int topPx) {
        this.drawerTopPx = topPx;

        if (drawerContainer != null) {
            LayoutParams lp = (LayoutParams) drawerContainer.getLayoutParams();
            lp.topMargin = drawerTopPx;
            drawerContainer.setLayoutParams(lp);
        }
    }

    /**
     * 设置抽屉大小。
     * 注意：这里的宽度只指左侧内容区宽度，不包含右侧把手。
     * 单位：px。
     */
    public void setDrawerSizePx(int contentWidthPx, int drawerHeightPx) {
        this.contentWidthPx = contentWidthPx;
        this.drawerHeightPx = drawerHeightPx;

        if (drawerContainer != null) {
            LayoutParams drawerLp = (LayoutParams) drawerContainer.getLayoutParams();
            drawerLp.width = this.contentWidthPx + this.handleWidthPx;
            drawerLp.height = this.drawerHeightPx;
            drawerContainer.setLayoutParams(drawerLp);
        }

        if (contentPanel != null) {
            FrameLayout.LayoutParams contentLp = (FrameLayout.LayoutParams) contentPanel.getLayoutParams();
            contentLp.width = this.contentWidthPx;
            contentLp.height = this.drawerHeightPx;
            contentPanel.setLayoutParams(contentLp);
        }

        applyDrawerState(false);
    }

    /**
     * 设置广告数据。
     * 默认展示 6 个，即 2 列 x 3 行。
     * 如果超过 6 个，只展示前 6 个。
     */
    public void setAdItems(List<DrawerAdItem> items) {
        adItems.clear();

        if (items != null) {
            int count = Math.min(items.size(), 6);
            for (int i = 0; i < count; i++) {
                adItems.add(items.get(i));
            }
        }

        refreshGrid();
    }

    public void setOnDrawerAdItemClickListener(OnDrawerAdItemClickListener listener) {
        this.onDrawerAdItemClickListener = listener;
    }

    public interface OnDrawerAdItemClickListener {
        void onDrawerAdItemClick(int position, DrawerAdItem item);
    }

    public static class DrawerAdItem {
        public String title;
        public int iconResId;
        public int iconBgColor;
        public String iconText;
        public Object data;

        public DrawerAdItem(String title, int iconResId, int iconBgColor, String iconText) {
            this.title = title;
            this.iconResId = iconResId;
            this.iconBgColor = iconBgColor;
            this.iconText = iconText;
        }

        public DrawerAdItem(String title, int iconResId, int iconBgColor, String iconText, Object data) {
            this.title = title;
            this.iconResId = iconResId;
            this.iconBgColor = iconBgColor;
            this.iconText = iconText;
            this.data = data;
        }
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    public static int dp(Context context, float value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
