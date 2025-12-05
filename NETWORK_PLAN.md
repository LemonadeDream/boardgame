# 围棋联网（TCP，服务器权威）总体计划

此文档记录采用“方案 A（仅在第二次点击发送落子请求）”并使用服务器权威策略的实现计划。目标是对现有代码库的侵入最小化，保留现有 UI（双击/二次点击落子交互）和棋局判断逻辑（复用 `GoPlaceProcessor`）。

目录

-   概览
-   架构与消息协议
-   新增类清单（签名级别）
-   精确接入点与需要修改的文件/方法
-   线程/EDT 注意事项
-   测试计划
-   实施顺序

## 概览

-   客户端保留“第一次点击定位、第二次点击确认”的交互；当且仅当用户在第二次点击时，客户端发送 `MoveRequestMessage` 到服务器。
-   服务器维护权威棋盘状态并复用现有 `GoPlaceProcessor`（同一套规则实现），验证请求并在应用成功后广播 `MoveBroadcastMessage`（包含 seq、坐标、颜色、捕子等信息）。
-   客户端在收到 `MoveBroadcastMessage` 后在 EDT 上调用现有的落子逻辑以更新 UI（通过 `GoPanel.applyRemoteMove(...)` 或直接调用 `GoPlaceProcessor` 的应用函数）。

## 架构与消息协议（简要）

-   传输：TCP sockets，文字 JSON（便于调试）。
-   每条消息包含字段：
    -   `t` : 类型 ("MOVE_REQ","MOVE_BC","STATE","ACK","PING","ERROR")
    -   `s` : seq（服务器分配的序号，客户端发送的请求可为 0）
    -   `p` : playerId
    -   `ts`: timestamp
    -   `d` : data (类型特定负载)

示例：

```
{"t":"MOVE_REQ","s":0,"p":"playerA","ts":1690000000000,"d":{"x":10,"y":5,"c":1}}
{"t":"MOVE_BC","s":123,"p":"server","ts":1690000001000,"d":{"x":10,"y":5,"c":1,"captured":[{"x":11,"y":5}]}}
```

## 新增类清单（签名级别）

（包路径建议：`com.github.lemonadedream.boardgame.controller.network`）

-   `NetworkMessage` (抽象)

    -   fields: `String t; long s; String p; long ts; JsonObject d;`
    -   methods: `String toJson()`, `static NetworkMessage fromJson(String json)`

-   `MoveRequestMessage extends NetworkMessage`

    -   constructor: `MoveRequestMessage(String playerId,int x,int y,int color)`

-   `MoveBroadcastMessage extends NetworkMessage`

    -   fields in `d`: `int x, int y, int c, List<Capture> captured`
    -   constructor: `MoveBroadcastMessage(long seq, String playerId,int x,inty,int color,List<Capture> captured)`

-   `StateMessage extends NetworkMessage`

    -   used for full-board sync on connect/reconnect

-   `AckMessage`, `ErrorMessage`, `PingMessage` 简单工具类

-   `GameServer`

    -   `public GameServer(int port)`
    -   `public void start()`
    -   `public void stop()`
    -   `private void handleClientMessage(ConnectionContext ctx, NetworkMessage msg)`
    -   维护：`ServerSocket serverSocket; ConcurrentMap<String,ConnectionContext> clients; ServerGame serverGame; ExecutorService pool;`

-   `ServerGame`（server 端的棋局模型桥接）

    -   fields: `Board board; GoPlaceProcessor logic;`（复用现有 model）
    -   `public MoveResult applyMove(String playerId,int x,int y,int color)` -> 返回是否成功、捕子列表等

-   `GameClient`

    -   `public GameClient(String host,int port,String playerId)`
    -   `public void connect()`
    -   `public void disconnect()`
    -   `public void send(NetworkMessage msg)`
    -   listener 接口 `ClientListener` 用于分发收到的 `NetworkMessage`

-   `NetworkManager`（单例）

    -   `public static NetworkManager getInstance()`
    -   `public void startServer(int port)` / `public void startClient(String host,int port,String playerId)`
    -   `public void stop()` / `public boolean isNetworkMode()`
    -   `public void send(NetworkMessage msg)`

-   `NetController`（适配器，在客户端）

    -   构造: `public NetController(GoPanel panel, String playerId)`
    -   `public void requestMove(int x,inty)` // 在第二次点击时调用
    -   `public void onMoveBroadcast(MoveBroadcastMessage m)` // 从 GameClient 回调，在 EDT 上调用 panel.applyRemoteMove(...)
    -   `private AtomicLong pendingSeq;` 等

-   `RemoteMoveHandler`

    -   `public void handle(NetworkMessage msg)` -> 分派到 `onMoveBroadcast` 等

-   `ConnectionDialog` (Swing)
    -   `public ConnectionConfig showDialog()` 包含 server/client 选择、host、port

## 精确接入点（最小修改，文件与方法）

-   `GoPanel` (`src/com/github/lemonadedream/boardgame/view/panel/mainGamePanel/GoPanel.java`)

    -   新增字段与方法：
        -   `private NetController netController;`
        -   `public void setNetController(NetController nc)` — 保存引用
        -   `public void applyRemoteMove(int x,int y,int color)` — 在 EDT 上调用现有落子应用方法（例如调用内部的 `applyPlace(x,y,color)` 或通过 `GoPlaceProcessor`）
    -   说明：`applyRemoteMove` 应调用与本地落子相同的更新路径以保证 UI/状态一致。

-   `GoBoardMouseController` (`src/com/github/lemonadedream/boardgame/controller/GoBoardMouseController.java`)

    -   在处理“第二次点击确认”逻辑的分支（当前代码处直接调用 `GoPlaceProcessor.check(x,y,color,1)` 或等价方法）处作如下替换：
        -   原离线：`goPlaceProcessor.check(x,y,color,1);`
        -   网络模式：
            ```java
            if (NetworkManager.getInstance().isNetworkMode()) {
                // 禁用输入并在 UI 显示“等待确认”状态
                netController.requestMove(x,y);
            } else {
                goPlaceProcessor.check(x,y,color,1);
            }
            ```
    -   说明：`netController` 可通过 `goPanel.getNetController()` 或在 `GoBoardMouseController` 构造时注入。

-   `MainWindow.initGame()` (`src/com/github/lemonadedream/boardgame/view/MainWindow.java`)
    -   在 `goPanel = GoPanel.getGoPanel();` 之后加入：
        -   弹出 `ConnectionDialog`（或检查配置）获得 `ConnectionConfig`
        -   调用 `NetworkManager.getInstance().startServer(port)` 或 `startClient(host,port,playerId)`
        -   创建 `NetController nc = new NetController(goPanel, playerId)` 并调用 `goPanel.setNetController(nc)`

## 线程 / EDT 注意事项

-   所有 socket I/O（connect/read/write）必须在非-EDT 的线程上执行。
-   `GameClient` 的 reader thread 解析消息后通过 `RemoteMoveHandler` 在 EDT（`SwingUtilities.invokeLater(...)`）上调用 `GoPanel.applyRemoteMove(...)`。
-   发送消息通过 `BlockingQueue<NetworkMessage>` 与 writer thread 完成，避免在 EDT 上直接执行 `socket.write()`。

## 测试计划（本地调试允许同 JVM 启动 Server）

1. 本地端到端：在同一 JVM 启动 `GameServer`（端口 9000），再在两窗口启动客户端分别连接 `localhost:9000`，验证连接建立与初始 `StateMessage`。
2. 正常同步：客户端 A 第二次点击发送 `MOVE_REQ` -> 服务器验证并广播 -> 客户端 B 在 <200ms 内更新棋盘位置与颜色一致。
3. 并发冲突：同时发送两个 `MOVE_REQ` 到同一格，服务器以先到先服务为准并向失败方返回 `ERROR` 或 `StateMessage`。
4. 重连：模拟断开客户端 B，再重连，服务器应发送完整 `StateMessage` 完成同步。
5. 超时：客户端在请求后若 5s 无响应应回退到未点击状态并提示。

## 实施顺序（建议）

1. 新增消息类与 JSON 工具（小且可测）。
2. 实现 `GameClient` 与 `NetworkManager`（保持独立，先可用回环测试）。
3. 在客户端实现 `NetController` 与 `GoPanel` 的 `setNetController`/`applyRemoteMove`，并修改 `GoBoardMouseController` 的第二次点击分支为 `requestMove`（离线模式保留原逻辑）。
4. 实现 `GameServer`（最小版本：接收 `MOVE_REQ`，调用 `GoPlaceProcessor.check`，广播 `MOVE_BC`）。可先在同一 JVM 里运行以便调试。
5. 完成心跳/ACK/超时与 UI 提示。
6. 测试与修复并发/重连/一致性边界情况。

---

如需我把上述类骨架（`.java` 文件）逐个生成到 `src/com/github/lemonadedream/boardgame/controller/network/`，并自动修改 `GoPanel`、`GoBoardMouseController`、`MainWindow` 的声明点（只做接口与方法签名、注入点，不实现网络细节），我可以继续执行并附上变更补丁与说明。请回复“生成骨架”或“先到此为止”。
