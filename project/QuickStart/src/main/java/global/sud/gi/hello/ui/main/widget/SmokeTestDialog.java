package global.sud.gi.hello.ui.main.widget;

import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;

import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseDialogFragment;
import global.sud.gi.hello.common.utils.DensityUtils;
import global.sud.gi.hello.ui.main.model.GameModel;


public class SmokeTestDialog extends BaseDialogFragment {

    private EditText mEtStartInterval;
    private EditText mEtStopInterval;
    private MyAdapter mAdapter;
    private List<GameModel> mDatas;
    private GameModel mSelectedOpenPass;
    private OnStartListener mOnStartListener;
    public int mStartInterval = 5;
    public int mStopInterval = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_smoke_test;
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
    protected int getHeight() {
        return (int) (DensityUtils.getAppScreenHeight() * 0.7);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mEtStartInterval = findViewById(R.id.et_start_interval);
        mEtStopInterval = findViewById(R.id.et_stop_interval);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new MyAdapter(mDatas);
        recyclerView.setAdapter(mAdapter);
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
            if (mSelectedOpenPass == null) {
                ToastUtils.showLong("Brother,Please select game");
                return;
            }
            int startInterval = getInterval(mEtStartInterval);
            int stopInterval = getInterval(mEtStopInterval);
            if (startInterval == 0 || stopInterval == 0) {
                ToastUtils.showLong("Brother，填下间隔");
                return;
            }
            if (mOnStartListener != null) {
                mOnStartListener.onStart(startInterval, stopInterval, mSelectedOpenPass);
            }
            dismiss();
        });
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                GameModel item = mAdapter.getItem(position);
                ToastUtils.showLong("您选中了：" + item.gameName);
                mSelectedOpenPass = item;
            }
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

    private class MyAdapter extends BaseQuickAdapter<GameModel, BaseViewHolder> {
        public MyAdapter(@Nullable List<GameModel> data) {
            super(R.layout.item_name_list, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder model, GameModel info) {
            model.setText(R.id.tv_title, info.gameName);
        }
    }

    public void setDatas(List<GameModel> datas) {
        mDatas = datas;
    }

    public void setOnStartListener(OnStartListener onStartListener) {
        mOnStartListener = onStartListener;
    }

    public interface OnStartListener {
        void onStart(int startInterval, int stopInterval, GameModel info);
    }

}
