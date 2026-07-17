package global.sud.gi.hello.game.ad.widget.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.IOException;

public class RewardedAdView extends FrameLayout {
    private VideoScaleType videoScaleType = VideoScaleType.CENTER_CROP;

    public enum VideoScaleType {
        /**
         * 铺满整个 View，多余部分裁剪。
         */
        CENTER_CROP,
        /**
         * 完整显示视频，不裁剪，可能出现黑边。
         */
        FIT_CENTER
    }

    public enum CloseReason {
        USER,
        PROGRAM,
        ERROR
    }

    public interface OnRewardedAdListener {

        /**
         * 已经看满要求时长，比如默认 30 秒。
         */
        void onRewardedAdCompleted();

        /**
         * 最终关闭回调。
         *
         * @param watchedComplete 是否看完，是否满足奖励条件
         * @param watchedMillis   已观看毫秒数
         * @param reason          关闭原因
         */
        void onRewardedAdClosed(boolean watchedComplete, long watchedMillis, CloseReason reason);

        /**
         * 播放出错。
         */
        void onRewardedAdError(Exception e);

        void onClickVideo();
    }

    private final Context appContext;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private AspectTextureView textureView;
    private TextView countDownText;
    private TextView closeText;
    private TextView muteText;

    private MediaPlayer mediaPlayer;
    private Surface surface;

    private String videoUrl;

    private long requiredWatchMillis = 30_000L;
    private long watchedMillis = 0L;
    private long lastTickTime = 0L;

    private boolean completed = false;
    private boolean closed = false;
    private boolean prepared = false;

    private boolean hostPaused = false;
    private boolean dialogShowing = false;
    private boolean muted = false;
    private boolean autoRemoveOnClose = false;

    private AlertDialog exitDialog;

    private OnRewardedAdListener listener;

    public RewardedAdView(Context context) {
        super(context);
        this.appContext = context.getApplicationContext();
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.BLACK);
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);

        textureView = new AspectTextureView(context);
        textureView.setVideoScaleType(videoScaleType);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        textureView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClickVideo();
            }
        });

        LayoutParams videoLp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );
        videoLp.gravity = Gravity.CENTER;
        addView(textureView, videoLp);

        countDownText = new TextView(context);
        countDownText.setTextColor(Color.WHITE);
        countDownText.setTextSize(14);
        countDownText.setGravity(Gravity.CENTER);
        countDownText.setPadding(dp(14), dp(8), dp(14), dp(8));
        countDownText.setBackground(makeRoundBg(0x99000000, dp(18)));
        countDownText.setText("看30s可领取奖励");

        LayoutParams countLp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        countLp.gravity = Gravity.START | Gravity.TOP;
        countLp.leftMargin = dp(16);
        countLp.topMargin = dp(86);
        addView(countDownText, countLp);

        closeText = new TextView(context);
        closeText.setText("×");
        closeText.setTextColor(Color.WHITE);
        closeText.setTextSize(28);
        closeText.setGravity(Gravity.CENTER);
        closeText.setTypeface(Typeface.DEFAULT_BOLD);
        closeText.setBackground(makeRoundBg(0x99000000, dp(18)));

        LayoutParams closeLp = new LayoutParams(dp(42), dp(36));
        closeLp.gravity = Gravity.END | Gravity.TOP;
        closeLp.rightMargin = dp(16);
        closeLp.topMargin = dp(86);
        addView(closeText, closeLp);

        muteText = new TextView(context);
        muteText.setText("声");
        muteText.setTextColor(Color.WHITE);
        muteText.setTextSize(14);
        muteText.setGravity(Gravity.CENTER);
        muteText.setBackground(makeRoundBg(0x99000000, dp(18)));

        LayoutParams muteLp = new LayoutParams(dp(42), dp(36));
        muteLp.gravity = Gravity.END | Gravity.TOP;
        muteLp.rightMargin = dp(64);
        muteLp.topMargin = dp(86);
        addView(muteText, muteLp);

        closeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUserClose();
            }
        });

        muteText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setMuted(!muted);
            }
        });

        updateCountDownText();
    }

    /**
     * 设置监听。
     */
    public void setOnRewardedAdListener(OnRewardedAdListener listener) {
        this.listener = listener;
    }

    /**
     * 默认 30 秒，可自定义。
     */
    public void setRequiredWatchMillis(long requiredWatchMillis) {
        if (requiredWatchMillis <= 0) {
            requiredWatchMillis = 30_000L;
        }
        this.requiredWatchMillis = requiredWatchMillis;
        updateCountDownText();
    }

    /**
     * 关闭时是否自动从父布局 remove。
     */
    public void setAutoRemoveOnClose(boolean autoRemoveOnClose) {
        this.autoRemoveOnClose = autoRemoveOnClose;
    }

    /**
     * 加载并播放视频 URL。
     */
    public void load(String videoUrl) {
        this.videoUrl = videoUrl;
        preparePlayerIfReady();
    }

    /**
     * 程序主动关闭，不弹窗。
     */
    public void closeByProgram() {
        closeInternal(CloseReason.PROGRAM);
    }

    /**
     * 程序主动关闭。
     *
     * @param showConfirmIfNotCompleted 如果未看完，是否也弹确认框
     */
    public void closeByProgram(boolean showConfirmIfNotCompleted) {
        if (showConfirmIfNotCompleted && !completed) {
            showExitConfirmDialog();
        } else {
            closeInternal(CloseReason.PROGRAM);
        }
    }

    /**
     * 给 Activity 的返回键调用。
     * <p>
     * 示例：
     * if (adView != null && adView.handleBackPressed()) return;
     */
    public boolean handleBackPressed() {
        if (closed) {
            return false;
        }
        requestUserClose();
        return true;
    }

    /**
     * 宿主 Activity onPause 时调用。
     */
    public void onHostPause() {
        hostPaused = true;
        pausePlayback();
    }

    /**
     * 宿主 Activity onResume 时调用。
     */
    public void onHostResume() {
        hostPaused = false;
        if (!dialogShowing) {
            resumePlayback();
        }
    }

    /**
     * 宿主销毁时调用，避免泄漏。
     */
    public void destroy() {
        closeInternal(CloseReason.PROGRAM);
    }

    public boolean isWatchedComplete() {
        return completed;
    }

    public long getWatchedMillis() {
        return watchedMillis;
    }

    private void requestUserClose() {
        if (closed) {
            return;
        }

        if (completed) {
            closeInternal(CloseReason.USER);
        } else {
            showExitConfirmDialog();
        }
    }

    private void showExitConfirmDialog() {
        if (closed || dialogShowing) {
            return;
        }

        dialogShowing = true;
        pausePlayback();

        long remainMs = Math.max(0L, requiredWatchMillis - watchedMillis);
        long remainSec = (remainMs + 999L) / 1000L;

        String message = "继续观看 " + remainSec + " 秒即可领取奖励，确定要离开吗？";

        exitDialog = new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setNegativeButton("关闭视频", null)
                .setPositiveButton("继续观看", null)
                .create();

        exitDialog.setOnShowListener(dialog -> {
            Button closeBtn = exitDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            Button continueBtn = exitDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            closeBtn.setTextColor(Color.parseColor("#FF5252"));
            continueBtn.setTextColor(Color.parseColor("#4CAF50"));
            closeBtn.setAllCaps(false);
            continueBtn.setAllCaps(false);

            closeBtn.setOnClickListener(v -> {
                dialogShowing = false;
                exitDialog.dismiss();
                closeInternal(CloseReason.USER);
            });

            continueBtn.setOnClickListener(v -> {
                dialogShowing = false;
                exitDialog.dismiss();
                if (!hostPaused) {
                    resumePlayback();
                }
            });
        });

        exitDialog.setOnCancelListener(dialog -> {
            dialogShowing = false;
            if (!hostPaused && !closed) {
                resumePlayback();
            }
        });

        exitDialog.show();
    }

    private void preparePlayerIfReady() {
        if (videoUrl == null || videoUrl.length() == 0) {
            return;
        }

        if (surface == null) {
            return;
        }

        releasePlayer();

        try {
            prepared = false;

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getContext(), Uri.parse(videoUrl));
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setOnPreparedListener(mp -> {
                prepared = true;

                textureView.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());

                applyMute();

                if (!hostPaused && !dialogShowing && !closed) {
                    resumePlayback();
                }

                startProgressTicker();
            });

            mediaPlayer.setOnVideoSizeChangedListener((mp, width, height) -> {
                textureView.setVideoSize(width, height);
            });

            mediaPlayer.setOnInfoListener((mp, what, extra) -> {
                /**
                 * MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001
                 * 部分设备会通过 extra 返回旋转角度。
                 */
                if (what == 10001) {
                    textureView.setVideoRotation(extra);
                }
                return false;
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                if (listener != null) {
                    listener.onRewardedAdError(
                            new RuntimeException("MediaPlayer error, what=" + what + ", extra=" + extra)
                    );
                }
                closeInternal(CloseReason.ERROR);
                return true;
            });

            mediaPlayer.prepareAsync();

        } catch (IOException e) {
            if (listener != null) {
                listener.onRewardedAdError(e);
            }
            closeInternal(CloseReason.ERROR);
        } catch (Exception e) {
            if (listener != null) {
                listener.onRewardedAdError(e);
            }
            closeInternal(CloseReason.ERROR);
        }
    }

    private void resumePlayback() {
        if (closed || hostPaused || dialogShowing) {
            return;
        }

        try {
            if (mediaPlayer != null && prepared && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
            lastTickTime = SystemClock.elapsedRealtime();
        } catch (Exception ignored) {
        }
    }

    private void pausePlayback() {
        try {
            if (mediaPlayer != null && prepared && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception ignored) {
        }
    }

    private void startProgressTicker() {
        lastTickTime = SystemClock.elapsedRealtime();
        handler.removeCallbacks(progressRunnable);
        handler.post(progressRunnable);
    }

    private final Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (closed) {
                return;
            }

            long now = SystemClock.elapsedRealtime();

            if (shouldCounting()) {
                long delta = now - lastTickTime;
                if (delta > 0 && delta < 2000) {
                    watchedMillis += delta;
                }

                if (!completed && watchedMillis >= requiredWatchMillis) {
                    completed = true;
                    watchedMillis = requiredWatchMillis;

                    if (listener != null) {
                        listener.onRewardedAdCompleted();
                    }
                }
            }

            lastTickTime = now;

            updateCountDownText();

            handler.postDelayed(this, 250L);
        }
    };

    private boolean shouldCounting() {
        if (completed || closed || hostPaused || dialogShowing) {
            return false;
        }

        try {
            return mediaPlayer != null && prepared && mediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    private void updateCountDownText() {
        if (countDownText == null) {
            return;
        }

        if (completed) {
            countDownText.setText("已满足奖励条件");
        } else {
            long remainMs = Math.max(0L, requiredWatchMillis - watchedMillis);
            long remainSec = (remainMs + 999L) / 1000L;
            countDownText.setText("看" + remainSec + "s可领取奖励");
        }
    }

    private void setMuted(boolean muted) {
        this.muted = muted;
        applyMute();

        if (muteText != null) {
            muteText.setText(muted ? "静" : "声");
        }
    }

    private void applyMute() {
        try {
            if (mediaPlayer != null) {
                if (muted) {
                    mediaPlayer.setVolume(0f, 0f);
                } else {
                    mediaPlayer.setVolume(1f, 1f);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void closeInternal(CloseReason reason) {
        if (closed) {
            return;
        }

        closed = true;

        handler.removeCallbacks(progressRunnable);

        if (exitDialog != null) {
            try {
                exitDialog.dismiss();
            } catch (Exception ignored) {
            }
            exitDialog = null;
        }

        releasePlayer();

        if (listener != null) {
            listener.onRewardedAdClosed(completed, watchedMillis, reason);
        }

        if (autoRemoveOnClose) {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            }
        }
    }

    private void releasePlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.setOnVideoSizeChangedListener(null);
                mediaPlayer.setOnInfoListener(null);
                mediaPlayer.setOnErrorListener(null);
                mediaPlayer.stop();
            }
        } catch (Exception ignored) {
        }

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        } catch (Exception ignored) {
        }

        mediaPlayer = null;
        prepared = false;
    }

    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    surface = new Surface(surfaceTexture);
                    preparePlayerIfReady();
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                    textureView.updateTransform();
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    releasePlayer();

                    if (surface != null) {
                        surface.release();
                        surface = null;
                    }

                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                }
            };

    private GradientDrawable makeRoundBg(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 自动根据当前 View 宽高、视频宽高调整显示方式。
     * <p>
     * CENTER_CROP：铺满裁剪
     * FIT_CENTER：完整显示视频，可能出现黑边
     */
    private static class AspectTextureView extends TextureView {

        private int videoWidth = 0;
        private int videoHeight = 0;
        private int videoRotation = 0;

        private VideoScaleType videoScaleType = VideoScaleType.CENTER_CROP;

        public AspectTextureView(Context context) {
            super(context);
        }

        public void setVideoScaleType(VideoScaleType videoScaleType) {
            if (videoScaleType == null) {
                videoScaleType = VideoScaleType.CENTER_CROP;
            }

            this.videoScaleType = videoScaleType;
            updateTransform();
        }

        public void setVideoSize(int width, int height) {
            if (width <= 0 || height <= 0) {
                return;
            }

            this.videoWidth = width;
            this.videoHeight = height;
            updateTransform();
        }

        public void setVideoRotation(int rotation) {
            this.videoRotation = rotation;
            updateTransform();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            updateTransform();
        }

        public void updateTransform() {
            int viewWidth = getWidth();
            int viewHeight = getHeight();

            if (viewWidth <= 0 || viewHeight <= 0 || videoWidth <= 0 || videoHeight <= 0) {
                return;
            }

            int realVideoWidth = videoWidth;
            int realVideoHeight = videoHeight;

            if (videoRotation == 90 || videoRotation == 270) {
                realVideoWidth = videoHeight;
                realVideoHeight = videoWidth;
            }

            float viewAspect = viewWidth * 1f / viewHeight;
            float videoAspect = realVideoWidth * 1f / realVideoHeight;

            float scaleX = 1f;
            float scaleY = 1f;

            if (videoScaleType == VideoScaleType.CENTER_CROP) {
                /**
                 * CENTER_CROP：
                 * 视频铺满整个 RewardedAdView，多余部分裁剪。
                 */
                if (videoAspect > viewAspect) {
                    scaleX = videoAspect / viewAspect;
                } else {
                    scaleY = viewAspect / videoAspect;
                }
            } else {
                /**
                 * FIT_CENTER：
                 * 完整显示视频，不裁剪。
                 * 视频比例和 View 比例不一致时，会出现黑边。
                 */
                if (videoAspect > viewAspect) {
                    scaleY = viewAspect / videoAspect;
                } else {
                    scaleX = videoAspect / viewAspect;
                }
            }

            Matrix matrix = new Matrix();
            float px = viewWidth / 2f;
            float py = viewHeight / 2f;

            matrix.setScale(scaleX, scaleY, px, py);

            if (videoRotation != 0) {
                matrix.postRotate(videoRotation, px, py);
            }

            setTransform(matrix);
        }
    }

    /**
     * 设置视频显示模式。
     * <p>
     * CENTER_CROP：铺满裁剪
     * FIT_CENTER：完整显示，有黑边
     */
    public void setVideoScaleType(VideoScaleType videoScaleType) {
        if (videoScaleType == null) {
            videoScaleType = VideoScaleType.CENTER_CROP;
        }
        this.videoScaleType = videoScaleType;
        if (textureView != null) {
            textureView.setVideoScaleType(videoScaleType);
        }
    }
}