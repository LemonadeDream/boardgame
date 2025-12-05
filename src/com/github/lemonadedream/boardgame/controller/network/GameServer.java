package com.github.lemonadedream.boardgame.controller.network;

/**
 * 最小 GameServer 骨架: 提供接口和注释，便于后续实现
 * 使用说明:
 * - new GameServer(port)
 * - 调用 start() 在后台线程中监听并接受客户端
 * - 在收到 MoveRequestMessage 时调用 ServerGame.applyMove(...) 验证并广播
 * MoveBroadcastMessage
 */
public class GameServer {
    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        // TODO: 在此实现 ServerSocket 接受连接与客户端会话管理
        throw new UnsupportedOperationException("GameServer.start not implemented in skeleton");
    }

    public void stop() {
        // TODO: 停止监听并关闭资源
    }
}
