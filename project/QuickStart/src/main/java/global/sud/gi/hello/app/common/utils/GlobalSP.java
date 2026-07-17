package global.sud.gi.hello.app.common.utils;

import com.blankj.utilcode.util.SPUtils;

/**
 * 全局使用的SharedPreferences
 */
public class GlobalSP {
    private final static String SP_NAME = "global.sp";
    public static final String KEY_GAME_DIR_PATH = "key_game_dir_path";
    public static final String KEY_GAME_ID = "key_game_id";
    public static final String KEY_GAME_SIGNATURE = "key_game_signature";

    public static SPUtils getSP() {
        return SPUtils.getInstance(SP_NAME);
    }
}
