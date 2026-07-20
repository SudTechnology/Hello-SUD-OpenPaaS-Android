[中文文档](./zh_README.md)

# Hello-SUD-OpenPaaS-Android

## Requirements

- minSdk 26, Gradle 7+

## Three-Minute Integration Guide

- Step 1: Import the SDK and wrapper code

  <details>
  <summary>Import the SUDGIWrapper module</summary>

      1. Download this project to your local machine.
      2. Use Android Studio's Import Module feature to import project/SUDGIWrapper.
      3. Add the SUDGIWrapper dependency in the app module's build.gradle file.
      4. This module includes the SDK dependency. If you need to publish the app on Google Play, please refer to the comments in SUDGIWrapper/build.gradle.

  ```java
  build.gradle

  dependencies {
    // Import SUDGIWrapper
    implementation project(':SUDGIWrapper')
  }
  ```

  </details>

  <details>
  <summary>Copy files</summary>

      1. Go to the directory: project/QuickStart/src/main/java/global/sud/gi/hello/quickstart
      2. Copy QuickStartGameViewModel.java and QuickStartSUDOPWrappedClient.java into your own project.

  </details>

- Step 2: Define the game View container and related containers in your own project

  <details>
  <summary>Refer to activity_quickstart_game.xml</summary>

  ```xml
  <!-- Game View container. The android:visibility attribute must not be set to gone. -->
  <FrameLayout
      android:id="@+id/game_container"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />

  <!-- Container used for toasts, dialogs, videos, and other widgets. -->
  <androidx.constraintlayout.widget.ConstraintLayout
      android:id="@+id/widget_container"
      android:layout_width="match_parent"
      android:layout_height="match_parent" />
  ```

  </details>

- Step 4: Create a QuickStartGameViewModel instance and handle lifecycle callbacks

  <details>
  <summary>Detailed description: QuickStartGameActivity.java</summary>

      1. Implement adding and removing the game View.
      2. Handle the game lifecycle.
      3. Code example:

  ```java
  private final QuickStartGameViewModel gameViewModel = new QuickStartGameViewModel(); // Create ViewModel

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      ......
      // Initialize containers and set up listeners.
      FrameLayout gameContainer = findViewById(R.id.game_container);
      ConstraintLayout widgetContainer = findViewById(R.id.widget_container);
      gameViewModel.setSUDOPWrappedClient(new QuickStartSUDOPWrappedClient(gameViewModel, this, gameContainer, widgetContainer));

      // Listen for game View add or remove events.
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

- Step 5: Load a game

  <details>
  <summary>Detailed description: QuickStartGameActivity.java</summary>

  ```java
  // Execute the game loading process.
  // Pass in different gameId values to load different games.
  // The app controls when to load the game.
  String gameId = "2049100752121937922";
  gameViewModel.switchGame(this, gameId);
  ```

  </details>

  Notes:

  - At this step, if you want to run the game directly in your own project to preview the effect before integrating with your backend, you can temporarily change your project's applicationId to: `global.sud.gi.hello`.
  - If your backend has already been deployed, please refer to the comments in `QuickStartGameViewModel` and modify the required parameters and methods, such as `appId`.

- Step 6: Destroy the game

  <details>
  <summary>Detailed description: QuickStartGameActivity.java</summary>

  ```java
  // Destroy the game before destroying the page.
  gameViewModel.destroyGame();
  finish();
  ```

  </details>

## Use Your Own appId and Backend Service

- Before releasing the application, replace the `appId`, `appKey`, and other information in the demo with the information you applied for on the SUD platform. Also make sure that the `applicationId` is configured in the console.
  [How to obtain it from the console](https://developer.sud.tech/openpaas/app/guide/guide-dev/op-console.html)

- In `QuickStartGameViewModel`, use your own application's `appId`, `appKey`, and `userId`.

- Override the `getCode` method. Use your own network request to request the `userSignature` value from your backend service, and then return it through the listener.
  [Backend method for generating userSignature](https://developer.sud.tech/openpaas/app/guide/guide-dev/quick-integration/auth.html)