package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class SettingPanel extends JPanel {
    // 主窗口引用
    private MainWindow window;
    // 创建游戏选择界面按钮组件
    JButton backButton = new JButton("返回");

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换
        backButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // 组装选择按钮
    public void init() {
        // 调用按钮绑定切换功能的函数
        initButtons();
        // 按钮打包到桌布
        this.setLayout(new GridLayout(1, 1, 0, 50));
        this.add(backButton);
        this.setBorder(BorderFactory.createEmptyBorder(150, 250, 250, 250));
    }

    // 构造方法
    public SettingPanel(MainWindow window) {
        // 赋值窗口引用
        this.window = window;
        // 初始化面板
        init();
    }
}
