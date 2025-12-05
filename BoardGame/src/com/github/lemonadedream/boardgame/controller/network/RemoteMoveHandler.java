package com.github.lemonadedream.boardgame.controller.network;

/**
 * 负责将解析到的网络消息分发到 NetController 的简单处理器
 */
public class RemoteMoveHandler {
    private final NetControllerImpl netController;

    public RemoteMoveHandler(NetControllerImpl netController) {
        this.netController = netController;
    }

    public void handle(NetworkMessage msg) {
        if (msg == null)
            return;
        if (msg instanceof MoveBroadcastMessage) {
            netController.onMoveBroadcast((MoveBroadcastMessage) msg);
        }
        // TODO: 处理 STATE/ACK/ERROR 等
    }
}
