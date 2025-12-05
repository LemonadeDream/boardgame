package com.github.lemonadedream.boardgame.controller.network;

/**
 * 简单确认消息骨架
 */
public class AckMessage extends NetworkMessage {
    public long forSeq;

    public AckMessage(long forSeq) {
        super("ACK");
        this.forSeq = forSeq;
    }
}
