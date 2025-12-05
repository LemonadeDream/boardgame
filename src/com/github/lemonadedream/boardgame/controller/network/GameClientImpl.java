package com.github.lemonadedream.boardgame.controller.network;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 完整的 GameClient 实现
 */
public class GameClientImpl {
    private final String host;
    private final int port;
    private final String playerId;

    public interface ClientListener {
        void onMessage(NetworkMessage msg);

        void onDisconnected();
    }

    private ClientListener listener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean connected = false;
    private final BlockingQueue<NetworkMessage> outboundQueue = new LinkedBlockingQueue<>();
    private Thread readerThread;
    private Thread writerThread;

    public GameClientImpl(String host, int port, String playerId) {
        this.host = host;
        this.port = port;
        this.playerId = playerId;
    }

    public void setListener(ClientListener listener) {
        this.listener = listener;
    }

    public void connect() throws IOException {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
                connected = true;
                System.out.println("[GameClient] Connected to " + host + ":" + port);

                // 启动读线程
                readerThread = new Thread(this::readLoop);
                readerThread.setDaemon(true);
                readerThread.start();

                // 启动写线程
                writerThread = new Thread(this::writeLoop);
                writerThread.setDaemon(true);
                writerThread.start();
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
                if (listener != null) {
                    SwingUtilities.invokeLater(() -> listener.onDisconnected());
                }
            }
        }).start();
    }

    private void readLoop() {
        try {
            String line;
            while (connected && (line = in.readLine()) != null) {
                System.out.println("[GameClient] Received: " + line);
                NetworkMessage msg = deserializeMessage(line);
                if (msg != null && listener != null) {
                    SwingUtilities.invokeLater(() -> listener.onMessage(msg));
                }
            }
        } catch (IOException e) {
            System.err.println("[GameClient] Read error: " + e.getMessage());
        } finally {
            connected = false;
            if (listener != null) {
                SwingUtilities.invokeLater(() -> listener.onDisconnected());
            }
        }
    }

    private void writeLoop() {
        try {
            while (connected) {
                try {
                    NetworkMessage msg = outboundQueue.take();
                    String json = serializeMessage(msg);
                    out.println(json);
                    System.out.println("[GameClient] Sent: " + json);
                } catch (InterruptedException e) {
                    if (!connected)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String serializeMessage(NetworkMessage msg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("t", msg.t);
        obj.addProperty("s", msg.s);
        obj.addProperty("p", msg.p);
        obj.addProperty("ts", msg.ts);

        if (msg instanceof MoveRequestMessage) {
            MoveRequestMessage m = (MoveRequestMessage) msg;
            JsonObject data = new JsonObject();
            data.addProperty("x", m.x);
            data.addProperty("y", m.y);
            data.addProperty("c", m.c);
            obj.add("d", data);
        } else if (msg instanceof MoveBroadcastMessage) {
            MoveBroadcastMessage m = (MoveBroadcastMessage) msg;
            JsonObject data = new JsonObject();
            data.addProperty("x", m.x);
            data.addProperty("y", m.y);
            data.addProperty("c", m.c);
            obj.add("d", data);
        }

        return obj.toString();
    }

    private NetworkMessage deserializeMessage(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            String type = obj.get("t").getAsString();
            long seq = obj.has("s") ? obj.get("s").getAsLong() : 0;
            String playerId = obj.has("p") ? obj.get("p").getAsString() : "";
            long ts = obj.has("ts") ? obj.get("ts").getAsLong() : System.currentTimeMillis();

            if ("MOVE_BC".equals(type)) {
                JsonObject data = obj.getAsJsonObject("d");
                int x = data.get("x").getAsInt();
                int y = data.get("y").getAsInt();
                int c = data.get("c").getAsInt();
                MoveBroadcastMessage msg = new MoveBroadcastMessage(seq, playerId, x, y, c, null);
                msg.ts = ts;
                return msg;
            } else if ("STATE".equals(type)) {
                StateMessage msg = new StateMessage(obj.getAsJsonObject("d").toString());
                msg.s = seq;
                msg.p = playerId;
                msg.ts = ts;
                return msg;
            }
        } catch (Exception e) {
            System.err.println("[GameClient] Deserialize error: " + e.getMessage());
        }
        return null;
    }

    public void disconnect() {
        connected = false;
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

    public void send(NetworkMessage msg) {
        if (connected) {
            outboundQueue.offer(msg);
        }
    }
}
