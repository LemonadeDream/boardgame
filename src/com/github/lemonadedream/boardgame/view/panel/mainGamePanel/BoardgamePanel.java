package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.controller.ImageLoader;
import java.awt.image.BufferedImage;

public class BoardgamePanel extends JPanel {
    // 主窗口引用（保留以便将来用于事件回调 / 状态访问）
    private MainWindow window;
    // 图片资源路径（每个子类可以传入不同路径）
    protected String backgroundPath = null;
    protected String boardPath = null;
    // 缓存图片
    private BufferedImage backgroundImg;
    private BufferedImage boardImg;
    // 缩放后的棋盘图缓存（根据面板大小动态计算）
    private BufferedImage scaledBoardImg;
    // 面板四周留白（像素，可改为按比例计算）
    private int margin = 40;
    // 创建游戏选择界面按钮组件

    // JButton backButton = new JButton("返回");

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换

        // backButton.addActionListener(e -> {
        // window.switchPanel(e);
        // });
    }

    // 组装选择按钮
    public void init() {
        // 调用按钮绑定切换功能的函数
        initButtons();
        // 按钮打包到桌布

        // this.setLayout(new GridLayout(1, 1, 0, 50));

        // this.add(backButton);

        this.setBorder(BorderFactory.createEmptyBorder(150, 200, 250, 200));
        // 尝试加载图片资源（如果资源不存在，则不会抛出异常）
        backgroundImg = ImageLoader.load(backgroundPath);
        boardImg = ImageLoader.load(boardPath);
    }

    // 构造方法（默认资源路径）
    public BoardgamePanel(MainWindow window) {
        this(window, null, null);
    }

    /**
     * 构造方法，允许子类或调用方指定背景与棋盘图片的 classpath 路径。
     * 若传入的路径为 null，则使用默认字段值。
     */
    public BoardgamePanel(MainWindow window, String backgroundPath, String boardPath) {
        // 赋值窗口引用
        this.window = window;
        // 如果调用方提供了自定义路径，则覆盖默认值
        if (backgroundPath != null)
            this.backgroundPath = backgroundPath;
        if (boardPath != null)
            this.boardPath = boardPath;
        // 初始化面板（会加载图片）
        init();
    }

    // 在面板大小变化时，重新计算棋盘缩放图像
    private void updateScaledBoardIfNeeded() {
        if (boardImg == null) {
            scaledBoardImg = null;
            return;
        }
        int availableW = Math.max(1, getWidth() - margin * 2);
        int availableH = Math.max(1, getHeight() - margin * 2);
        // 为了把棋盘置顶，可以限制高度为(可用高度 * 0.6) 或者直接使用可用高度
        int maxBoardH = (int) (availableH * 0.7);
        BufferedImage scaled = ImageLoader.scaleToFit(boardImg, availableW, maxBoardH);
        scaledBoardImg = scaled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // 绘制背景：如果有背景图，则把背景图拉伸/平铺以覆盖整个面板
        if (backgroundImg != null) {
            int w = getWidth();
            int h = getHeight();
            // 直接拉伸背景到面板大小（可能变形），也可选择等比裁剪实现更好视觉
            g2.drawImage(backgroundImg, 0, 0, w, h, null);
        }

        // 更新并绘制棋盘图像（置于上层）
        if (boardImg != null) {
            updateScaledBoardIfNeeded();
            if (scaledBoardImg != null) {
                int bw = scaledBoardImg.getWidth();
                // 将棋盘居中于水平中线，置顶于可用区域（即距上边 margin）
                int x = (getWidth() - bw) / 2;
                int y = margin; // 置顶留出上方 margin
                g2.drawImage(scaledBoardImg, x, y, null);
            }
        }

        g2.dispose();
    }
}