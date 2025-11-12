package com.github.lemonadedream.boardgame.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.github.lemonadedream.boardgame.controller.ImageLoader;

/**
 * 通用比例按钮类
 * 固定25:16比例，可设置大小和背景透明度
 */
public class RatioButton extends JButton {
    private static BufferedImage buttonBackground = null;
    private int buttonWidth;
    private int buttonHeight;
    private float backgroundOpacity = 0.5f; // 默认背景透明度50%

    /**
     * 创建一个25:16比例的按钮
     * 
     * @param text  按钮文本
     * @param width 按钮宽度
     */
    public RatioButton(String text, int width) {
        this(text, width, 0.5f);
    }

    /**
     * 创建一个25:16比例的按钮，可指定背景透明度
     * 
     * @param text              按钮文本
     * @param width             按钮宽度
     * @param backgroundOpacity 背景透明度 (0.0-1.0)
     */
    public RatioButton(String text, int width, float backgroundOpacity) {
        super(text);
        this.buttonWidth = width;
        this.buttonHeight = (int) (width * 16.0 / 25.0); // 保持25:16比例
        this.backgroundOpacity = Math.max(0.0f, Math.min(1.0f, backgroundOpacity)); // 限制在0-1之间

        // 加载按钮背景图片
        if (buttonBackground == null) {
            buttonBackground = ImageLoader.load("resources/images/buttonbackground/ever0307.png");
        }

        initButton();
    }

    /**
     * 初始化按钮样式
     */
    private void initButton() {
        // 设置按钮固定大小
        Dimension size = new Dimension(buttonWidth, buttonHeight);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);

        // 设置按钮透明
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        // 设置默认字体
        setFont(new Font("微软雅黑", Font.BOLD, 24));
        setForeground(Color.BLACK);

        // 使用自定义UI绘制背景
        setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton btn = (JButton) c;
                Graphics2D g2d = (Graphics2D) g.create();

                // 启用抗锯齿
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 绘制背景图片（带透明度）
                if (buttonBackground != null) {
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                            backgroundOpacity);
                    g2d.setComposite(alphaComposite);
                    g2d.drawImage(buttonBackground, 0, 0, c.getWidth(), c.getHeight(), null);

                    // 恢复不透明度以绘制文本
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                }

                // 如果按钮被按下，添加一个半透明覆盖层
                if (btn.getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                }
                // 如果鼠标悬停，添加一个高亮覆盖层
                else if (btn.getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                }

                g2d.dispose();

                // 绘制文本
                super.paint(g, c);
            }
        });
    }

    /**
     * 设置背景透明度
     * 
     * @param opacity 透明度 (0.0-1.0)
     */
    public void setBackgroundOpacity(float opacity) {
        this.backgroundOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        repaint();
    }

    /**
     * 获取当前背景透明度
     * 
     * @return 透明度值
     */
    public float getBackgroundOpacity() {
        return backgroundOpacity;
    }

    /**
     * 获取按钮宽度
     */
    public int getButtonWidth() {
        return buttonWidth;
    }

    /**
     * 获取按钮高度
     */
    public int getButtonHeight() {
        return buttonHeight;
    }
}
