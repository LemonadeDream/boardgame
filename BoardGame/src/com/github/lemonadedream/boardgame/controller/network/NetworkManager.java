package com.github.lemonadedream.boardgame.controller.network;

import java.io.IOException;

/**
 * 完整的 NetworkManager 单例 - 管理 server/client 生命周期
 */
public class NetworkManager {
    private static final NetworkManager INSTANCE = new NetworkManager();

    private GameClientImpl client;
    private GameServerImpl server;
    private boolean networkMode = false;
    private boolean isServerMode = false;

    private NetworkManager() {
    }

    public static NetworkManager getInstance() {
        return INSTANCE;
    }

    public void startClient(String host, int port, String playerId) {
        try {
            this.client = new GameClientImpl(host, port, playerId);
            this.networkMode = true;
            this.isServerMode = false;
            client.connect();
            System.out.println("[NetworkManager] Client started, connecting to " + host + ":" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(int port) {
        this.server = new GameServerImpl(port);
        this.networkMode = true;
        this.isServerMode = true;
        server.start();
        System.out.println("[NetworkManager] Server started on port " + port);
    }

    public void stop() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        if (server != null) {
            server.stop();
            server = null;
        }
        this.networkMode = false;
        this.isServerMode = false;
    }

    public boolean isNetworkMode() {
        return networkMode;
    }

    public boolean isServerMode() {
        return isServerMode;
    }

    public GameClientImpl getClient() {
        return client;
    }

    public void send(NetworkMessage msg) {
        if (client != null) {
            client.send(msg);
        }
    }

    public void setClientListener(GameClientImpl.ClientListener listener) {
        if (client != null) {
            client.setListener(listener);
        }
    }
}
