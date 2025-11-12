package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.controller.ImageLoader;
import java.awt.image.BufferedImage;

public class BoardgamePanel extends JPanel {
    // 图片资源路径（每个子类可以传入不同路径）
    protected String backgroundPath = null;
    protected String boardPath = null;
    // 缓存图片
    private BufferedImage backgroundImg;
    private BufferedImage boardImg;
    // 缩放后的棋盘图缓存(根据面板大小动态计算)
    private BufferedImage scaledBoardImg;
    // 面板四周留白(像素,可改为按比例计算)
    private int margin = 0;
    // 棋盘实际绘制区域(用于子类进行坐标转换和绘制棋子)
    protected Rectangle boardDrawBounds = new Rectangle();
    // 棋盘透明度 (0.0f = 完全透明, 1.0f = 完全不透明)
    private float boardOpacity = 1.0f;
    // 创建游戏选择界面按钮组件

    // JButton backButton = new JButton("返回");

    // 给各个按钮绑定功能
    public void initButtons() {
        // 所有按钮都添加事件监听器,用OuterWindow的switchPanel切换

        // backButton.addActionListener(e -> {
        // MainWindow.getInstance().switchPanel(e);
        // });
    }

    // 组装选择按钮
    public void init() {
        // 调用按钮绑定切换功能的函数
        initButtons();
        // 按钮打包到桌布

        // this.setLayout(new GridLayout(1, 1, 0, 50));

        // this.add(backButton);

        this.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        // 尝试加载资源路径对应的图片（如果路径为 null 或资源不存在，ImageLoader.load 可能返回 null）
        backgroundImg = ImageLoader.load(backgroundPath);
        boardImg = ImageLoader.load(boardPath);
    }

    // 构造方法：使用默认资源路径（即不传入自定义路径）
    public BoardgamePanel() {
        this(null, null);
    }

    /**
     * 构造方法，允许调用方指定背景图片路径与棋盘图片路径
     * 
     * @param backgroundPath 背景图片的资源路径（可为 null）
     * @param boardPath      棋盘图片的资源路径（可为 null）
     */
    public BoardgamePanel(String backgroundPath, String boardPath) {
        // 如果调用方传入了自定义背景路径，则覆盖默认值
        if (backgroundPath != null)
            this.backgroundPath = backgroundPath;
        // 如果调用方传入了自定义棋盘路径，则覆盖默认值
        if (boardPath != null)
            this.boardPath = boardPath;
        // 初始化面板（会调用 initButtons 并加载图片）
        init();
    }

    /**
     * 根据给定的目标宽高判断是否需要生成/更新缓存的缩放棋盘图像
     * 
     * @param targetW 希望放置棋盘的目标宽度（像素）
     * @param targetH 希望放置棋盘的目标高度（像素）
     */
    private void updateScaledBoardIfNeeded(int targetW, int targetH) {
        // 如果没有加载棋盘图片，则清空缓存并返回
        if (boardImg == null) {
            scaledBoardImg = null; // 清理之前的缩放缓存
            return; // 提前返回，避免后续访问空对象
        }
        // 计算去掉左右上下 margin 后的可用宽高，确保最小为 1 防止传 0 导致异常
        int availableW = Math.max(1, targetW - margin * 2); // 可用宽度（像素）
        int availableH = Math.max(1, targetH - margin * 2); // 可用高度（像素）
        // 为了避免棋盘完全填满导致遮挡其它控件，预留少量空白：这里取 95% 的可用空间
        int maxBoardW = (int) (availableW * 0.95);
        int maxBoardH = (int) (availableH * 0.95);
        // 使用 ImageLoader.scaleToFit 等比例缩放原始棋盘图片以适配 maxBoardW x maxBoardH
        BufferedImage scaled = ImageLoader.scaleToFit(boardImg, maxBoardW, maxBoardH);
        // 将缩放后的图片缓存，避免在每次重绘时重复缩放，提升性能
        scaledBoardImg = scaled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 首先让父类处理背景等基础绘制（例如透明度或背景色）
        super.paintComponent(g);
        // 创建 Graphics2D 的副本以便在结束时调用 dispose，不影响传入的 Graphics 对象
        Graphics2D g2 = (Graphics2D) g.create();
        // 如果存在背景图，则把背景图拉伸到整个面板大小进行绘制
        if (backgroundImg != null) {
            int w = getWidth(); // 面板当前宽度
            int h = getHeight(); // 面板当前高度
            // 直接拉伸背景图片以覆盖整个面板区域
            g2.drawImage(backgroundImg, 0, 0, w, h, null);
        }

        // 接着绘制棋盘图（位于背景之上）
        if (boardImg != null) {
            // 默认的绘制区域为整个面板
            Rectangle drawArea = new Rectangle(0, 0, getWidth(), getHeight());
            // 如果面板使用 BorderLayout，则尝试只在 CENTER 区域绘制棋盘，避免被左右侧栏遮挡
            LayoutManager lm = getLayout();
            if (lm instanceof BorderLayout) {
                Component center = ((BorderLayout) lm).getLayoutComponent(this, BorderLayout.CENTER);
                if (center != null && center.isVisible()) {
                    Rectangle b = center.getBounds();
                    // 使用 center 区域作为绘图区域的边界
                    drawArea = new Rectangle(b);
                }
            }
            // 根据当前可绘制区域计算并缓存缩放后的棋盘图像（如有必要）
            updateScaledBoardIfNeeded(drawArea.width, drawArea.height);
            // 如果已经准备好缩放后的棋盘图像,则把它绘制到面板上
            if (scaledBoardImg != null) {
                // 获取缓存图像的实际像素宽高
                int bw = scaledBoardImg.getWidth();
                int bh = scaledBoardImg.getHeight();
                // 计算绘制区域内部去掉 margin 后的可用宽高
                int availableW = Math.max(1, drawArea.width - margin * 2);
                int availableH = Math.max(1, drawArea.height - margin * 2);
                // 计算使棋盘在绘制区域内水平和垂直居中的坐标
                int x = drawArea.x + margin + (availableW - bw) / 2; // 左上角 x 坐标
                int y = drawArea.y + margin + (availableH - bh) / 2; // 左上角 y 坐标

                // 应用透明度设置
                if (boardOpacity < 1.0f) {
                    Composite originalComposite = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, boardOpacity));
                    g2.drawImage(scaledBoardImg, x, y, null);
                    g2.setComposite(originalComposite);
                } else {
                    // 完全不透明时直接绘制，性能更好
                    g2.drawImage(scaledBoardImg, x, y, null);
                }

                // 保存棋盘实际绘制区域,供子类使用(如绘制棋子、坐标转换)
                boardDrawBounds.setBounds(x, y, bw, bh);
            }
        }

        // 释放 Graphics2D 副本资源
        g2.dispose();
    }

    /**
     * 获取棋盘实际绘制区域(左上角坐标+宽高)
     * 
     * @return 棋盘绘制区域的矩形,若棋盘未绘制则返回空矩形
     */
    public Rectangle getBoardDrawBounds() {
        return new Rectangle(boardDrawBounds);
    }

    /**
     * 更新背景图片
     * 
     * @param backgroundPath 新的背景图片路径
     */
    public void setBackgroundImage(String backgroundPath) {
        if (backgroundPath != null) {
            this.backgroundPath = backgroundPath;
            this.backgroundImg = ImageLoader.load(backgroundPath);
            repaint();
        }
    }

    /**
     * 更新棋盘图片
     * 
     * @param boardPath 新的棋盘图片路径
     */
    public void setBoardImage(String boardPath) {
        if (boardPath != null) {
            this.boardPath = boardPath;
            this.boardImg = ImageLoader.load(boardPath);
            // 清除缩放缓存，强制重新计算
            this.scaledBoardImg = null;
            repaint();
        }
    }

    /**
     * 设置棋盘透明度
     * 
     * @param opacity 透明度值 (0.0f = 完全透明, 1.0f = 完全不透明)
     */
    public void setBoardOpacity(float opacity) {
        this.boardOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        repaint();
    }

    /**
     * 获取当前棋盘透明度
     * 
     * @return 当前透明度值
     */
    public float getBoardOpacity() {
        return boardOpacity;
    }
}