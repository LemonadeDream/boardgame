package com.github.lemonadedream.boardgame.controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.view.component.TimePiece;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;

/**
 * GoGameController: 围棋游戏按钮控制器
 * 负责处理游戏中的各种按钮事件：暂停、悔棋、认输、充值、设置、成就等
 */
public class GoGameButtonController implements ActionListener {
    private MainWindow mainWindow;
    private GoPanel goPanel;
    private GoComponentsAdder componentsAdder;
    private boolean isPaused = false; // 游戏暂停状态

    public GoGameButtonController(MainWindow mainWindow, GoPanel goPanel, GoComponentsAdder componentsAdder) {
        this.mainWindow = mainWindow;
        this.goPanel = goPanel;
        this.componentsAdder = componentsAdder;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        // 检查当前面板模式
        GoPanel.PanelMode currentMode = goPanel.getCurrentMode();

        // 复盘模式下只允许退出和导航按钮
        if (currentMode == GoPanel.PanelMode.REPLAY) {
            switch (command) {
                case "exit":
                    handleExit();
                    break;
                case "replay_prev":
                    handleReplayPrev();
                    break;
                case "replay_next":
                    handleReplayNext();
                    break;
                default:
                    // 复盘模式下忽略其他按钮
                    break;
            }
            return;
        }

        // 对局模式下的按钮处理
        switch (command) {
            case "undo":
                handleUndo(1);
                break;
            case "exit":
                handleExit();
                break;
            case "surrender":
                handleSurrender();
                break;
            case "music_prev":
                handleMusicPrevious();
                break;
            case "music_mute":
                handleMusicMute();
                break;
            case "music_next":
                handleMusicNext();
                break;
            default:
                break;
        }
    }

    /**
     * 处理上一首按钮
     */
    private void handleMusicPrevious() {
        MusicPlayer.getInstance().previous();
    }

    /**
     * 处理静音按钮
     */
    private void handleMusicMute() {
        MusicPlayer.getInstance().toggleMute();
    }

    /**
     * 处理下一首按钮
     */
    private void handleMusicNext() {
        MusicPlayer.getInstance().next();
    }

    /**
     * 处理暂停按钮
     */
    private void handlePause(ActionEvent e) {
        if (!isPaused) {
            isPaused = true;

            // 暂停计时器
            TimePiece matchTimer = componentsAdder.getMatchTimePiece();
            TimePiece moveTimer = componentsAdder.getMoveTimePiece();

            if (matchTimer != null) {
                matchTimer.pause();
            }
            if (moveTimer != null) {
                moveTimer.pause();
            }

            // 修改按钮文本为"继续"
            JButton pauseButton = (JButton) e.getSource();
            pauseButton.setText("继续");

            // 可选：显示暂停提示
            JOptionPane.showMessageDialog(goPanel, "游戏已暂停", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 处理继续按钮（从暂停状态恢复）
     */
    private void handleResume(ActionEvent e) {
        if (isPaused) {
            isPaused = false;

            // 恢复计时器（使用start方法恢复）
            TimePiece matchTimer = componentsAdder.getMatchTimePiece();
            TimePiece moveTimer = componentsAdder.getMoveTimePiece();

            if (matchTimer != null) {
                matchTimer.start();
            }
            if (moveTimer != null) {
                moveTimer.start();
            }

            // 修改按钮文本回"暂停"
            JButton pauseButton = (JButton) e.getSource();
            pauseButton.setText("暂停");
        }
    }

    /**
     * 处理悔棋按钮
     * 只有当轮到自己（黑棋，先手）时才允许悔棋
     */
    private void handleUndo(int who) {
        if (isPaused) {
            JOptionPane.showMessageDialog(goPanel, "游戏暂停中，无法悔棋", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取当前轮到谁下棋
        int currentColor = goPanel.getMouseController().getCurrentColor();

        // 检查是否轮到自己（黑棋 = 1）
        if (currentColor != 1) { // 1 表示黑棋（先手玩家）
            JOptionPane.showMessageDialog(goPanel, "只能在轮到自己时悔棋", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查是否有足够的历史记录可以悔棋
        // 需要至少有2步棋才能悔棋（回退自己的一步 + 对方的一步）
        if (goPanel.getBoardModel().stackTop < 1) {
            JOptionPane.showMessageDialog(goPanel, "无法悔棋，当前没有足够的步数", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 检查悔棋次数
        if (goPanel.getBoardModel().undoCount[currentColor - 1] <= 0) {
            JOptionPane.showMessageDialog(goPanel, "悔棋次数已用尽", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 弹出确认对话框
        int confirm = JOptionPane.showConfirmDialog(
                goPanel,
                "确定要悔棋吗？将撤销你和对方的最后一步",
                "悔棋确认",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 减少悔棋次数
            goPanel.getBoardModel().undoCount[currentColor - 1]--;

            // 当前轮到黑棋，说明棋盘已经下完了白棋
            // 历史栈状态：[...] [黑2] [白2] <- stackTop
            // 目标：恢复到下黑2之前的状态

            // 第一次：弹出白2的状态（只减少栈顶指针，不恢复）
            goPanel.getBoardModel().boardStatusPopOnly();

            // 第二次：弹出黑2的状态（只减少栈顶指针，不恢复）
            goPanel.getBoardModel().boardStatusPopOnly();

            // 现在stackTop指向下黑2之前的状态，恢复该状态
            if (goPanel.getBoardModel().stackTop >= 0) {
                // 手动恢复棋盘状态
                int[][] historyStatus = goPanel.getBoardModel().historyStatusStack[goPanel.getBoardModel().stackTop];
                for (int i = 1; i <= 19; i++) {
                    for (int j = 1; j <= 19; j++) {
                        goPanel.getBoardModel().curStatus[i][j] = historyStatus[i][j];
                    }
                }
            }

            // 悔棋后仍然轮到黑棋（自己），所以不需要切换颜色

            // 更新回合数（回退2步棋，回合数减2）
            JLabel roundLabel = componentsAdder.getRoundLabel();
            if (roundLabel != null) {
                int currentRound = Integer.parseInt(roundLabel.getText());
                if (currentRound > 2) {
                    roundLabel.setText(String.valueOf(currentRound - 2));
                }
            }

            // 更新棋子数量（重新统计）
            updatePieceCount();

            // 刷新棋盘显示
            goPanel.refreshBoard();

            JOptionPane.showMessageDialog(goPanel, "已悔棋", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 更新棋子数量显示
     */
    private void updatePieceCount() {
        int blackCount = 0;
        int whiteCount = 0;
        int[][] status = goPanel.getBoardModel().getCurStatus();
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                if (status[i][j] == 1) { // BLACK
                    blackCount++;
                } else if (status[i][j] == 2) { // WHITE
                    whiteCount++;
                }
            }
        }

        JLabel pieceLabel = componentsAdder.getPieceCountLabel();
        if (pieceLabel != null) {
            pieceLabel.setText(String.format("黑:%d 白:%d", blackCount, whiteCount));
        }
    }

    /**
     * 处理认输按钮
     */
    private void handleSurrender() {
        // 弹出确认对话框
        int confirm = JOptionPane.showConfirmDialog(
                goPanel,
                "确定要认输吗？",
                "认输确认",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // 调用鼠标控制器的游戏结束方法
            goPanel.getMouseController().triggerGameEnd("认输");
        }
    }

    /**
     * 处理充值按钮
     */
    private void handleRecharge() {
        JOptionPane.showMessageDialog(goPanel, "充值功能暂未开放", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 处理设置按钮
     */
    private void handleSettings() {
        // 跳转到设置界面
        ActionEvent settingsEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "设置");
        mainWindow.switchPanel(settingsEvent);
    }

    /**
     * 处理成就按钮
     */
    private void handleAchievements() {
        // 跳转到成就界面
        ActionEvent achievementsEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "成就");
        mainWindow.switchPanel(achievementsEvent);
    }

    /**
     * 检查游戏是否暂停
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * 设置暂停状态（供外部调用）
     */
    public void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    // ========== 新增：复盘模式和退出按钮处理 ==========

    /**
     * 处理退出按钮
     * 无论当前是对局模式还是复盘模式，都返回主界面
     */
    private void handleExit() {
        // 如果在复盘模式，先退出复盘
        if (goPanel.getCurrentMode() == GoPanel.PanelMode.REPLAY) {
            goPanel.exitReplayMode();
        }

        // 返回主界面
        ActionEvent mainPanelEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "返回");
        mainWindow.switchPanel(mainPanelEvent);
    }

    /**
     * 处理复盘"上一步"按钮
     */
    private void handleReplayPrev() {
        ReplayController replayController = goPanel.getReplayController();
        if (replayController == null) {
            return;
        }

        if (replayController.hasPrevStep()) {
            replayController.prevStep();
            goPanel.refreshBoard();
            updateReplayInfo();
        } else {
            JOptionPane.showMessageDialog(goPanel, "已经是第一步了", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 处理复盘"下一步"按钮
     */
    private void handleReplayNext() {
        ReplayController replayController = goPanel.getReplayController();
        if (replayController == null) {
            return;
        }

        if (replayController.hasNextStep()) {
            replayController.nextStep();
            goPanel.refreshBoard();
            updateReplayInfo();
        } else {
            JOptionPane.showMessageDialog(goPanel, "已经是最后一步了", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * 更新复盘时的回合信息显示
     */
    private void updateReplayInfo() {
        ReplayController replayController = goPanel.getReplayController();
        if (replayController == null) {
            return;
        }

        // 更新回合标签显示当前步数
        JLabel roundLabel = componentsAdder.getRoundLabel();
        if (roundLabel != null) {
            roundLabel.setText(String.format("步数: %d/%d",
                    replayController.getCurrentStep(),
                    replayController.getMaxStep()));
        }

        // 更新棋子数量
        updatePieceCount();
    }
}
