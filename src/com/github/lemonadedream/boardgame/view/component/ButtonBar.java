package com.github.lemonadedream.boardgame.view.component;

import javax.swing.*;
import java.awt.*;

/**
 * ButtonBar: 通用的按钮条，用于创建顶部/底部的一行按钮。
 * 按钮外观统一设置，便于在不同面板中复用，也方便后续通过样式控制统一修改。
 */
public class ButtonBar {
    /**
     * 创建一行按钮的容器
     *
     * @param buttonTexts 按钮文本，按顺序添加到按钮条
     * @return 返回一个透明背景的 JPanel，居中布局，内部包含按顺序创建的 RatioButton
     */
    public static JPanel createButtonBar(String... buttonTexts) {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        bar.setOpaque(false);
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 16); // 设置按钮字体
        for (String text : buttonTexts) {
            RatioButton btn = new RatioButton(text, 100, 0.5f);
            btn.setFont(buttonFont); // 应用字体
            bar.add(btn);
        }
        return bar;
    }
}
