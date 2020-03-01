package com.example.native_splash;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.android.SplashScreen;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterMain;

// public class MainActivity extends FlutterActivity {
//   @Override
//   public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
//     GeneratedPluginRegistrant.registerWith(flutterEngine);
//   }
// }


public class MainActivity extends FlutterActivity {
  private static FlutterEngine flutterEngine;
  @Override
  public FlutterEngine provideFlutterEngine(Context context) {
    if (flutterEngine == null) {
      flutterEngine = new FlutterEngine(context);
      flutterEngine.getDartExecutor().executeDartEntrypoint(new DartExecutor.DartEntrypoint(
          FlutterMain.findAppBundlePath(context), "main"));
    }
    return flutterEngine;
  }

  @Override
  public SplashScreen provideSplashScreen() {
    return new CustomSplashScreen();
  }
}
