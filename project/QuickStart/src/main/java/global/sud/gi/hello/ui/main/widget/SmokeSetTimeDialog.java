package global.sud.gi.hello.ui.main.widget;

import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;

import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseDialogFragment;


public class SmokeSetTimeDialog extends BaseDialogFragment {

    private EditText mEtStartInterval;
    private EditText mEtStopInterval;
    private OnStartListener mOnStartListener;
    public int mStartInterval = 5;
    public int mStopInterval = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_smoke_set_time;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEtStartInterval = findViewById(R.id.et_start_interval);
        mEtStopInterval = findViewById(R.id.et_stop_interval);
    }

    @Override
    protected void initData() {
        super.initData();
        mEtStartInterval.setText(mStartInterval + "");
        mEtStopInterval.setText(mStopInterval + "");
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            int startInterval = getInterval(mEtStartInterval);
            int stopInterval = getInterval(mEtStopInterval);
            if (startInterval == 0 || stopInterval == 0) {
                ToastUtils.showLong("Brother，填下间隔");
                return;
            }
            if (mOnStartListener != null) {
                mOnStartListener.onStart(startInterval, stopInterval);
            }
            dismiss();
        });
    }

    private int getInterval(EditText editText) {
        Editable text = editText.getText();
        if (text != null) {
            try {
                return Integer.parseInt(text.toString());
            } catch (Exception e) {
            }
        }
        return 0;
    }

    public void setOnStartListener(OnStartListener onStartListener) {
        mOnStartListener = onStartListener;
    }

    public interface OnStartListener {
        void onStart(int startInterval, int stopInterval);
    }

}
