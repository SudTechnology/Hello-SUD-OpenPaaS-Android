package global.sud.gi.hello.game.ad.widget.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GameBannerView extends FrameLayout {

    public static final String ORIENTATION_HORIZONTAL = "horizontal";
    public static final String ORIENTATION_VERTICAL = "vertical";

    private int adLeft;
    private int adTop;
    private String orientation = ORIENTATION_HORIZONTAL;

    private final String[] names = {
            "修脚大师",
            "削木头贼6",
            "炸飞小兔兔",
            "钢琴块2",
            "超级课代表"
    };

    private final String[] icons = {
            "脚",
            "剑",
            "兔",
            "2",
            "课"
    };

    private final int[] iconColors = {
            Color.parseColor("#65C7FF"),
            Color.parseColor("#45A3FF"),
            Color.parseColor("#32D989"),
            Color.parseColor("#111827"),
            Color.parseColor("#FF6B6B")
    };

    public GameBannerView(Context context) {
        super(context);
        buildView();
    }

    public GameBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        buildView();
    }

    public GameBannerView(Context context, int left, int top, String orientation) {
        super(context);
        this.adLeft = left;
        this.adTop = top;
        this.orientation = normalizeOrientation(orientation);
        buildView();
    }

    private String normalizeOrientation(String orientation) {
        if (ORIENTATION_VERTICAL.equalsIgnoreCase(orientation)) {
            return ORIENTATION_VERTICAL;
        }
        return ORIENTATION_HORIZONTAL;
    }

    private void buildView() {
        removeAllViews();

        setClickable(true);
        setFocusable(true);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(18));
        setBackground(bg);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(dp(6));
        }

        if (ORIENTATION_VERTICAL.equals(orientation)) {
            buildVerticalBanner();
        } else {
            buildHorizontalBanner();
        }
    }

    /**
     * 横向互推盒子，类似你截图底部那种。
     */
    private void buildHorizontalBanner() {
        setPadding(dp(10), dp(0), dp(10), dp(8));

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        container.setClipToPadding(false);

        LayoutParams containerLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(container, containerLp);

        TextView title = createTitleView("精品小游戏推荐");

        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                dp(150),
                dp(22)
        );
        titleLp.gravity = Gravity.CENTER_HORIZONTAL;
        container.addView(title, titleLp);

        LinearLayout itemRow = new LinearLayout(getContext());
        itemRow.setOrientation(LinearLayout.HORIZONTAL);
        itemRow.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        );
        container.addView(itemRow, rowLp);

        for (int i = 0; i < names.length; i++) {
            View item = createHorizontalItem(i);

            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            itemRow.addView(item, itemLp);
        }
    }

    /**
     * 竖向互推盒子。
     */
    private void buildVerticalBanner() {
        setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);

        LayoutParams containerLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        addView(container, containerLp);

        TextView title = createTitleView("小游戏推荐");

        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(24)
        );
        container.addView(title, titleLp);

        for (int i = 0; i < names.length; i++) {
            View item = createVerticalItem(i);

            LinearLayout.LayoutParams itemLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1
            );
            itemLp.topMargin = dp(4);
            container.addView(item, itemLp);
        }
    }

    private TextView createTitleView(String text) {
        TextView title = new TextView(getContext());
        title.setText(text);
        title.setTextColor(Color.parseColor("#333333"));
        title.setTextSize(12);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setIncludeFontPadding(false);

        GradientDrawable titleBg = new GradientDrawable();
        titleBg.setColor(Color.parseColor("#FFD52E"));
        titleBg.setCornerRadius(dp(2));
        title.setBackground(titleBg);

        return title;
    }

    private View createHorizontalItem(final int index) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(2), dp(2), dp(2), dp(0));

        TextView icon = createIconView(index);

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                dp(40),
                dp(40)
        );
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        item.addView(icon, iconLp);

        TextView name = createNameView(names[index]);

        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(18)
        );
        nameLp.topMargin = dp(2);
        item.addView(name, nameLp);

//        item.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 这里只是演示，真实项目里可以在这里打开互推小游戏或通知游戏层
//                // Log.d("GameBannerView", "click ad item: " + names[index]);
//            }
//        });

        return item;
    }

    private View createVerticalItem(final int index) {
        LinearLayout item = new LinearLayout(getContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(2), dp(2), dp(2), dp(2));

        TextView icon = createIconView(index);

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                dp(36),
                dp(36)
        );
        item.addView(icon, iconLp);

        TextView name = createNameView(names[index]);
        name.setGravity(Gravity.CENTER_VERTICAL);
        name.setTextSize(10);

        LinearLayout.LayoutParams nameLp = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
        );
        nameLp.leftMargin = dp(5);
        item.addView(name, nameLp);

//        item.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 演示点击
//            }
//        });

        return item;
    }

    private TextView createIconView(int index) {
        TextView icon = new TextView(getContext());
        icon.setText(icons[index]);
        icon.setTextColor(Color.WHITE);
        icon.setTextSize(18);
        icon.setTypeface(Typeface.DEFAULT_BOLD);
        icon.setGravity(Gravity.CENTER);
        icon.setIncludeFontPadding(false);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(iconColors[index]);
        iconBg.setCornerRadius(dp(10));
        icon.setBackground(iconBg);

        return icon;
    }

    private TextView createNameView(String text) {
        TextView name = new TextView(getContext());
        name.setText(text);
        name.setTextColor(Color.parseColor("#333333"));
        name.setTextSize(10);
        name.setGravity(Gravity.CENTER);
        name.setSingleLine(true);
        name.setIncludeFontPadding(false);
        return name;
    }

    public int getBannerWidthPx() {
        if (ORIENTATION_VERTICAL.equals(orientation)) {
            return dp(110);
        }
        return dp(330);
    }

    public int getBannerHeightPx() {
        if (ORIENTATION_VERTICAL.equals(orientation)) {
            return dp(320);
        }
        return dp(86);
    }

    public void updatePosition(int left, int top, String orientation) {
        this.adLeft = left;
        this.adTop = top;
        this.orientation = normalizeOrientation(orientation);

        buildView();

        ViewGroup.LayoutParams params = getLayoutParams();
        if (params instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) params;
            lp.leftMargin = adLeft;
            lp.topMargin = adTop;
            lp.width = getBannerWidthPx();
            lp.height = getBannerHeightPx();
            setLayoutParams(lp);
        }
    }

    public void close() {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 方便外部直接展示。
     */
    public static GameBannerView show(Activity activity, int left, int top, String orientation) {
        FrameLayout root = activity.findViewById(android.R.id.content);

        GameBannerView bannerView = new GameBannerView(activity, left, top, orientation);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                bannerView.getBannerWidthPx(),
                bannerView.getBannerHeightPx()
        );
        lp.gravity = Gravity.START | Gravity.TOP;
        lp.leftMargin = left;
        lp.topMargin = top;

        root.addView(bannerView, lp);

        return bannerView;
    }
}
