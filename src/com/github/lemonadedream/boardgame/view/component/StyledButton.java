package com.github.lemonadedream.boardgame.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import com.github.lemonadedream.boardgame.controller.ImageLoader;

/**
 * 自定义样式按钮工具类，用于为按钮添加背景图片
 */
public class StyledButton {
    private static BufferedImage buttonBackground = null;

    /**
     * 为按钮应用背景图片样式
     * 
     * @param button 要应用样式的按钮
     */
    public static void applyButtonStyle(JButton button) {
        // 延迟加载背景图片
        if (buttonBackground == null) {
            buttonBackground = ImageLoader.load("resources/images/buttonbackground/ever0307.png");
        }

        if (buttonBackground != null) {
            // 设置按钮为不透明，以便绘制背景
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);

            // 使用自定义UI来绘制背景
            final BufferedImage bg = buttonBackground;
            button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
                @Override
                public void paint(Graphics g, JComponent c) {
                    JButton btn = (JButton) c;
                    Graphics2D g2d = (Graphics2D) g.create();

                    // 绘制背景图片
                    g2d.drawImage(bg, 0, 0, c.getWidth(), c.getHeight(), null);

                    // 如果按钮被按下，添加一个半透明覆盖层
                    if (btn.getModel().isPressed()) {
                        g2d.setColor(new Color(0, 0, 0, 50));
                        g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                    }
                    // 如果鼠标悬停，添加一个高亮覆盖层
                    else if (btn.getModel().isRollover()) {
                        g2d.setColor(new Color(255, 255, 255, 30));
                        g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
                    }

                    g2d.dispose();

                    // 绘制文本
                    super.paint(g, c);
                }
            });
        }
    }
}
