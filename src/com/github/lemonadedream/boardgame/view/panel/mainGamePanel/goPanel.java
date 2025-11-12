package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.controller.GoBoardMouseController;
import com.github.lemonadedream.boardgame.controller.GoComponentsAdder;
import com.github.lemonadedream.boardgame.controller.GoGameButtonController;
import com.github.lemonadedream.boardgame.controller.ImageLoader;
import com.github.lemonadedream.boardgame.controller.ReplayController;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;

public class GoPanel extends BoardgamePanel {
    // 面板模式枚举
    public enum PanelMode {
        PLAYING, // 对局模式
        REPLAY // 复盘模式
    }

    // 单例实例已移除，允许外部按需创建新的 GoPanel 实例

    // ========== 棋子绘制相关字段 ==========
    // 棋盘模型引用(用于获取当前棋局状态)
    private GoBoard boardModel;
    // 黑棋图片(由用户选择,默认路径可为null)
    private BufferedImage blackStoneImg;
    // 白棋图片(由用户选择,默认路径可为null)
    private BufferedImage whiteStoneImg;
    // 辅助线显示坐标(预留,用于首次点击后显示落子辅助线)
    private Point auxiliaryLinePos = null;
    // UI组件管理器(用于访问计时器和信息标签)
    private GoComponentsAdder componentsAdder;
    // 鼠标控制器引用
    private GoBoardMouseController mouseController;
    // 复盘控制器
    private ReplayController replayController;
    // 当前模式
    private PanelMode currentMode = PanelMode.PLAYING;

    public GoPanel() {
        // 为围棋游戏指定专用的背景与棋盘图片资源路径(classpath 相对路径)
        super("resources/images/board/goBackground.jpg", "resources/images/board/goBoard.png");
        // 使用 BorderLayout 以便添加上下左右组件
        setLayout(new BorderLayout());
        // 初始化棋盘模型
        this.boardModel = new GoBoard();
        // 用户选择棋子图片后调用setStoneImages方法设置
        // 示例: setStoneImages("resources/images/stones/black.png",
        // "resources/images/stones/white.png");
        setStoneImages("resources/images/stones/pic1.png", "resources/images/stones/pic2.png");

        // 将围棋相关组件添加到本面板(不影响中心棋盘的自绘)
        this.componentsAdder = new GoComponentsAdder();
        this.componentsAdder.addTo(this);

        // 创建并绑定鼠标控制器，传入计时器以实现时间控制
        // 同时传入游戏结束监听器，用于处理跳转到复盘面板
        this.mouseController = new GoBoardMouseController(
                this,
                this::onBoardClicked, // 使用方法引用作为回调
                componentsAdder.getMatchTimePiece(), // 对局计时器
                componentsAdder.getMoveTimePiece(), // 步时计时器
                this::onGameEnd // 游戏结束回调
        );

        // 创建游戏控制器并绑定按钮事件
        GoGameButtonController gameController = new GoGameButtonController(MainWindow.getInstance(), this,
                componentsAdder);
        componentsAdder.bindButtonListener(gameController);
    }

    /**
     * 棋盘点击事件回调(用于更新回合和棋子数量)
     */
    private void onBoardClicked(int row, int col, java.awt.event.MouseEvent e) {
        // 只在确认落子成功后才更新信息（此回调仅在confirmMove成功时调用）
        updateGameInfo(false);
    }

    /**
     * 游戏结束回调
     * 
     * @param winnerColor 胜方颜色
     * @param reason      结束原因
     */
    private void onGameEnd(int winnerColor, String reason) {
        System.out.println("GoPanel收到游戏结束通知: " +
                (winnerColor == GoBoard.BLACK ? "黑方" : "白方") + "获胜");

        // TODO: 在这里实现跳转到复盘面板的逻辑
        // 例如: window.switchToReplayPanel();
        // 或者: ActionEvent replayEvent = new ActionEvent(this,
        // ActionEvent.ACTION_PERFORMED, "复盘");
        // window.switchPanel(replayEvent);
    }

    /**
     * 更新游戏信息显示(回合数和棋子数量)
     * 
     * @param undo 是否为悔棋操作,是则回合数减1
     */

    private void updateGameInfo(boolean undo) {
        if (componentsAdder == null) {
            return;
        }

        // 更新回合数
        javax.swing.JLabel roundLabel = componentsAdder.getRoundLabel();
        if (roundLabel != null) {
            int currentRound = Integer.parseInt(roundLabel.getText());
            if (undo) {
                // 悔棋：回合数减1
                roundLabel.setText(String.valueOf(Math.max(currentRound - 1, 1)));
            } else {
                // 正常落子：回合数加1
                roundLabel.setText(String.valueOf(currentRound + 1));
            }
        }

        // 更新棋子数量(手动统计)
        int blackCount = 0;
        int whiteCount = 0;
        int[][] status = boardModel.getCurStatus();
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                if (status[i][j] == GoBoard.BLACK) {
                    blackCount++;
                } else if (status[i][j] == GoBoard.WHITE) {
                    whiteCount++;
                }
            }
        }

        javax.swing.JLabel pieceLabel = componentsAdder.getPieceCountLabel();
        if (pieceLabel != null) {
            pieceLabel.setText(String.format("黑:%d 白:%d", blackCount, whiteCount));
        }
    }

    /**
     * 兼容旧调用：每次返回一个新的 GoPanel 实例（不再复用老实例）
     */
    public static GoPanel getGoPanel() {
        return new GoPanel();
    }

    // ========== 棋子图片设置方法 ==========
    /**
     * 设置黑白棋子图片(由用户选择后调用)
     * 
     * @param blackStonePath 黑棋图片资源路径
     * @param whiteStonePath 白棋图片资源路径
     */
    public void setStoneImages(String blackStonePath, String whiteStonePath) {
        if (blackStonePath != null) {
            this.blackStoneImg = ImageLoader.load(blackStonePath);
        }
        if (whiteStonePath != null) {
            this.whiteStoneImg = ImageLoader.load(whiteStonePath);
        }
        repaint(); // 加载图片后重绘
    }

    /**
     * 获取棋盘模型(供Controller使用)
     */
    public GoBoard getBoardModel() {
        return boardModel;
    }

    /**
     * 获取鼠标控制器(供Controller使用)
     */
    public GoBoardMouseController getMouseController() {
        return mouseController;
    }

    // ========== 坐标转换工具方法 ==========
    /**
     * 将鼠标点击的像素坐标转换为棋盘逻辑坐标
     * 
     * @param pixelX 鼠标点击的屏幕X坐标
     * @param pixelY 鼠标点击的屏幕Y坐标
     * @return Point对象,x和y为棋盘逻辑坐标(1-19),若点击在棋盘外则返回null
     */
    public Point pixelToBoard(int pixelX, int pixelY) {
        // 获取棋盘实际绘制区域
        Rectangle bounds = getBoardDrawBounds();
        if (bounds.width == 0 || bounds.height == 0) {
            return null; // 棋盘未绘制
        }

        // 判断点击是否在棋盘范围内
        if (pixelX < bounds.x || pixelX > bounds.x + bounds.width ||
                pixelY < bounds.y || pixelY > bounds.y + bounds.height) {
            return null; // 点击在棋盘外
        }

        // 计算每个格子的像素大小(考虑了边缘像素)
        double gridWidth = bounds.width / 21.25;
        double gridHeight = bounds.height / 21.25;

        // 计算棋盘中心点的像素坐标
        double centerX = bounds.x + bounds.width / 2.0;
        double centerY = bounds.y + bounds.height / 2.0;

        // 计算点击位置相对于中心点的偏移
        double offsetX = pixelX - centerX;
        double offsetY = pixelY - centerY;

        // 转换为逻辑坐标(中心点为10,10,向右下为正)
        int boardX = 10 + (int) Math.round(offsetX / gridWidth);
        int boardY = 10 + (int) Math.round(offsetY / gridHeight);

        // 边界检查(确保在1-19范围内)
        if (boardX < 1 || boardX > 19 || boardY < 1 || boardY > 19) {
            return null;
        }

        return new Point(boardX, boardY);
    }

    // ========== 辅助线显示方法(预留接口) ==========
    /**
     * 显示落子辅助线(首次点击后调用,暂时空置)
     * 
     * @param row 棋盘行坐标(1-19)
     * @param col 棋盘列坐标(1-19)
     */
    public void showAuxiliaryLine(int row, int col) {
        // TODO: 实现辅助线绘制逻辑(如高亮十字线、预览棋子等)
        this.auxiliaryLinePos = new Point(row, col);
        // TODO: 在paintComponent中使用auxiliaryLinePos绘制辅助效果
        repaint();
    }

    /**
     * 隐藏辅助线
     */
    public void hideAuxiliaryLine() {
        this.auxiliaryLinePos = null;
        repaint();
    }

    // ========== 棋子绘制方法 ==========
    @Override
    protected void paintComponent(Graphics g) {
        // 先绘制背景和棋盘
        super.paintComponent(g);

        // 如果棋子图片未加载,不绘制棋子
        if (blackStoneImg == null || whiteStoneImg == null) {
            return;
        }

        // 获取棋盘绘制区域
        Rectangle bounds = getBoardDrawBounds();
        if (bounds.width == 0 || bounds.height == 0) {
            return; // 棋盘未绘制
        }

        Graphics2D g2 = (Graphics2D) g.create();

        // 计算每个格子的像素大小(考虑边缘,与pixelToBoard保持一致)
        double gridWidth = bounds.width / 21.25;
        double gridHeight = bounds.height / 21.25;

        // 计算棋盘中心点的像素坐标
        double boardCenterX = bounds.x + bounds.width / 2.0;
        double boardCenterY = bounds.y + bounds.height / 2.0;

        // 棋子大小(略小于格子以避免重叠,取格子宽度的85%)
        int stoneSize = (int) (Math.min(gridWidth, gridHeight) * 0.85);

        // 遍历棋盘状态,绘制所有棋子
        int[][] curStatus = boardModel.getCurStatus();
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                int pieceType = curStatus[i][j];
                if (pieceType == GoBoard.EMPTY) {
                    continue; // 空位不绘制
                }

                // 计算逻辑坐标相对于中心点(10,10)的偏移
                double offsetX = (i - 10) * gridWidth;
                double offsetY = (j - 10) * gridHeight;

                // 计算棋子中心点的像素坐标
                int centerX = (int) Math.round(boardCenterX + offsetX);
                int centerY = (int) Math.round(boardCenterY + offsetY);

                // 计算棋子左上角坐标(使其居中)
                int x = centerX - stoneSize / 2;
                int y = centerY - stoneSize / 2;

                // 根据棋子类型选择图片
                BufferedImage stoneImg = (pieceType == GoBoard.BLACK) ? blackStoneImg : whiteStoneImg;

                // 绘制棋子(缩放到计算好的尺寸)
                g2.drawImage(stoneImg, x, y, stoneSize, stoneSize, null);
            }
        }

        // TODO: 绘制辅助线(如果auxiliaryLinePos不为null)
        // 示例: 在auxiliaryLinePos位置绘制半透明预览棋子或十字线

        g2.dispose();
    }

    /**
     * 触发重绘(供Controller在棋盘状态更新后调用)
     */
    public void refreshBoard() {
        repaint();
    }

    // ========== 模式切换方法 ==========
    /**
     * 获取当前面板模式
     */
    public PanelMode getCurrentMode() {
        return currentMode;
    }

    /**
     * 进入复盘模式
     * 游戏结束后调用,展示复盘界面
     */
    public void enterReplayMode() {
        // 创建复盘控制器
        replayController = new ReplayController(boardModel);

        // 移除鼠标监听器(禁止落子操作)
        if (mouseController != null) {
            this.removeMouseListener(mouseController);
        }

        // 切换按钮面板显示
        if (componentsAdder != null) {
            componentsAdder.switchToReplayMode();
        }

        // 更新模式状态
        currentMode = PanelMode.REPLAY;

        // 跳转到棋局开始(清空棋盘)
        if (replayController != null) {
            replayController.jumpToFirst();
            refreshBoard();
        }
    }

    /**
     * 退出复盘模式
     * 返回主界面或重新开始游戏时调用
     */
    public void exitReplayMode() {
        // 清理复盘控制器
        replayController = null;

        // 恢复鼠标监听器(允许落子操作)
        if (mouseController != null && currentMode == PanelMode.REPLAY) {
            this.addMouseListener(mouseController);
        }

        // 切换按钮面板显示
        if (componentsAdder != null) {
            componentsAdder.switchToPlayingMode();
        }

        // 更新模式状态
        currentMode = PanelMode.PLAYING;
    }

    /**
     * 获取复盘控制器(供按钮事件调用)
     */
    public ReplayController getReplayController() {
        return replayController;
    }

    /**
     * 获取UI组件管理器(供按钮控制器使用)
     */
    public GoComponentsAdder getComponentsAdder() {
        return componentsAdder;
    }
}