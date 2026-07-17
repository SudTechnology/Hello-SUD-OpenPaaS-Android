package global.sud.gi.hello.quickstart;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import global.sud.gi.hello.R;

/**
 * 一个简单的游戏加载页面
 * A simple game loading page
 */
public class QuickStartGameActivity extends AppCompatActivity {

    private QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quickstart_game);

        // 初始化容器及监听
        // Initialize the container and set up the listener.
        FrameLayout gameContainer = findViewById(R.id.game_container);
        ConstraintLayout adContainer = findViewById(R.id.ad_container);
        ConstraintLayout topContainer = findViewById(R.id.top_container);
        gameViewModel.setSUDOPWrappedClient(new QuickStartSUDOPWrappedClient(gameViewModel, this, gameContainer, adContainer, topContainer));

        // 监听游戏View的添加或者删除事件
        // Listen for the addition or deletion events of the game View
        gameViewModel.gameViewLiveData.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View view) {
                if (view == null) {
                    gameContainer.removeAllViews();
                } else {
                    gameContainer.addView(view, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                }
            }
        });

        // 执行加载游戏，传入不同的gameId即可加载不同的游戏，加载游戏时机由app自行控制
        // Execute the game loading process. By passing in different "gameId", you can load different games. The timing of loading the game is controlled by the app itself.
        String gameId = "2049100752121937922";
        gameViewModel.switchGame(this, gameId);
    }

}
