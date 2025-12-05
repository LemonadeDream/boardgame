package com.github.lemonadedream.boardgame.controller.network;

import javax.swing.SwingUtilities;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;

/**
 * 完整的 NetController 实现 - 客户端本地适配器
 */
public class NetControllerImpl implements GameClientImpl.ClientListener {
    private final GoPanel panel;
    private final String playerId;
    private final RemoteMoveHandler moveHandler;
    private int localColor = 1; // 假设初始为黑棋

    public NetControllerImpl(GoPanel panel, String playerId) {
        this.panel = panel;
        this.playerId = playerId;
        this.moveHandler = new RemoteMoveHandler(this);

        // 注册到 NetworkManager 的客户端
        NetworkManager nm = NetworkManager.getInstance();
        if (nm.getClient() != null) {
            nm.getClient().setListener(this);
        }
    }

    /**
     * 在第二次点击时被调用: 将 MOVE_REQ 发往服务器
     */
    public void requestMove(int x, int y) {
        MoveRequestMessage req = new MoveRequestMessage(playerId, x, y, localColor);
        NetworkManager.getInstance().send(req);
        System.out.println("[NetController] Requested move at (" + x + "," + y + ")");

        // 在实际实现中: 禁用本地输入并显示等待提示
        // TODO: panel.showWaitingState();
    }

    /**
     * 当收到服务器广播后调用
     */
    public void onMoveBroadcast(MoveBroadcastMessage m) {
        System.out.println("[NetController] Move broadcast: (" + m.x + "," + m.y + ") color=" + m.c);
        SwingUtilities.invokeLater(() -> {
            panel.applyRemoteMove(m.x, m.y, m.c);
            // 切换颜色
            localColor = 3 - localColor;
        });
    }

    @Override
    public void onMessage(NetworkMessage msg) {
        if (msg instanceof MoveBroadcastMessage) {
            onMoveBroadcast((MoveBroadcastMessage) msg);
        } else if (msg instanceof StateMessage) {
            System.out.println("[NetController] State message received");
        }
    }

    @Override
    public void onDisconnected() {
        System.out.println("[NetController] Disconnected from server");
        // TODO: 显示断线提示
    }
}
