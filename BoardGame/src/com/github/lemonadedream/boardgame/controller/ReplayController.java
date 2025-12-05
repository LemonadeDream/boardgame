package com.github.lemonadedream.boardgame.controller;

import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;

/**
 * 复盘控制器
 * 负责管理复盘过程中的棋谱播放
 */
public class ReplayController {
    private GoBoard boardModel;
    private int currentStep = -1; // 当前复盘到第几步（-1表示初始状态）
    private int maxStep; // 总步数

    public ReplayController(GoBoard boardModel) {
        this.boardModel = boardModel;
        this.maxStep = boardModel.stackTop;
        this.currentStep = maxStep; // 默认从最后一步开始
    }

    /**
     * 下一步
     * 
     * @return 是否成功前进
     */
    public boolean nextStep() {
        if (currentStep < maxStep) {
            currentStep++;
            restoreToStep(currentStep);
            return true;
        }
        return false;
    }

    /**
     * 上一步
     * 
     * @return 是否成功后退
     */
    public boolean prevStep() {
        if (currentStep > -1) {
            currentStep--;
            if (currentStep == -1) {
                clearBoard();
            } else {
                restoreToStep(currentStep);
            }
            return true;
        }
        return false;
    }

    /**
     * 跳到第一步
     */
    public void jumpToFirst() {
        currentStep = -1;
        clearBoard();
    }

    /**
     * 跳到最后一步
     */
    public void jumpToLast() {
        currentStep = maxStep;
        if (maxStep >= 0) {
            restoreToStep(maxStep);
        }
    }

    /**
     * 跳到指定步
     * 
     * @param step 步数（-1表示初始状态）
     */
    public void jumpToStep(int step) {
        if (step >= -1 && step <= maxStep) {
            currentStep = step;
            if (step == -1) {
                clearBoard();
            } else {
                restoreToStep(step);
            }
        }
    }

    /**
     * 恢复到指定步的棋盘状态
     */
    private void restoreToStep(int step) {
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                boardModel.curStatus[i][j] = boardModel.historyStatusStack[step][i][j];
            }
        }
    }

    /**
     * 清空棋盘
     */
    private void clearBoard() {
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                boardModel.curStatus[i][j] = GoBoard.EMPTY;
            }
        }
    }

    // Getter方法
    public int getCurrentStep() {
        return currentStep;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public boolean hasNextStep() {
        return currentStep < maxStep;
    }

    public boolean hasPrevStep() {
        return currentStep > -1;
    }
}
