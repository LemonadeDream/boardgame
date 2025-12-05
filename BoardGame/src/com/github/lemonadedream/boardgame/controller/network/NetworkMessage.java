package com.github.lemonadedream.boardgame.controller.network;

/**
 * 抽象的网络消息基类（骨架）
 * 注意: 这是一个轻量骨架，用于实现联网流程的占位符。
 * 如何使用:
 * 子类实现具体消息 (MoveRequestMessage/MoveBroadcastMessage 等)
 * 使用 toJson()/fromJson() 序列化/反序列化（当前为占位实现，项目中可替换为 Gson/Jsonb）
 */
public abstract class NetworkMessage {
    public String t; // type
    public long s; // seq
    public String p; // playerId
    public long ts; // timestamp

    public NetworkMessage(String t) {
        this.t = t;
        this.ts = System.currentTimeMillis();
    }

    public String toJson() {
        // TODO: replace with proper JSON library (Gson/Jackson)
        return String.format("{\"t\":\"%s\",\"s\":%d,\"p\":\"%s\",\"ts\":%d}",
                t, s, p == null ? "" : p, ts);
    }

    public static NetworkMessage fromJson(String json) {
        // TODO: 实现反序列化（此处为占位）
        throw new UnsupportedOperationException("fromJson not implemented");
    }
}
