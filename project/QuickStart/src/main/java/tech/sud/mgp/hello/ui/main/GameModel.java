package tech.sud.mgp.hello.ui.main;

import java.io.Serializable;

public class GameModel implements Serializable {
    public String gameName; // 游戏名称
    public String gameId; // 游戏id
    public String gameUrl; // 游戏Url
    public String gamePkgVersion; // 游戏包版本
    public int homeGamePic; // 首页展示的游戏图标
    public int gamePic; // 游戏图标
    public int runtime;
}
