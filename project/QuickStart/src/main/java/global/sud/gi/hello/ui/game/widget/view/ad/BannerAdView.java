package global.sud.gi.hello.ui.game.widget.view.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class BannerAdView extends View {

    private final float density;

    private final Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint imagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Paint titleTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint descTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tagTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint imageTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint buttonTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private String title = "热门游戏推荐";
    private String desc = "超高返利，轻松上手，立即体验精彩内容";
    private String buttonText = "立即下载";

    public BannerAdView(Context context) {
        this(context, null);
    }

    public BannerAdView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        initPaints();
    }

    private void initPaints() {
        backgroundPaint.setColor(Color.WHITE);
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderPaint.setColor(Color.parseColor("#DDDDDD"));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(dp(1));

        tagPaint.setColor(Color.parseColor("#FFCC00"));
        tagPaint.setStyle(Paint.Style.FILL);

        imagePaint.setColor(Color.parseColor("#EAEAEA"));
        imagePaint.setStyle(Paint.Style.FILL);

        buttonPaint.setColor(Color.parseColor("#2D8CFF"));
        buttonPaint.setStyle(Paint.Style.FILL);

        titleTextPaint.setColor(Color.parseColor("#222222"));
        titleTextPaint.setTextSize(sp(15));
        titleTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        descTextPaint.setColor(Color.parseColor("#666666"));
        descTextPaint.setTextSize(sp(12));

        tagTextPaint.setColor(Color.BLACK);
        tagTextPaint.setTextSize(sp(10));
        tagTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        tagTextPaint.setTextAlign(Paint.Align.CENTER);

        imageTextPaint.setColor(Color.parseColor("#999999"));
        imageTextPaint.setTextSize(sp(12));
        imageTextPaint.setTextAlign(Paint.Align.CENTER);

        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(sp(12));
        buttonTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultWidth = (int) dp(320);
        int defaultHeight = (int) dp(80);

        int width = resolveSize(defaultWidth, widthMeasureSpec);
        int height = resolveSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float radius = dp(10);
        float padding = dp(10);

        RectF bgRect = new RectF(0, 0, w, h);
        canvas.drawRoundRect(bgRect, radius, radius, backgroundPaint);
        canvas.drawRoundRect(bgRect, radius, radius, borderPaint);

        // AD 标签
        float tagWidth = dp(28);
        float tagHeight = dp(16);
        RectF tagRect = new RectF(padding, padding, padding + tagWidth, padding + tagHeight);
        canvas.drawRoundRect(tagRect, dp(4), dp(4), tagPaint);
        float tagTextY = tagRect.centerY() - (tagTextPaint.descent() + tagTextPaint.ascent()) / 2f;
        canvas.drawText("AD", tagRect.centerX(), tagTextY, tagTextPaint);

        // 图片区域
        float imageTop = padding + tagHeight + dp(8);
        float imageSize = h - imageTop - padding;
        RectF imageRect = new RectF(padding, imageTop, padding + imageSize, imageTop + imageSize);
        canvas.drawRoundRect(imageRect, dp(8), dp(8), imagePaint);
        float imageTextY = imageRect.centerY() - (imageTextPaint.descent() + imageTextPaint.ascent()) / 2f;
        canvas.drawText("图片", imageRect.centerX(), imageTextY, imageTextPaint);

        // 按钮区域
        float buttonWidth = dp(74);
        float buttonHeight = dp(30);
        float buttonRight = w - padding;
        float buttonLeft = buttonRight - buttonWidth;
        float buttonTop = h - padding - buttonHeight;
        RectF buttonRect = new RectF(buttonLeft, buttonTop, buttonRight, buttonTop + buttonHeight);
        canvas.drawRoundRect(buttonRect, dp(15), dp(15), buttonPaint);
        float buttonTextY = buttonRect.centerY() - (buttonTextPaint.descent() + buttonTextPaint.ascent()) / 2f;
        canvas.drawText(buttonText, buttonRect.centerX(), buttonTextY, buttonTextPaint);

        // 文本区域
        float textLeft = imageRect.right + dp(10);
        float textRight = buttonLeft - dp(10);
        float titleBaseY = imageTop + dp(16);
        float descBaseY = titleBaseY + dp(22);

        drawSingleLineEllipsizedText(canvas, title, textLeft, titleBaseY, textRight, titleTextPaint);
        drawSingleLineEllipsizedText(canvas, desc, textLeft, descBaseY, textRight, descTextPaint);
    }

    private void drawSingleLineEllipsizedText(Canvas canvas, String text, float left, float baselineY, float right, Paint paint) {
        float maxWidth = right - left;
        if (maxWidth <= 0) return;

        String drawText = text == null ? "" : text;
        if (paint.measureText(drawText) > maxWidth) {
            String ellipsis = "...";
            while (drawText.length() > 0 && paint.measureText(drawText + ellipsis) > maxWidth) {
                drawText = drawText.substring(0, drawText.length() - 1);
            }
            drawText += ellipsis;
        }
        canvas.drawText(drawText, left, baselineY, paint);
    }

    private float dp(float value) {
        return value * density;
    }

    private float sp(float value) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    // 可选：给你留几个 setter，后面改文案方便
    public void setTitle(String title) {
        this.title = title;
        invalidate();
    }

    public void setDesc(String desc) {
        this.desc = desc;
        invalidate();
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        invalidate();
    }
}
