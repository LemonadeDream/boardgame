package com.github.lemonadedream.boardgame.module.GoGameModel.GoLogic;

import java.util.Stack;

import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;

/*
 * 处理围棋的落子逻辑
 */
public class GoPlaceProcessor {
    // 棋盘模型实例
    private GoBoard boardModel;
    // 临时的棋盘状态引用
    private int[][] board;

    // 构造函数，传入棋盘实例
    public GoPlaceProcessor(GoBoard boardModel) {
        this.boardModel = boardModel;
        this.board = boardModel.getCurStatus();
    }

    public GoBoard getBoardModel() {
        return this.boardModel;
    }

    /**
     * 分析在坐标和颜色下的合法性
     * 
     * @param x      落子点的x坐标
     * @param y      落子点的y坐标
     * @param color  落子颜色
     * @param method 落子方法，如果是0表示在初次判断落子合法性，1则表示已经判断过合法性的棋子（需要执行更新功能）
     * @return 返回一个数字判断落子的状态，根据状态判断合法，以及不合法的类型
     *         0-合法落子
     *         1-位置已被占用
     *         2-触犯了打劫规则
     *         3-落子构成的连通块没有气
     */
    public int check(int x, int y, int color, int method) {
        // 尝试落子
        if (board[x][y] != GoBoard.EMPTY) {
            return 1; // // 落子点已经有棋子了，位置已被占用
        }

        // 保存初始状态用于method=0时的恢复（必须在修改board之前保存）
        int[][][] capturedStates = null;
        if (method == 0) {
            // 创建棋盘状态的深拷贝
            capturedStates = new int[1][21][21];
            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 21; j++) {
                    capturedStates[0][i][j] = board[i][j];
                }
            }
        }

        // 临时更新用来判断后续逻辑合法性
        board[x][y] = color;

        // 第一步: 尝试吃掉周围无气的异色子
        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        boolean hasCaptured = false; // 记录是否发生了吃子
        for (int[] dir : directions) {
            int cx = x + dir[0];
            int cy = y + dir[1];
            if (board[cx][cy] == GoBoard.WALL)
                continue; // 越界跳过
            if (board[cx][cy] == 3 - color) {
                // 遇到异色子，检查其是否无气
                if (libertyCheck(cx, cy, 0) == false) {
                    // 异色子无气，删除其连通块
                    libertyCheck(cx, cy, 1);
                    hasCaptured = true; // 发生了吃子
                }
            }
        }

        // 第二步: 如果没有吃子，检查自己是否有气（禁止自杀）
        if (!hasCaptured) {
            if (libertyCheck(x, y, 0) == false) {
                if (method == 0) {
                    // 恢复整个棋盘状态
                    for (int i = 0; i < 21; i++) {
                        for (int j = 0; j < 21; j++) {
                            board[i][j] = capturedStates[0][i][j];
                        }
                    }
                }
                return 3;
            }
        }

        // 第三步: 检查是否触犯打劫规则
        if (koCheck() == true) {
            if (method == 0) {
                // 恢复整个棋盘状态
                for (int i = 0; i < 21; i++) {
                    for (int j = 0; j < 21; j++) {
                        board[i][j] = capturedStates[0][i][j];
                    }
                }
            }
            return 2;
        }

        if (method == 1) {
            // 更新处理后的状态
            for (int i = 1; i <= 19; i++)
                for (int j = 1; j <= 19; j++)
                    boardModel.curStatus[i][j] = board[i][j]; // 同步回棋盘模型
            boardModel.boardStatusPush(); // 将当前状态压入历史状态栈
        } else {
            // method=0时恢复棋盘状态
            for (int i = 0; i < 21; i++) {
                for (int j = 0; j < 21; j++) {
                    board[i][j] = capturedStates[0][i][j];
                }
            }
        }
        return 0; // 状态合法
    }

    /**
     * 判断是否触犯打劫规则
     */
    boolean koCheck() {
        // 遍历历史栈中的所有状态
        for (int top = 0; top <= boardModel.stackTop; top++) {
            int[][] koMatchedBoard = boardModel.historyStatusStack[top];
            boolean theSame = true;

            // 比较当前棋盘状态和历史状态（只比较有效区域1-19，不比较边界）
            for (int i = 1; i <= 19; i++) {
                for (int j = 1; j <= 19; j++) {
                    if (koMatchedBoard[i][j] != board[i][j]) {
                        theSame = false;
                        i = 100;
                        j = 100; // 跳出两层循环
                    }
                }
            }

            if (theSame) {
                return true; // 发现重复状态，触犯打劫规则
            }
        }

        return false; // 未发现重复状态
    }

    /**
     * 判断落子连通块周围的气是否存在
     * 
     * @param x      棋盘横坐标
     * @param y      棋盘纵坐标
     * @param method 检测方法，0无特殊操作，1表示删除此连通块连通块气
     */
    public boolean libertyCheck(int x, int y, int method) {
        /* 原则只要连通块附近有一个点有气就是有气 */
        // 记录每个点是否访问过
        boolean[] st = new boolean[21 * 21];
        // 初始化每个点都没有访问过
        for (int i = 0; i < 21 * 21; i++)
            st[i] = false;
        // 记录连通块的颜色
        int blockColor = board[x][y];
        // 广度优先遍历连通块
        Stack<Integer> tmpStack = new Stack<>(); // 栈储存遍历的点序列
        tmpStack.push(x * 21 + y);
        st[x * 21 + y] = true;
        while (tmpStack.empty() == false) {
            int code = tmpStack.pop();
            st[code] = true; // 标记已经访问
            int px = code / 21;
            int py = code % 21;

            // 检查四个方向
            int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
            for (int[] dir : directions) {
                int cx = px + dir[0];
                int cy = py + dir[1];
                if (board[cx][cy] == GoBoard.WALL)
                    continue; // 越界跳过
                if (board[cx][cy] == GoBoard.EMPTY) {
                    return true; // 有气直接中止
                } else if (board[cx][cy] == blockColor && st[cx * 21 + cy] == false) {
                    tmpStack.push(cx * 21 + cy); // 同色且未访问，加入栈中继续遍历
                }
            }
        }
        // 遍历结束没有找到气
        if (method == 1) {
            // 如果是要求删除的操作，则删除连通块的棋子
            for (int i = 1; i <= 19; i++) {
                for (int j = 1; j <= 19; j++) {
                    if (st[i * 21 + j]) {
                        board[i][j] = GoBoard.EMPTY; // 删除被标记过的连通块棋子
                    }
                }
            }
        }
        return false;
    }
}