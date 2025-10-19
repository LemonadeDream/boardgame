package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class MainPanel extends JPanel {
    // 主窗口引用
    private MainWindow window;
    // 创建主界面按钮组件
    JButton beginButton = new JButton("开始");
    JButton settingButton = new JButton("设置");
    JButton closeButton = new JButton("退出");
    JButton achivementButton = new JButton("成就");

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换
        beginButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        settingButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        closeButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        achivementButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // 初始化函数,给按钮进行排版
    public void init() {
        // 调用按钮绑定切换功能的函数
        initButtons();
        // 主界面按钮打包到桌布
        this.setLayout(new GridLayout(4, 1, 0, 30));
        this.add(beginButton);
        this.add(settingButton);
        this.add(achivementButton);
        this.add(closeButton);
        // 桌布设置边框
        this.setBorder(BorderFactory.createEmptyBorder(150, 200, 150, 200));
    }

    // 构造方法
    public MainPanel(MainWindow window) {
        // 赋值
        this.window = window;
        // 调用初始化函数
        init();
    }
}