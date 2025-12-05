package com.github.lemonadedream.boardgame.controller.network;

/**
 * 完整棋盘状态同步消息(服务器->客户端)
 */
public class StateMessage extends NetworkMessage {
    // 为简单起见, 以字符串或序列化数组表示棋盘
    public String boardPayload;

    public StateMessage(String boardPayload) {
        super("STATE");
        this.boardPayload = boardPayload;
    }
}
