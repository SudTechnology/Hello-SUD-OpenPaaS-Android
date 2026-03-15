package tech.sud.mgp.hello.ui.game;

import android.util.Log;

import global.sud.runtime.annotation.SUDASync;
import global.sud.runtime.api.SUDJSCallback;

public class LoginDemo {
    private static final String TAG = "LoginDemo";

    public static String testLogin() {
        return ""
                + "(function() {  "
                + "  try {  "
                + "    sud.login({  "
                + "      userId: 'inject-test',  "
                + "      success: function(msg) {  "
                + "        if (typeof console !== 'undefined' && console.log) {  "
                + "          console.log('login success', msg);  "
                + "        }  "
                + "      },  "
                + "      fail: function(msg) {  "
                + "        if (typeof console !== 'undefined' && console.log) {  "
                + "          console.log('login fail', msg);  "
                + "        }  "
                + "      }  "
                + "    });  "
                + "  } catch (e) {  "
                + "      console.error(e);  "
                + "  }  "
                + "})();  ";
    }

    public static class LoginOptions {
        public String userId = "userID is null";

        public SUDJSCallback successcb;
        public SUDJSCallback failcb;

        @SUDASync
        public void success(SUDJSCallback cb) {
            Log.d(TAG, "LoginDemo success");
            if (successcb != null) {
                successcb.release();
            }
            this.successcb = cb;
        }

        @SUDASync
        public void fail(SUDJSCallback cb) {
            Log.d(TAG, "LoginDemo SUDJSCallback");
            if (failcb != null) {
                failcb.release();
            }
            this.failcb = cb;
        }
    }

    @SUDASync
    public void login(LoginOptions options) {
        if (options == null) {
            return;
        }
        SUDJSCallback successcb = options.successcb;
        SUDJSCallback failcb = options.failcb;
        if (successcb != null) {
            successcb.invoke("login success " + options.userId);
        }
//        if (failcb != null) {
//            failcb.invoke("login failed" + options.userId);
//        }
    }
}
