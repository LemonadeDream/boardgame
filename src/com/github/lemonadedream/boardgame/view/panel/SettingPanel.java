package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import com.github.lemonadedream.boardgame.controller.ImageLoader;
import com.github.lemonadedream.boardgame.controller.MusicPlayer;
import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.view.component.RatioButton;

public class SettingPanel extends JPanel {
    // 创建按钮组件 - 使用200宽度形成2x2布局
    RatioButton backButton = new RatioButton("返回", 200, 0.5f);
    RatioButton muteButton = new RatioButton("静音", 200, 0.5f);
    RatioButton previousButton = new RatioButton("上一首", 200, 0.5f);
    RatioButton nextButton = new RatioButton("下一首", 200, 0.5f);

    // 背景图片
    private BufferedImage backgroundImage;

    // 设置按钮字体
    private void setButtonFonts() {
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 24);
        backButton.setFont(buttonFont);
        muteButton.setFont(buttonFont);
        previousButton.setFont(buttonFont);
        nextButton.setFont(buttonFont);
    }

    // 给各个按钮绑定功能
    public void initButtons() {
        MusicPlayer player = MusicPlayer.getInstance();

        // 返回按钮
        backButton.addActionListener(e -> {
            MainWindow.getInstance().switchPanel(e);
        });

        // 静音按钮
        muteButton.addActionListener(e -> {
            player.toggleMute();
            updateMuteButtonText();
        });

        // 上一首按钮
        previousButton.addActionListener(e -> {
            player.previous();
        });

        // 下一首按钮
        nextButton.addActionListener(e -> {
            player.next();
        });
    }

    // 更新静音按钮文本
    private void updateMuteButtonText() {
        MusicPlayer player = MusicPlayer.getInstance();
        muteButton.setText(player.isMuted() ? "取消静音" : "静音");
    }

    // 组装选择按钮
    public void init() {
        // 加载背景图片
        backgroundImage = ImageLoader.load("resources/images/outerbackground/helektra_bath.png");
        // 设置按钮字体
        setButtonFonts();
        // 调用按钮绑定切换功能的函数
        initButtons();

        // 使用GridBagLayout创建2x2布局
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // 第一行：返回和静音
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(backButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(muteButton, gbc);

        // 第二行：上一首和下一首
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(previousButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(nextButton, gbc);
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
    public SettingPanel() {
        // 初始化面板
        init();
    }
}
