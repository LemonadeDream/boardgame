package com.github.lemonadedream.boardgame.controller.network;

/**
 * 客户端 -> 服务器 的落子请求消息骨架
 */
public class MoveRequestMessage extends NetworkMessage {
    public int x;
    public int y;
    public int c; // color

    public MoveRequestMessage(String playerId, int x, int y, int color) {
        super("MOVE_REQ");
        this.p = playerId;
        this.x = x;
        this.y = y;
        this.c = color;
    }
}
