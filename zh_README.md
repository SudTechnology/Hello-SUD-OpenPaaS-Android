# Hello-SUD-OpenPaaS-Android

## 要求

- minSdk 26，Gradle 7+

## 三分钟集成代码

- 第一步：导入SDK及封装代码
  <details>
  <summary>导入模块SUDGIWrapper</summary>

      1.下载此项目代码到本地；  
      2.通过Android Studio的Import Module功能，导入project/SUDGIWrapper；  
      3.APP主工程文件build.gradle中，添加 SUDGIWrapper 依赖;
      4.此模块包含了SDK的依赖，如需上架Google Play，请查看SUDGIWrapper/build.gradle当中的注释说明
  ``` java
  build.gradle
  
  dependencies {
    // 引入 SUDGIWrapper
    implementation project(':SUDGIWrapper')
  }
  ```
  </details>

  <details>
  <summary>拷贝文件</summary>

      1.在目录：project/QuickStart/src/main/java/global/sud/gi/hello/quickstart
      2.拷贝QuickStartGameViewModel.java、QuickStartSUDOPWrappedClient.java到自己的项目当中
  </details>

- 第二步：在自己的项目当中，定义游戏View的容器等
    <details>
    <summary>参考 activity_quickstart_game.xml</summary>

    ``` xml
    <!-- 游戏View容器，android:visibility属性不能设置为gone -->
    <FrameLayout
        android:id="@+id/game_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!-- 一些toast、弹框、视频所使用的容器 -->
    <androidx.constraintlayout.widget.ConstraintLayout
        android:id="@+id/widget_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    ```
    </details>

- 第四步：创建QuickStartGameViewModel实例，及生命周期对应
    <details>
    <summary>详细描述 QuickStartGameActivity.java</summary>

      1.实现游戏View的添加与移除；
      2.游戏生命周期处理
      3.代码；
    ``` java
    private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel(); // 创建ViewModel
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ......
        // 初始化容器及监听
        // Initialize the container and set up the listener.
        FrameLayout gameContainer = findViewById(R.id.game_container);
        ConstraintLayout widgetContainer = findViewById(R.id.widget_container);
        gameViewModel.setSUDOPWrappedClient(new QuickStartSUDOPWrappedClient(gameViewModel, this, gameContainer, widgetContainer));

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
    }
  
    @Override
    protected void onStart() {
        super.onStart();
        gameViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameViewModel.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameViewModel.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.destroyGame();
    }
    ```
    </details>


- 第五步：加载游戏
    <details>
    <summary>详细描述 QuickStartGameActivity.java</summary>

    ``` java
    // 执行加载游戏，传入不同的gameId即可加载不同的游戏，加载游戏时机由app自行控制
    // Execute the game loading process. By passing in different "gameId", you can loaddifferent games. The timing of loading the game is controlled by the app itself.
    String gameId = "2049100752121937922";
    gameViewModel.switchGame(this, gameId);   
    ```
    </details>

    注意：
    - 走到这一步时，如果想在和后端联调前直接让游戏在自己项目当中跑起来看效果，可临时将自己项目的applicationId修改为：global.sud.gi.hello
    - 如果后端已部署完成，请查看QuickStartGameViewModel当中注释里需要修改的appId等参数和方法
- 第六步：销毁游戏
    <details>
    <summary>详细描述 QuickStartGameActivity.java</summary>

    ``` java
    // 页面销毁之前，先销毁游戏
    gameViewModel.destroyGame();
    finish();    
    ```
    </details>

## 使用自己申请的appId以及后端服务

- 应用上线前，需要将demo当中的appId、appKey等信息替换为自身在SUD平台申请的信息，以及注意applicationId在控制台当中配置。[获取方式控制台](https://developer.sud.tech/openpaas/app/guide/guide-dev/op-console.html)

- 在QuickStartGameViewModel当中，使用应用自身的appId、appKey、userId。

- 重写getCode方法，使用应用自身的网络请求，向自身的后端服务请求拿到userSignature值后通过listener返回，[后端生成userSignature的方法](https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/auth.html)
