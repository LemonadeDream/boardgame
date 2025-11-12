package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.github.lemonadedream.boardgame.controller.ImageLoader;
import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.view.skins.SkinChooser;
import com.github.lemonadedream.boardgame.view.component.RatioButton;

public class GoChoosePanel extends JPanel {

    // 皮肤预设按钮 - 2×2布局，使用400宽度
    private RatioButton preset1Button;
    private RatioButton preset2Button;
    private RatioButton preset3Button;
    private RatioButton preset4Button;

    // 棋盘预设按钮 - 1×2布局，使用400宽度
    private RatioButton boardPreset1Button;
    private RatioButton boardPreset2Button;

    // 背景预设按钮 - 1×2布局，使用400宽度
    private RatioButton bgPreset1Button;
    private RatioButton bgPreset2Button;

    // 透明度选项按钮 - 1×2布局，使用200宽度
    private RatioButton opacityFullButton;
    private RatioButton opacity80Button;

    // 开始游戏按钮 - 单独一个，使用500宽度
    private RatioButton startGameButton;

    // 皮肤选择器（需要在游戏面板创建后初始化）
    private SkinChooser skinChooser;

    // 背景图片
    private BufferedImage backgroundImage;

    public GoChoosePanel() {
        // 加载背景图片
        backgroundImage = ImageLoader.load("resources/images/outerbackground/helektra_bath.png");
        initComponents();
        layoutComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 绘制背景图片
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void initComponents() {
        // 初始化皮肤预设按钮 - 200宽度
        preset1Button = new RatioButton("预设1: 默认", 200, 0.5f);
        preset2Button = new RatioButton("预设2: 绿蓝", 200, 0.5f);
        preset3Button = new RatioButton("预设3: 红蓝", 200, 0.5f);
        preset4Button = new RatioButton("预设4: 紫白", 200, 0.5f);

        // 初始化棋盘预设按钮 - 200宽度
        boardPreset1Button = new RatioButton("默认棋盘", 200, 0.5f);
        boardPreset2Button = new RatioButton("无色棋盘", 200, 0.5f);

        // 初始化背景预设按钮 - 200宽度
        bgPreset1Button = new RatioButton("默认背景", 200, 0.5f);
        bgPreset2Button = new RatioButton("Ano背景", 200, 0.5f);

        // 初始化透明度选项按钮 - 200宽度
        opacityFullButton = new RatioButton("不透明", 200, 0.5f);
        opacity80Button = new RatioButton("80%透明", 200, 0.5f);

        // 绑定预设按钮事件
        preset1Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setStoneSkin("default");
                JOptionPane.showMessageDialog(this, "已选择默认皮肤，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        preset2Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setStoneSkin("green_lightblue");
                JOptionPane.showMessageDialog(this, "已选择绿蓝皮肤，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        preset3Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setStoneSkin("red_pureblue");
                JOptionPane.showMessageDialog(this, "已选择红蓝皮肤，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        preset4Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setStoneSkin("pr_white");
                JOptionPane.showMessageDialog(this, "已选择紫白皮肤，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 绑定棋盘预设按钮事件
        boardPreset1Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBoardPreset("default");
                JOptionPane.showMessageDialog(this, "已选择默认棋盘，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        boardPreset2Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBoardPreset("colorless");
                JOptionPane.showMessageDialog(this, "已选择无色棋盘，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 绑定背景预设按钮事件
        bgPreset1Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBackgroundPreset("default");
                JOptionPane.showMessageDialog(this, "已选择默认背景，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        bgPreset2Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBackgroundPreset("ano");
                JOptionPane.showMessageDialog(this, "已选择Ano背景，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 绑定透明度选项按钮事件
        opacityFullButton.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBoardOpacity(1.0f);
                JOptionPane.showMessageDialog(this, "已设置棋盘不透明，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        opacity80Button.addActionListener(e -> {
            if (skinChooser != null) {
                skinChooser.setBoardOpacity(0.2f);
                JOptionPane.showMessageDialog(this, "已设置棋盘80%透明度，进入游戏后生效", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 初始化开始游戏按钮 - 200宽度
        startGameButton = new RatioButton("进入围棋游戏", 200, 0.5f);
        startGameButton.setFont(new Font("微软雅黑", Font.BOLD, 20));
        startGameButton.addActionListener(e -> MainWindow.getInstance().switchPanel(e));

        // 设置所有按钮的字体
        setButtonFonts();
    }

    // 设置按钮字体
    private void setButtonFonts() {
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 18);
        preset1Button.setFont(buttonFont);
        preset2Button.setFont(buttonFont);
        preset3Button.setFont(buttonFont);
        preset4Button.setFont(buttonFont);
        boardPreset1Button.setFont(buttonFont);
        boardPreset2Button.setFont(buttonFont);
        bgPreset1Button.setFont(buttonFont);
        bgPreset2Button.setFont(buttonFont);
        opacityFullButton.setFont(buttonFont);
        opacity80Button.setFont(buttonFont);
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // 皮肤预设区域 - 2×2布局
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(preset1Button, gbc);

        gbc.gridx = 1;
        add(preset2Button, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(preset3Button, gbc);

        gbc.gridx = 1;
        add(preset4Button, gbc);

        // 棋盘预设区域 - 1×2布局
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(boardPreset1Button, gbc);

        gbc.gridx = 1;
        add(boardPreset2Button, gbc);

        // 背景预设区域 - 1×2布局
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(bgPreset1Button, gbc);

        gbc.gridx = 1;
        add(bgPreset2Button, gbc);

        // 透明度选项区域 - 1×2布局
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(opacityFullButton, gbc);

        gbc.gridx = 1;
        add(opacity80Button, gbc);

        // 添加开始游戏按钮 - 居中，跨两列
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 15, 15, 15);
        add(startGameButton, gbc);
    }

    /**
     * 设置皮肤选择器（在游戏面板创建后调用）
     * 
     * @param skinChooser 皮肤选择器实例
     */
    public void setSkinChooser(SkinChooser skinChooser) {
        this.skinChooser = skinChooser;
    }
}