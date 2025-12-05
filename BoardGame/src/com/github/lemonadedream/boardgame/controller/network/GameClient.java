package com.github.lemonadedream.boardgame.controller.network;

import java.io.IOException;

/**
 * 客户端骨架: 管理 socket 连接、读写线程与回调
 * 如何使用:
 * - 创建 `new GameClient(host,port,playerId)`
 * - 调用 `connect()` 启动连接（在后台线程）
 * - 注册 `ClientListener` 以接收消息回调
 */
public class GameClient {
    private final String host;
    private final int port;
    private final String playerId;

    public interface ClientListener {
        void onMessage(NetworkMessage msg);

        void onDisconnected();
    }

    private ClientListener listener;

    public GameClient(String host, int port, String playerId) {
        this.host = host;
        this.port = port;
        this.playerId = playerId;
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public void connect() throws IOException {
        // TODO: 在此实现真实 socket 连接与 reader/writer 线程
        throw new UnsupportedOperationException("connect not implemented in skeleton");
    }

    public void disconnect() {
        // TODO: 关闭 socket 与线程
    }

    public void send(NetworkMessage msg) {
        // TODO: 将消息序列化并写入 socket 输出流(异步)
    }
}
