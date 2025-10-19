package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class goPanel extends BoardgamePanel {
    private static goPanel goGame = null;

    private goPanel(MainWindow window) {
        // 为围棋游戏指定专用的背景与棋盘图片资源路径（classpath 相对路径）
        super(window, "src/resources/images/board/goBackground.png", "src/resources/images/board/goBorad.png");
    }

    public static goPanel initGoPanel(MainWindow w) {
        if (goGame == null) {
            goGame = new goPanel(w);
        }
        return goGame;
    }
}