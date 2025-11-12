package com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents;

public class GoBoard {
    // 棋盘大小（包括墙）
    public static final int BOARD_SIZE = 21;
    // 最大步数
    public static final int MAX_STEP = 1000;

    // 位置状态常量
    public static final int WALL = -1;
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;
    public static final int MUTUAL_PLACE = 100;

    // 记录当前的棋盘状态，每个位置是棋子的编号
    public int[][] curStatus = new int[BOARD_SIZE][BOARD_SIZE];
    // 记录历史棋盘状态的栈
    public int stackTop = -1;
    public int[][][] historyStatusStack = new int[MAX_STEP][BOARD_SIZE][BOARD_SIZE];
    // 存放双方的悔棋次数
    public int[] undoCount = { 1, 1 };

    // 取出当前棋盘状态
    public int[][] getCurStatus() {
        return curStatus;
    }

    // 取出历史棋盘状态栈顶
    public int[][] getHistoryStatusTop() {
        if (stackTop >= 0)
            return historyStatusStack[stackTop];
        else
            return null;
    }

    // 将当前状态加入到历史状态栈中
    public void boardStatusPush() {
        ++stackTop;
        for (int i = 1; i <= 19; i++)
            for (int j = 1; j <= 19; j++)
                historyStatusStack[stackTop][i][j] = curStatus[i][j];
    }

    // 将历史状态栈顶恢复成当前的状态
    public void boardStatusPop() {
        if (stackTop >= 0) {
            for (int i = 1; i <= 19; i++)
                for (int j = 1; j <= 19; j++)
                    curStatus[i][j] = historyStatusStack[stackTop][i][j];
            --stackTop;
        }
    }

    // 仅弹出栈顶，不恢复状态（用于跳过某个历史状态）
    public void boardStatusPopOnly() {
        if (stackTop >= 0) {
            --stackTop;
        }
    }

    /**
     * 将坐标x，y的棋子放进或移出当前状态
     * 
     * @param x      棋盘横坐标
     * @param y      棋盘纵坐标
     * @param color  棋子颜色
     * @param method 操作方法（1放进0移出）
     */
    public void piecesChange(int x, int y, int color, int method) {
        if (x < 0 || y < 0 || x >= BOARD_SIZE || y >= BOARD_SIZE)
            return;
        if (method == 1) {
            curStatus[x][y] = color;
        } else if (method == 0) {
            curStatus[x][y] = EMPTY;
        }
    }

    public GoBoard() {
        // 初始化棋盘，边界为墙，内部为空
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (i == 0 || i == BOARD_SIZE - 1 || j == 0 || j == BOARD_SIZE - 1) {
                    curStatus[i][j] = WALL;
                } else {
                    curStatus[i][j] = EMPTY;
                }
            }
        }
    }
}