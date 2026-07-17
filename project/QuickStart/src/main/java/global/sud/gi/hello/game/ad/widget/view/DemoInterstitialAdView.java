package global.sud.gi.hello.game.ad.widget.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DemoInterstitialAdView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF cardRect = new RectF();
    private final RectF imageRect = new RectF();
    private final RectF buttonRect = new RectF();
    private final RectF closeRect = new RectF();

    private final Path cardPath = new Path();

    private OnAdClickListener listener;

    private boolean dismissed = false;

    private final String title = "红果免费短剧";
    private final String desc = "畅享海量短剧，尽在红果免费短剧";
    private final String buttonText = "立即下载";

    public DemoInterstitialAdView(Context context) {
        super(context);
        init();
    }

    public DemoInterstitialAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoInterstitialAdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setClickable(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        // 半透明遮罩
        canvas.drawColor(Color.argb(165, 0, 0, 0));

//        drawTopFakeUi(canvas, w);

        calculateLayout(w, h);

        drawAdCard(canvas);
        drawCloseButton(canvas);
        drawBottomText(canvas, w, h);
    }

    private void calculateLayout(int w, int h) {
        float cardW = Math.min(dp(288), w - dp(48));
        float cardH = dp(442);

        if (h < dp(620)) {
            cardH = Math.min(dp(400), h - dp(140));
        }

        float left = (w - cardW) / 2f;
        float top = (h - cardH) / 2f - dp(20);

        if (top < dp(70)) {
            top = dp(70);
        }

        cardRect.set(left, top, left + cardW, top + cardH);

        imageRect.set(
                cardRect.left,
                cardRect.top,
                cardRect.right,
                cardRect.top + dp(200)
        );

        float btnW = cardW - dp(52);
        float btnH = dp(46);
        float btnLeft = cardRect.left + (cardW - btnW) / 2f;
        float btnTop = cardRect.bottom - dp(74);

        buttonRect.set(
                btnLeft,
                btnTop,
                btnLeft + btnW,
                btnTop + btnH
        );

        float closeSize = dp(42);
        float closeLeft = cardRect.centerX() - closeSize / 2f;
        float closeTop = cardRect.bottom + dp(32);

        closeRect.set(
                closeLeft,
                closeTop,
                closeLeft + closeSize,
                closeTop + closeSize
        );
    }

    private void drawAdCard(Canvas canvas) {
        float radius = dp(16);

        // 阴影
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(70, 0, 0, 0));
        paint.setShadowLayer(dp(18), 0, dp(6), Color.argb(90, 0, 0, 0));
        canvas.drawRoundRect(cardRect, radius, radius, paint);
        paint.clearShadowLayer();

        // 卡片路径，用于裁剪内容
        cardPath.reset();
        cardPath.addRoundRect(cardRect, radius, radius, Path.Direction.CW);

        canvas.save();
        canvas.clipPath(cardPath);

        // 卡片底色
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(211, 177, 164));
        canvas.drawRoundRect(cardRect, radius, radius, paint);

        // 顶部假图片
        drawFakeImage(canvas);

        // 图片到底部的柔和渐变遮罩
        Paint fadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient gradient = new LinearGradient(
                0,
                cardRect.top + dp(120),
                0,
                cardRect.top + dp(300),
                new int[]{
                        Color.argb(0, 211, 177, 164),
                        Color.argb(230, 211, 177, 164),
                        Color.rgb(211, 177, 164)
                },
                new float[]{0f, 0.58f, 1f},
                Shader.TileMode.CLAMP
        );
        fadePaint.setShader(gradient);
        canvas.drawRect(
                cardRect.left,
                cardRect.top + dp(100),
                cardRect.right,
                cardRect.bottom,
                fadePaint
        );

        canvas.restore();

        drawAppIcon(canvas);
        drawTexts(canvas);
        drawDownloadButton(canvas);
    }

    private void drawFakeImage(Canvas canvas) {
        // 背景
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb(250, 245, 240));
        canvas.drawRect(imageRect, paint);

        // 右侧窗帘
        paint.setColor(Color.rgb(255, 205, 210));
        canvas.drawRect(
                imageRect.right - dp(52),
                imageRect.top,
                imageRect.right,
                imageRect.bottom,
                paint
        );

        paint.setColor(Color.argb(90, 255, 255, 255));
        for (int i = 0; i < 4; i++) {
            float x = imageRect.right - dp(48) + i * dp(12);
            canvas.drawRect(x, imageRect.top, x + dp(5), imageRect.bottom, paint);
        }

        // 沙发
        paint.setColor(Color.rgb(238, 218, 202));
        RectF sofa = new RectF(
                imageRect.left + dp(20),
                imageRect.top + dp(82),
                imageRect.right - dp(26),
                imageRect.top + dp(165)
        );
        canvas.drawRoundRect(sofa, dp(26), dp(26), paint);

        paint.setColor(Color.rgb(246, 160, 150));
        RectF pillow1 = new RectF(
                sofa.left + dp(42),
                sofa.top + dp(20),
                sofa.left + dp(88),
                sofa.top + dp(66)
        );
        canvas.drawRoundRect(pillow1, dp(10), dp(10), paint);

        paint.setColor(Color.rgb(255, 185, 190));
        RectF pillow2 = new RectF(
                sofa.right - dp(78),
                sofa.top + dp(18),
                sofa.right - dp(28),
                sofa.top + dp(64)
        );
        canvas.drawRoundRect(pillow2, dp(10), dp(10), paint);

        // 人物头发
        paint.setColor(Color.rgb(91, 35, 20));
        RectF hair = new RectF(
                imageRect.centerX() - dp(36),
                imageRect.top + dp(18),
                imageRect.centerX() + dp(34),
                imageRect.top + dp(92)
        );
        canvas.drawOval(hair, paint);

        // 脸
        paint.setColor(Color.rgb(245, 180, 160));
        RectF face = new RectF(
                imageRect.centerX() - dp(24),
                imageRect.top + dp(36),
                imageRect.centerX() + dp(24),
                imageRect.top + dp(88)
        );
        canvas.drawOval(face, paint);

        // 脸部马赛克/模糊块
        Paint blurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient faceMask = new LinearGradient(
                face.left,
                face.top,
                face.right,
                face.bottom,
                new int[]{
                        Color.rgb(205, 150, 135),
                        Color.rgb(235, 190, 175),
                        Color.rgb(185, 125, 115)
                },
                null,
                Shader.TileMode.CLAMP
        );
        blurPaint.setShader(faceMask);
        RectF mask = new RectF(
                face.left + dp(3),
                face.top + dp(4),
                face.right - dp(3),
                face.bottom - dp(4)
        );
        canvas.drawRoundRect(mask, dp(8), dp(8), blurPaint);

        // 身体
        paint.setColor(Color.rgb(255, 218, 210));
        RectF body = new RectF(
                imageRect.centerX() - dp(44),
                imageRect.top + dp(82),
                imageRect.centerX() + dp(40),
                imageRect.top + dp(168)
        );
        canvas.drawRoundRect(body, dp(30), dp(30), paint);

        // 手机
        paint.setColor(Color.rgb(20, 75, 120));
        RectF phone = new RectF(
                imageRect.centerX() + dp(16),
                imageRect.top + dp(82),
                imageRect.centerX() + dp(39),
                imageRect.top + dp(126)
        );
        canvas.drawRoundRect(phone, dp(4), dp(4), paint);

        paint.setColor(Color.argb(100, 255, 255, 255));
        canvas.drawCircle(phone.centerX(), phone.top + dp(7), dp(2), paint);

        // 左上角广告小标签
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(160, 80, 80, 80));
        RectF tag = new RectF(
                imageRect.left + dp(10),
                imageRect.top + dp(8),
                imageRect.left + dp(72),
                imageRect.top + dp(30)
        );
        canvas.drawRoundRect(tag, dp(6), dp(6), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(sp(8));
        paint.setFakeBoldText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseY = tag.centerY() - (fm.ascent + fm.descent) / 2f;
        canvas.drawText("红果免费短剧", tag.left + dp(5), baseY, paint);

        paint.setTextSize(sp(9));
        paint.setFakeBoldText(false);
        canvas.drawText("广告", tag.left + dp(5), tag.bottom + dp(12), paint);
    }

    private void drawAppIcon(Canvas canvas) {
        float size = dp(62);
        float left = cardRect.centerX() - size / 2f;
        float top = cardRect.top + dp(160);

        RectF icon = new RectF(left, top, left + size, top + size);

        Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient gradient = new LinearGradient(
                icon.left,
                icon.top,
                icon.right,
                icon.bottom,
                new int[]{
                        Color.rgb(255, 134, 36),
                        Color.rgb(255, 184, 70),
                        Color.rgb(35, 205, 210)
                },
                null,
                Shader.TileMode.CLAMP
        );
        iconPaint.setShader(gradient);
        canvas.drawRoundRect(icon, dp(14), dp(14), iconPaint);

        // 播放按钮白圆
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        canvas.drawCircle(icon.centerX(), icon.centerY() - dp(4), dp(16), paint);

        // 播放三角
        Path triangle = new Path();
        triangle.moveTo(icon.centerX() - dp(4), icon.centerY() - dp(14));
        triangle.lineTo(icon.centerX() - dp(4), icon.centerY() + dp(6));
        triangle.lineTo(icon.centerX() + dp(13), icon.centerY() - dp(4));
        triangle.close();

        paint.setColor(Color.rgb(255, 165, 56));
        canvas.drawPath(triangle, paint);

        // icon 内文字
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(sp(8));
        canvas.drawText("免费看短剧", icon.centerX(), icon.bottom - dp(9), paint);
        paint.setFakeBoldText(false);
    }

    private void drawTexts(Canvas canvas) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        // 标题
        paint.setColor(Color.WHITE);
        paint.setFakeBoldText(true);
        paint.setTextSize(sp(17));
        canvas.drawText(title, cardRect.centerX(), cardRect.top + dp(252), paint);

        // 描述
        paint.setFakeBoldText(false);
        paint.setColor(Color.argb(210, 255, 255, 255));
        paint.setTextSize(sp(13));
        canvas.drawText(desc, cardRect.centerX(), cardRect.top + dp(282), paint);
    }

    private void drawDownloadButton(Canvas canvas) {
        float radius = buttonRect.height() / 2f;

        Paint btnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LinearGradient gradient = new LinearGradient(
                buttonRect.left,
                buttonRect.centerY(),
                buttonRect.right,
                buttonRect.centerY(),
                Color.rgb(20, 130, 255),
                Color.rgb(0, 108, 255),
                Shader.TileMode.CLAMP
        );
        btnPaint.setShader(gradient);
        canvas.drawRoundRect(buttonRect, radius, radius, btnPaint);

        // 按钮文字
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
        paint.setTextSize(sp(17));
        paint.setColor(Color.WHITE);

        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseY = buttonRect.centerY() - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(buttonText, buttonRect.centerX() - dp(10), baseY, paint);

        // 右侧白色圆形装饰
        float cx = buttonRect.right - dp(30);
        float cy = buttonRect.centerY();

        paint.setFakeBoldText(false);
        paint.setColor(Color.argb(210, 255, 255, 255));
        canvas.drawCircle(cx, cy, dp(15), paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(cx + dp(9), cy + dp(7), dp(13), paint);

        // 下载箭头
        paint.setColor(Color.rgb(80, 150, 255));
        paint.setStrokeWidth(dp(3));
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawLine(cx + dp(2), cy - dp(5), cx + dp(11), cy + dp(6), paint);
        canvas.drawLine(cx + dp(20), cy - dp(5), cx + dp(11), cy + dp(6), paint);
        canvas.drawLine(cx + dp(11), cy + dp(6), cx + dp(11), cy - dp(12), paint);
    }

    private void drawCloseButton(Canvas canvas) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(Color.argb(200, 255, 255, 255));

        canvas.drawCircle(closeRect.centerX(), closeRect.centerY(), dp(17), paint);

        paint.setStrokeWidth(dp(2.2f));
        canvas.drawLine(
                closeRect.centerX() - dp(7),
                closeRect.centerY() - dp(7),
                closeRect.centerX() + dp(7),
                closeRect.centerY() + dp(7),
                paint
        );
        canvas.drawLine(
                closeRect.centerX() + dp(7),
                closeRect.centerY() - dp(7),
                closeRect.centerX() - dp(7),
                closeRect.centerY() + dp(7),
                paint
        );

        paint.setStyle(Paint.Style.FILL);
    }

    private void drawBottomText(Canvas canvas, int w, int h) {
        paint.reset();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(sp(10));
        paint.setColor(Color.argb(140, 255, 255, 255));
        canvas.drawText(
                "隐私 | 权限 | 介绍 | V7.1.5... | 北京笔墨留香科技有限公司",
                w / 2f,
                h - dp(22),
                paint
        );
    }

    /**
     * 画一点点顶部假的宿主页面 UI，单独预览这个 View 时更像截图。
     * 如果你是覆盖在真实页面上，也可以把这个方法里的内容删掉。
     */
    private void drawTopFakeUi(Canvas canvas, int w) {
        // 返回按钮
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(160, 107, 145, 59));
        RectF back = new RectF(dp(16), dp(30), dp(88), dp(69));
        canvas.drawRoundRect(back, dp(3), dp(3), paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(sp(14));
        paint.setColor(Color.argb(180, 255, 255, 255));
        Paint.FontMetrics fm = paint.getFontMetrics();
        float baseY = back.centerY() - (fm.ascent + fm.descent) / 2f;
        canvas.drawText("‹ 返回", back.centerX(), baseY, paint);

        // 右上角胶囊
        paint.setColor(Color.argb(120, 0, 0, 0));
        RectF pill = new RectF(w - dp(116), dp(34), w - dp(26), dp(69));
        canvas.drawRoundRect(pill, dp(20), dp(20), paint);

        paint.setColor(Color.argb(170, 255, 255, 255));
        paint.setTextSize(sp(18));
        canvas.drawText("···", pill.left + dp(24), pill.centerY() + dp(2), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        canvas.drawCircle(pill.right - dp(22), pill.centerY(), dp(9), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            if (closeRect.contains(x, y)) {
                invalidate();

                if (listener != null) {
                    listener.onCloseClick();
                }
                return true;
            }

            if (buttonRect.contains(x, y) || cardRect.contains(x, y)) {
                if (listener != null) {
                    listener.onDownloadClick();
                }
                return true;
            }
        }

        return true;
    }

    public void setOnAdClickListener(OnAdClickListener listener) {
        this.listener = listener;
    }

    public interface OnAdClickListener {
        void onDownloadClick();

        void onCloseClick();
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }

    private float sp(float value) {
        return value * getResources().getDisplayMetrics().scaledDensity;
    }
}
