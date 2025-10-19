package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class GameChoosePanel extends JPanel {
    // 主窗口引用
    private MainWindow window;
    // 创建游戏选择界面按钮组件
    JButton chessChoose = new JButton("国际象棋");
    JButton gomokuChoose = new JButton("围棋");
    JButton backButton = new JButton("返回");

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换
        chessChoose.addActionListener(e -> {
            window.switchPanel(e);
        });
        gomokuChoose.addActionListener(e -> {
            window.switchPanel(e);
        });
        backButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // 组装选择按钮
    public void init() {
        // 调用按钮绑定切换功能的函数
        initButtons();
        // 按钮打包到桌布
        this.setLayout(new GridLayout(3, 1, 0, 50));
        this.add(chessChoose);
        this.add(gomokuChoose);
        this.add(backButton);
        this.setBorder(BorderFactory.createEmptyBorder(150, 250, 250, 250));
    }

    public GameChoosePanel(MainWindow window) {
        // 赋值
        this.window = window;
        init();
    }
}
