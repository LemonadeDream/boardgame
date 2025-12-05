package com.github.lemonadedream.boardgame.controller.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingUtilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoComponents.GoBoard;
import com.github.lemonadedream.boardgame.module.GoGameModel.GoLogic.GoPlaceProcessor;

/**
 * 完整的 GameServer 实现 - 权威棋盘维护、规则校验、消息广播
 */
public class GameServerImpl {
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private final ConcurrentHashMap<String, ClientContext> clients = new ConcurrentHashMap<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private Thread acceptorThread;
    private final GoBoard boardModel = new GoBoard();
    private long moveSeq = 0;
    private int currentColor = GoBoard.BLACK;

    private static class ClientContext {
        String playerId;
        Socket socket;
        PrintWriter out;
        BufferedReader in;

        ClientContext(String playerId, Socket socket) throws IOException {
            this.playerId = playerId;
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        void close() {
            try {
                if (socket != null)
                    socket.close();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public GameServerImpl(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                running = true;
                System.out.println("[GameServer] Started on port " + port);

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String clientAddr = clientSocket.getInetAddress().getHostAddress();
                        System.out.println("[GameServer] Client connected: " + clientAddr);

                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String firstLine = in.readLine();
                        if (firstLine != null) {
                            JsonObject obj = JsonParser.parseString(firstLine).getAsJsonObject();
                            String playerId = obj.has("p") ? obj.get("p").getAsString()
                                    : "player_" + System.currentTimeMillis();

                            ClientContext ctx = new ClientContext(playerId, clientSocket);
                            clients.put(playerId, ctx);
                            System.out.println("[GameServer] Client registered: " + playerId);

                            // 发送当前棋盘状态
                            sendStateMessage(ctx);

                            // 处理该客户端的消息
                            pool.execute(() -> handleClient(ctx));
                        }
                    } catch (IOException e) {
                        if (running)
                            e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendStateMessage(ClientContext ctx) {
        JsonObject stateObj = new JsonObject();
        stateObj.addProperty("board", "init");
        stateObj.addProperty("seq", moveSeq);

        StateMessage stateMsg = new StateMessage(stateObj.toString());
        stateMsg.s = moveSeq;
        stateMsg.p = "server";
        String json = serializeMessage(stateMsg);
        ctx.out.println(json);
        System.out.println("[GameServer] Sent STATE to " + ctx.playerId);
    }

    private void handleClient(ClientContext ctx) {
        try {
            String line;
            while ((line = ctx.in.readLine()) != null) {
                System.out.println("[GameServer] Received from " + ctx.playerId + ": " + line);
                NetworkMessage msg = deserializeMessage(line);
                if (msg instanceof MoveRequestMessage) {
                    handleMoveRequest(ctx, (MoveRequestMessage) msg);
                }
            }
        } catch (IOException e) {
            System.err.println("[GameServer] Client " + ctx.playerId + " disconnected");
        } finally {
            clients.remove(ctx.playerId);
            ctx.close();
        }
    }

    private void handleMoveRequest(ClientContext ctx, MoveRequestMessage moveReq) {
        synchronized (boardModel) {
            // 验证落子合法性
            GoPlaceProcessor processor = new GoPlaceProcessor(boardModel);
            int checkRes = processor.check(moveReq.x, moveReq.y, moveReq.c, 1);

            if (checkRes == 0) {
                // 落子成功，分配 seq 并广播
                moveSeq++;
                currentColor = 3 - currentColor;

                MoveBroadcastMessage broadcast = new MoveBroadcastMessage(moveSeq, ctx.playerId, moveReq.x, moveReq.y,
                        moveReq.c, null);
                String json = serializeMessage(broadcast);

                // 广播给所有客户端
                for (ClientContext client : clients.values()) {
                    client.out.println(json);
                    System.out.println("[GameServer] Broadcast to " + client.playerId);
                }
            } else {
                // 落子失败，返回错误或当前棋盘状态
                System.out.println("[GameServer] Move rejected: code " + checkRes);
                sendStateMessage(ctx);
            }
        }
    }

    private String serializeMessage(NetworkMessage msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("t", msg.t);
        obj.addProperty("s", msg.s);
        obj.addProperty("p", msg.p);
        obj.addProperty("ts", msg.ts);

        if (msg instanceof MoveBroadcastMessage) {
            MoveBroadcastMessage m = (MoveBroadcastMessage) msg;
            JsonObject data = new JsonObject();
            data.addProperty("x", m.x);
            data.addProperty("y", m.y);
            data.addProperty("c", m.c);
            obj.add("d", data);
        } else if (msg instanceof StateMessage) {
            StateMessage m = (StateMessage) msg;
            obj.add("d", JsonParser.parseString(m.boardPayload));
        }

        return obj.toString();
    }

    private NetworkMessage deserializeMessage(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String type = obj.get("t").getAsString();
            long seq = obj.has("s") ? obj.get("s").getAsLong() : 0;
            String playerId = obj.has("p") ? obj.get("p").getAsString() : "";

            if ("MOVE_REQ".equals(type)) {
                JsonObject data = obj.getAsJsonObject("d");
                int x = data.get("x").getAsInt();
                int y = data.get("y").getAsInt();
                int c = data.get("c").getAsInt();
                return new MoveRequestMessage(playerId, x, y, c);
            }
        } catch (Exception e) {
            System.err.println("[GameServer] Deserialize error: " + e.getMessage());
        }
        return null;
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
            for (ClientContext ctx : clients.values()) {
                ctx.close();
            }
            pool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
