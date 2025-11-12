package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import com.github.lemonadedream.boardgame.controller.ImageLoader;
import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.view.component.RatioButton;

public class MainPanel extends JPanel {
    // 创建主界面按钮组件 - 使用RatioButton，宽度400，透明度50%（2x2布局需要小一点的按钮）
    RatioButton beginButton = new RatioButton("开始", 400, 0.5f);
    RatioButton settingButton = new RatioButton("设置", 400, 0.5f);
    RatioButton achivementButton = new RatioButton("成就", 400, 0.5f);
    RatioButton closeButton = new RatioButton("退出", 400, 0.5f);

    // 背景图片
    private BufferedImage backgroundImage;

    // 设置按钮字体（RatioButton已经有默认字体，可以覆盖）
    private void setButtonFonts() {
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 24);
        beginButton.setFont(buttonFont);
        settingButton.setFont(buttonFont);
        closeButton.setFont(buttonFont);
        achivementButton.setFont(buttonFont);
    }

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换
        beginButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });
        settingButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });
        closeButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });
        achivementButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });
    }

    // 初始化函数,给按钮进行排版
    public void init() {
        // 加载背景图片
        backgroundImage = ImageLoader.load("resources/images/outerbackground/helektra_bath.png");
        // 设置按钮字体
        setButtonFonts();
        // 调用按钮绑定切换功能的函数
        initButtons();

        // 使用GridBagLayout实现2x2布局，居中显示
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // 按钮之间的间距

        // 第一行第一列 - 开始按钮
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(beginButton, gbc);

        // 第一行第二列 - 设置按钮
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(settingButton, gbc);

        // 第二行第一列 - 成就按钮
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(achivementButton, gbc);

        // 第二行第二列 - 退出按钮
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(closeButton, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 绘制背景图片
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // 构造方法
    public MainPanel() {
        // 调用初始化函数
        init();
    }
}