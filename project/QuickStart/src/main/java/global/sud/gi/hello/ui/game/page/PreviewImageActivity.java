package global.sud.gi.hello.ui.game.page;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.gyf.immersionbar.ImmersionBar;

import java.util.Arrays;

import global.sud.gi.hello.R;
import global.sud.gi.hello.common.base.BaseActivity;
import global.sud.gi.hello.common.utils.ViewUtils;
import global.sud.op.runtime.core.wrapped.SUDOPPreviewImageParams;

public class PreviewImageActivity extends BaseActivity {

    private TextView tvProgress;
    private MyAdapter adapter;
    private ViewPager2 viewPager2;
    private SUDOPPreviewImageParams params;

    public static void start(Context context, SUDOPPreviewImageParams params) {
        Intent intent = new Intent(context, PreviewImageActivity.class);
        intent.putExtra("SUDOPPreviewImageParams", params);
        context.startActivity(intent);
    }

    @Override
    protected boolean beforeSetContentView() {
        SUDOPPreviewImageParams params = (SUDOPPreviewImageParams) getIntent().getSerializableExtra("SUDOPPreviewImageParams");
        if (params == null) {
            return true;
        }
        this.params = params;
        return super.beforeSetContentView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_image;
    }

    @Override
    protected void setStatusBar() {
        ImmersionBar.with(this).statusBarColor(R.color.transparent).statusBarDarkFont(false).init();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        tvProgress = findViewById(R.id.tv_progress);
        viewPager2 = findViewById(R.id.view_pager2);

        adapter = new MyAdapter();
        viewPager2.setAdapter(adapter);

        viewPager2.setOffscreenPageLimit(3);

        View topBar = findViewById(R.id.top_bar);
        ViewUtils.setHeight(topBar, ImmersionBar.getStatusBarHeight(this));
    }

    @Override
    protected void initData() {
        super.initData();
        adapter.setList(Arrays.asList(params.urls));
        if (params.urls != null && params.urls.length > 0 && !TextUtils.isEmpty(params.current)) {
            int index = 0;
            for (int i = 0; i < params.urls.length; i++) {
                String url = params.urls[i];
                if (params.current.equals(url)) {
                    index = i;
                    break;
                }
            }
            viewPager2.setCurrentItem(index);
            setPagerIndex(index);
        }
    }

    @Override
    protected void setListeners() {
        super.setListeners();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPagerIndex(position);
            }
        });
    }

    private void setPagerIndex(int index) {
        int total = params.urls == null ? 0 : params.urls.length;
        tvProgress.setText((++index) + " / " + total);
    }

    private class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_preview_image, null);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder holder, String path) {
            ImageView ivIcon = holder.getView(R.id.iv_icon);
            Glide.with(PreviewImageActivity.this).load(path).into(ivIcon);
        }
    }


}
