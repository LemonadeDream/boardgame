package com.github.lemonadedream.boardgame.controller.network;

import javax.swing.SwingUtilities;

import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;

/**
 * 客户端本地到网络的适配器(骨架)
 * 责任:
 * - 当用户在第二次点击时调用 requestMove(x,y)
 * - 接收来自 GameClient 的广播消息并在 EDT 上调用 GoPanel.applyRemoteMove
 */
public class NetController {
    private final GoPanel panel;
    private final String playerId;

    public NetController(GoPanel panel, String playerId) {
        this.panel = panel;
        this.playerId = playerId;
    }

    /**
     * 在第二次点击时被调用: 将 MOVE_REQ 发往服务器
     */
    public void requestMove(int x, int y) {
        // 构造消息并发送
        MoveRequestMessage req = new MoveRequestMessage(playerId, x, y, 0);
        NetworkManager.getInstance().send(req);

        // 在实际实现中: 禁用本地输入并显示等待提示
    }

    /**
     * 当收到服务器广播后调用(由 RemoteMoveHandler 或 GameClient 回调)
     */
    public void onMoveBroadcast(MoveBroadcastMessage m) {
        // 确保在 EDT 上调用 UI 更新
        SwingUtilities.invokeLater(() -> {
            panel.applyRemoteMove(m.x, m.y, m.c);
        });
    }
}
