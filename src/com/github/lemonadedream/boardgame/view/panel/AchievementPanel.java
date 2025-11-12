package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import com.github.lemonadedream.boardgame.controller.ImageLoader;
import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.view.component.RatioButton;

public class AchievementPanel extends JPanel {
    // 创建游戏选择界面按钮组件 - 单个按钮，使用500宽度
    RatioButton backButton = new RatioButton("返回", 500, 0.5f);

    // 背景图片
    private BufferedImage backgroundImage;

    // 设置按钮字体
    private void setButtonFonts() {
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 24);
        backButton.setFont(buttonFont);
    }

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换
        backButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });
    }

    // 组装选择按钮
    public void init() {
        // 加载背景图片
        backgroundImage = ImageLoader.load("resources/images/outerbackground/helektra_bath.png");
        // 设置按钮字体
        setButtonFonts();
        // 调用按钮绑定切换功能的函数
        initButtons();

        // 使用GridBagLayout居中
        this.setLayout(new GridBagLayout());
        this.add(backButton);
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
    public AchievementPanel() {
        // 初始化面板
        init();
    }
}