package com.github.lemonadedream.boardgame.module.GoGameModel.GoLogic;

import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;

public class GoWinLose {
    GoPlaceProcessor pro;
    int[][] board;
    boolean[][] visited;
    int[][] status;

    // 构造函数，传入落子处理器实例
    public GoWinLose(GoPlaceProcessor processor) {
        this.pro = processor;
        this.visited = new boolean[21][21];
        this.board = processor.getBoardModel().getCurStatus();
        this.status = new int[21][21];
    }

    /**
     * 计算胜负已分时双方棋子数量
     */
    public void PiecesCount() {
        int[][] board = pro.getBoardModel().getCurStatus();
        // 空位预处理, 只有在棋子超过1 / 2的时候才进行
        int total = 0;
        for (int i = 1; i <= 19; i++)
            for (int j = 1; j <= 19; j++)
                if (board[i][j] == GoBoard.BLACK || board[i][j] == GoBoard.WHITE)
                    total++;
        if (total >= 181) {
            for (int i = 1; i <= 19; i++) {
                for (int j = 1; j <= 19; j++) {
                    if (board[i][j] == GoBoard.EMPTY && (status[i][j] == 0))
                        dfs(i, j);
                }
            }
        }

        // 计算双方棋子数量
        float blackCount = 0;
        float whiteCount = 0;
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                if (board[i][j] == GoBoard.BLACK || status[i][j] == 1) {
                    blackCount++;
                } else if (board[i][j] == GoBoard.WHITE || status[i][j] == 2) {
                    whiteCount++;
                } else if (status[i][j] == 3) {
                    blackCount += 0.5;
                    whiteCount += 0.5;
                }
            }
        }

        System.out.println("黑方棋子数: " + blackCount + "减去3.5数后 " + (blackCount - 3.5));
        System.out.println("白方棋子数: " + whiteCount);
        // TODO ： 显示结果到界面上
    }

    /**
     * 确定一个空点的状态,同时更新status, 1表示黑,2表示白,3表示共同
     */
    void dfs(int x, int y) {
        int direct[][] = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        visited[x][y] = true;
        boolean libertyBlack = false;
        boolean libertyWhite = false;
        for (int i = 0; i < 4; i++) {
            int newX = x + direct[i][0];
            int newY = y + direct[i][1];
            if (newX >= 1 && newX <= 19 && newY >= 1 && newY <= 19 && !visited[newX][newY]) {
                if (board[newX][newY] == GoBoard.EMPTY) {
                    dfs(newX, newY);
                } else if (board[newX][newY] == GoBoard.BLACK) {
                    libertyBlack = true;
                    visited[newX][newY] = true;
                } else if (board[newX][newY] == GoBoard.WHITE) {
                    libertyWhite = true;
                    visited[newX][newY] = true;
                }
            }
        }

        if (libertyBlack && !libertyWhite) {
            status[x][y] = 1;
        } else if (!libertyBlack && libertyWhite) {
            status[x][y] = 2;
        } else if (libertyBlack && libertyWhite) {
            status[x][y] = 3;
        } else {
            status[x][y] = 0;
        }
    }

    /**
     * 判断指定颜色的玩家是否无法落子（遍历所有点检查是否存在合法落子）
     * 注意：此方法只检查"是否无法落子"，不检查时间、认输等其他结束条件
     * 
     * @param color 要检查的颜色（GoBoard.BLACK 或 GoBoard.WHITE）
     * @return 如果该颜色无法落子返回 true，否则返回 false
     */
    public boolean hasNoLegalMove(int color) {
        // 遍历棋盘上所有位置，检查是否存在至少一个合法落子
        for (int i = 1; i <= 19; i++) {
            for (int j = 1; j <= 19; j++) {
                // check 返回 0 表示合法落子
                if (pro.check(i, j, color, 0) == 0) {
                    return false; // 发现合法落子，该颜色可以继续下棋
                }
            }
        }
        // 遍历完所有位置都无法落子
        return true;
    }
}