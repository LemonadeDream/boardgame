package com.github.lemonadedream.boardgame.controller.network;

import java.util.List;

/**
 * 服务器广播的落子消息(权威)
 */
public class MoveBroadcastMessage extends NetworkMessage {
    public int x;
    public int y;
    public int c;
    public List<int[]> captured; // 简单表示捕子坐标列表: 每个元素为 {x,y}

    public MoveBroadcastMessage(long seq, String playerId, int x, int y, int color, List<int[]> captured) {
        super("MOVE_BC");
        this.s = seq;
        this.p = playerId;
        this.x = x;
        this.y = y;
        this.c = color;
        this.captured = captured;
    }
}
