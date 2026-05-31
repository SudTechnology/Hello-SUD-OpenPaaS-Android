package global.sud.gi.hello.ui.game.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import global.sud.gi.hello.R;
import global.sud.op.runtime.core.wrapped.SUDOPVideoParams;

public class VideoContainer extends ConstraintLayout {

    private ImageView ivFullscreen;
    private ImageView ivControl;
    private SeekBar seekBar;
    private TextView tvProgress;
    private TextView tvTotal;
    private ImageView ivExitFullscreen;

    public VideoContainer(@NonNull Context context) {
        this(context, null);
    }

    public VideoContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public VideoContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_video_container, this);
        ivFullscreen = findViewById(R.id.iv_video_fullscreen);
        ivControl = findViewById(R.id.iv_video_control);
        seekBar = findViewById(R.id.seek_bar);
        tvProgress = findViewById(R.id.tv_progress);
        tvTotal = findViewById(R.id.tv_total);
        ivExitFullscreen = findViewById(R.id.iv_exit_fullscreen);
    }

    public void initVideo(SUDOPVideoParams params) {
        TextureView videoView = params.videoView;
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(videoView, 0, layoutParams);

        // 封面，需要有展示的时机，以及不展示的时机
    }

}
