package com.github.lemonadedream.boardgame.controller;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;

import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;
import com.github.lemonadedream.boardgame.view.component.TimePiece;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoLogic.GoPlaceProcessor;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoLogic.GoWinLose;

/**
 * 围棋棋盘鼠标控制器
 * 实现两次点击确认机制:
 * 1. 首次点击: 判断合法性 → 显示辅助线
 * 2. 二次点击同位置: 确认落子 → 更新棋盘 → 重绘
 */
public class GoBoardMouseController extends MouseAdapter {

    // 回调接口(用于通知外部组件落子事件)
    public interface BoardClickListener {
        void onBoardClicked(int row, int col, MouseEvent e);
    }

    // 游戏结束回调接口
    public interface GameEndListener {
        /**
         * 游戏结束回调
         * 
         * @param winnerColor 胜方颜色（GoBoard.BLACK 或 GoBoard.WHITE）
         * @param reason      结束原因（"无法落子"、"时间耗尽"、"认输"等）
         */
        void onGameEnd(int winnerColor, String reason);
    }

    private final GoPanel panel;
    private final BoardClickListener listener;
    private final GameEndListener gameEndListener;
    private final GoBoard boardModel;
    private final GoPlaceProcessor processor;
    private final GoWinLose winLoseChecker;

    // 时间控制器
    private final TimePiece matchTimer; // 对局总时间
    private final TimePiece moveTimer; // 单步用时

    // 状态管理: 待确认的落子位置(null表示无待确认落子)
    private Point pendingMove = null;
    // 当前落子颜色(1=黑, 2=白)
    private int currentColor = GoBoard.BLACK;
    // 是否已开始游戏(用于首次落子时启动计时器)
    private boolean gameStarted = false;

    /**
     * 构造函数
     * 
     * @param panel    围棋面板实例
     * @param listener 点击事件监听器(可为null)
     */
    public GoBoardMouseController(GoPanel panel, BoardClickListener listener) {
        this(panel, listener, null, null, null);
    }

    /**
     * 构造函数(带时间控制)
     * 
     * @param panel      围棋面板实例
     * @param listener   点击事件监听器(可为null)
     * @param matchTimer 对局总时间计时器(可为null)
     * @param moveTimer  单步用时计时器(可为null)
     */
    public GoBoardMouseController(GoPanel panel, BoardClickListener listener,
            TimePiece matchTimer, TimePiece moveTimer) {
        this(panel, listener, matchTimer, moveTimer, null);
    }

    /**
     * 完整构造函数(带时间控制和游戏结束监听)
     * 
     * @param panel           围棋面板实例
     * @param listener        点击事件监听器(可为null)
     * @param matchTimer      对局总时间计时器(可为null)
     * @param moveTimer       单步用时计时器(可为null)
     * @param gameEndListener 游戏结束监听器(可为null)
     */
    public GoBoardMouseController(GoPanel panel, BoardClickListener listener,
            TimePiece matchTimer, TimePiece moveTimer, GameEndListener gameEndListener) {
        this.panel = panel;
        this.listener = listener;
        this.gameEndListener = gameEndListener;
        this.boardModel = panel.getBoardModel();
        this.matchTimer = matchTimer;
        this.moveTimer = moveTimer;

        // 初始化处理器和胜负判断器
        this.processor = new GoPlaceProcessor(boardModel);
        this.winLoseChecker = new GoWinLose(processor);

        // 将控制器注册为面板的鼠标监听器
        this.panel.addMouseListener(this);

        // 注册步时耗尽监听器
        if (moveTimer != null) {
            moveTimer.setTimeExpiredListener(() -> {
                handleGameEnd(3 - currentColor, "步时耗尽");
            });
        }

        // 注册对局时间耗尽监听器
        if (matchTimer != null) {
            matchTimer.setTimeExpiredListener(() -> {
                handleGameEnd(3 - currentColor, "对局时间耗尽");
            });
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 将像素坐标转换为棋盘逻辑坐标
        Point boardPos = panel.pixelToBoard(e.getX(), e.getY());
        if (boardPos == null) {
            // 点击在棋盘外,清除待确认状态
            clearPendingMove();
            return;
        }

        int row = boardPos.x;
        int col = boardPos.y;

        // 判断是否为二次点击同一位置
        if (pendingMove != null && pendingMove.x == row && pendingMove.y == col) {
            // 二次点击确认: 执行落子并更新棋盘
            confirmMove(row, col);
        } else {
            // 首次点击或点击新位置: 检查合法性
            attemptMove(row, col);
        }

        // 不在这里通知监听器，而是在confirmMove成功后才通知
    }

    /**
     * 首次点击: 尝试落子并显示辅助线
     */
    private void attemptMove(int row, int col) {
        // 创建临时GoPlaceProcessor检查合法性(method=0,仅检查不更新)
        GoPlaceProcessor processor = new GoPlaceProcessor(boardModel);
        int checkResult = processor.check(row, col, currentColor, 0);

        if (checkResult == 0) {
            // 合法落子: 保存待确认位置并显示辅助线
            pendingMove = new Point(row, col);
            panel.showAuxiliaryLine(row, col);
            System.out.println("落子预览: (" + row + "," + col + "), 再次点击确认");
        } else {
            // 不合法: 清除待确认状态并提示错误
            clearPendingMove();
            String errorMsg = getErrorMessage(checkResult);
            System.err.println("非法落子: " + errorMsg);
            // TODO: 可通过UI提示用户(如弹窗或状态栏)
        }
    }

    /**
     * 二次点击: 确认落子并更新棋盘
     */
    private void confirmMove(int row, int col) {
        // 创建临时GoPlaceProcessor执行落子(method=1,更新棋盘状态)
        GoPlaceProcessor processor = new GoPlaceProcessor(boardModel);
        int checkResult = processor.check(row, col, currentColor, 1);

        if (checkResult == 0) {
            // === 时间控制逻辑 ===
            // 首次落子时启动对局计时器
            if (!gameStarted) {
                gameStarted = true;
                if (matchTimer != null) {
                    matchTimer.start();
                    System.out.println("对局计时器已启动");
                }
                if (moveTimer != null) {
                    moveTimer.start();
                    System.out.println("步时计时器已启动");
                }
            } else {
                // 非首次落子: 重置步时计时器
                if (moveTimer != null) {
                    moveTimer.stop(); // 停止并重置
                    moveTimer.start(); // 重新启动
                    System.out.println("步时计时器已重置");
                }
            }

            // 落子成功: 切换颜色、隐藏辅助线、触发重绘
            currentColor = 3 - currentColor; // 黑白切换(1?2)

            // 更新聊天区的当前发言方颜色
            if (panel.getComponentsAdder() != null) {
                panel.getComponentsAdder().setCurrentColor(currentColor);
            }

            clearPendingMove();
            panel.refreshBoard();

            // 通知监听器(落子成功后才调用)
            if (listener != null) {
                listener.onBoardClicked(row, col, null);
            }

            System.out.println("落子成功: (" + row + "," + col + ")");

            // === 胜负判断 ===
            checkGameEnd();

        } else {
            // 理论上不应出现(首次检查已通过),但仍处理异常情况
            clearPendingMove();
            System.err.println("落子失败(状态异常): " + getErrorMessage(checkResult));
        }
    }

    /**
     * 检查游戏是否结束
     */
    private void checkGameEnd() {
        // 时间耗尽由 TimePiece 监听器主动触发，这里只检查是否无法落子
        checkNoLegalMoves();
    }

    /**
     * 检查当前玩家是否无合法落子
     */
    private void checkNoLegalMoves() {
        // 使用 GoWinLose.hasNoLegalMove 判断当前颜色是否无法落子
        if (winLoseChecker.hasNoLegalMove(currentColor)) {
            // 当前玩家无法落子，则对方获胜
            int winner = 3 - currentColor;
            handleGameEnd(winner, "对方无法落子");
        }
    }

    /**
     * 处理游戏结束
     * 
     * @param winnerColor 胜方颜色
     * @param reason      结束原因
     */
    private void handleGameEnd(int winnerColor, String reason) {
        // 停止计时器
        if (matchTimer != null) {
            matchTimer.stop();
        }
        if (moveTimer != null) {
            moveTimer.stop();
        }

        // 计算双方棋子数（可选，用于显示详细信息）
        winLoseChecker.PiecesCount();

        String winnerName = (winnerColor == GoBoard.BLACK) ? "黑方" : "白方";
        System.out.println("游戏结束: " + winnerName + "获胜 (" + reason + ")");

        // 弹出对话框询问是否进入复盘
        int choice = JOptionPane.showConfirmDialog(
                panel,
                winnerName + "获胜！\n原因：" + reason + "\n\n是否进入复盘模式?",
                "游戏结束",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // 进入复盘模式
            panel.enterReplayMode();
            System.out.println("已进入复盘模式");
        } else {
            System.out.println("用户选择不进入复盘模式");
            // 不进入复盘，可以选择返回主界面
            // 这里可以添加返回主界面的逻辑
        }
    }

    /**
     * 手动触发游戏结束（供认输功能调用）
     * 
     * @param reason 结束原因
     */
    public void triggerGameEnd(String reason) {
        // 认输时，当前颜色输，对方赢
        int winner = 3 - currentColor;
        handleGameEnd(winner, reason);
    }

    /**
     * 清除待确认状态并隐藏辅助线
     */
    private void clearPendingMove() {
        pendingMove = null;
        panel.hideAuxiliaryLine();
    }

    /**
     * 根据GoPlaceProcessor返回码获取错误信息
     */
    private String getErrorMessage(int code) {
        switch (code) {
            case 1:
                return "位置已被占用";
            case 2:
                return "触犯打劫规则";
            case 3:
                return "落子后该连通块无气(禁止自杀)";
            default:
                return "未知错误";
        }
    }

    /**
     * 获取当前落子颜色
     */
    public int getCurrentColor() {
        return currentColor;
    }

    /**
     * 手动设置当前落子颜色(用于悔棋或重置)
     */
    public void setCurrentColor(int color) {
        if (color == GoBoard.BLACK || color == GoBoard.WHITE) {
            this.currentColor = color;
        }
    }

    /**
     * 切换当前落子颜色(用于悔棋)
     */
    public void switchColor() {
        this.currentColor = 3 - this.currentColor;
    }

    /**
     * 重置控制器状态(清除待确认落子)
     */
    public void reset() {
        clearPendingMove();
        currentColor = GoBoard.BLACK;
        gameStarted = false;

        // 重置计时器
        if (matchTimer != null) {
            matchTimer.stop();
        }
        if (moveTimer != null) {
            moveTimer.stop();
        }
    }

    /**
     * 暂停游戏(暂停计时器)
     */
    public void pauseGame() {
        if (matchTimer != null) {
            matchTimer.pause();
        }
        if (moveTimer != null) {
            moveTimer.pause();
        }
    }

    /**
     * 继续游戏(恢复计时器)
     */
    public void resumeGame() {
        if (gameStarted) {
            if (matchTimer != null) {
                matchTimer.start();
            }
            if (moveTimer != null) {
                moveTimer.start();
            }
        }
    }
}