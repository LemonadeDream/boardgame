package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class goPanel extends BoardgamePanel {
    private static goPanel goGame = null;

    private goPanel(MainWindow window) {
        // ΪΧ����Ϸָ��ר�õı���������ͼƬ��Դ·����classpath ���·����
        super(window, "src/resources/images/board/goBackground.png", "src/resources/images/board/goBorad.png");
    }

    public static goPanel initGoPanel(MainWindow w) {
        if (goGame == null) {
            goGame = new goPanel(w);
        }
        return goGame;
    }
}