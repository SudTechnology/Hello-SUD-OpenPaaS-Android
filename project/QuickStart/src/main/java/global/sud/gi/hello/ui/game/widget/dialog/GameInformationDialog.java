package global.sud.gi.hello.ui.game.widget.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseDialogFragment;
import global.sud.gi.hello.common.utils.DensityUtils;
import global.sud.gi.hello.common.utils.HSTextUtils;
import global.sud.gi.hello.common.utils.ImageLoader;
import global.sud.op.runtime.core.model.SUDOPGameInformationModel;


public class GameInformationDialog extends BaseDialogFragment {

    private ImageView ivIcon;
    private TextView tvGameName;
    private TextView tvSubjectName;
    private OnClickInformationListener onClickInformationListener;
    private SUDOPGameInformationModel gameInformationModel;

    public static GameInformationDialog newInstance(SUDOPGameInformationModel model) {
        Bundle args = new Bundle();
        args.putSerializable("SUDOPGameInformationModel", model);
        GameInformationDialog fragment = new GameInformationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameInformationModel = (SUDOPGameInformationModel) getArguments().getSerializable("SUDOPGameInformationModel");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_game_information;
    }

    @Override
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected int getWidth() {
        return DensityUtils.dp2px(375);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ivIcon = findViewById(R.id.iv_icon);
        tvGameName = findViewById(R.id.tv_game_name);
        tvSubjectName = findViewById(R.id.tv_subject_name);
    }

    @Override
    protected void initData() {
        super.initData();
        setGameInfomation();
    }

    private void setGameInfomation() {
        if (gameInformationModel == null) {
            return;
        }
        ImageLoader.loadImage(ivIcon, gameInformationModel.game_icon);
        tvGameName.setText(HSTextUtils.getLanguageText(gameInformationModel.game_name));
        tvSubjectName.setText(gameInformationModel.subject_name);
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        findViewById(R.id.container_restart).setOnClickListener(v -> {
            if (onClickInformationListener != null) {
                onClickInformationListener.onClickRestart();
            }
            dismiss();
        });
        findViewById(R.id.container_information).setOnClickListener(v -> {
            onClickcInformation();
        });
        findViewById(R.id.top_info_container).setOnClickListener(v -> {
            onClickcInformation();
        });
    }

    private void onClickcInformation() {
        if (onClickInformationListener != null) {
            onClickInformationListener.onClickInformation();
        }
        dismiss();
    }

    @Override
    protected void customStyle(Window window) {
        super.customStyle(window);
        window.setWindowAnimations(R.style.BottomToTopAnim);
    }

    public void setOnClickInformationListener(OnClickInformationListener onClickInformationListener) {
        this.onClickInformationListener = onClickInformationListener;
    }

    public interface OnClickInformationListener {
        void onClickRestart();

        void onClickInformation();
    }

}
