# 围棋联网功能 - 使用指南

## 前置准备

### 1. 添加 Gson 库

本实现使用 Google Gson 进行 JSON 序列化/反序列化。

**方式 A: 使用 Maven（推荐）**
在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

**方式 B: 手动下载 JAR**

1. 从 [mvnrepository.com](https://mvnrepository.com/artifact/com.google.code.gson/gson) 下载 `gson-2.10.1.jar`
2. 将 JAR 放入项目的 `lib/` 目录
3. 在 IDE 中将 JAR 添加到 classpath（Eclipse/IntelliJ 都支持自动识别 `lib/` 目录）

### 2. 编译项目

如果使用 Maven：

```bash
mvn clean compile
```

如果使用 IDE，直接编译整个项目即可。

---

## 运行方式

### 两台电脑（不同主机）

#### PC-A（服务器）

1. 编译并运行游戏
2. 进入"围棋游戏"界面
3. 在 `ConnectionDialog` 中：
    - 选择 **"启动服务器"**
    - 端口保持 9000（或自定义）
    - 点击"连接"
4. 等待客户端连接

#### PC-B（客户端）

1. 编译并运行游戏
2. 进入"围棋游戏"界面
3. 在 `ConnectionDialog` 中：
    - 选择 **"连接到服务器"**
    - 输入 PC-A 的 IP 地址（例如 `192.168.1.100`）
    - 端口填写 9000
    - 玩家 ID 填写 `playerB`（或任意标识符）
    - 点击"连接"
4. 连接成功后可开始下棋

### 单台电脑（本地测试）

#### 终端 1（启动服务器）

```bash
java -cp bin:lib/gson-2.10.1.jar com.github.lemonadedream.boardgame.view.MainWindow
# 在 ConnectionDialog 中选择"启动服务器"，端口 9000
```

#### 终端 2（启动第一个客户端，黑棋）

```bash
java -cp bin:lib/gson-2.10.1.jar com.github.lemonadedream.boardgame.view.MainWindow
# 在 ConnectionDialog 中选择"连接到服务器"，host=localhost，port=9000，playerId=playerA
```

#### 终端 3（启动第二个客户端，白棋）

```bash
java -cp bin:lib/gson-2.10.1.jar com.github.lemonadedream.boardgame.view.MainWindow
# 在 ConnectionDialog 中选择"连接到服务器"，host=localhost，port=9000，playerId=playerB
```

---

## 游戏流程

1. **黑方（PC-A 或第一个客户端）** 第一次点击选择位置，第二次点击确认

    - 客户端发送 `MoveRequestMessage` 到服务器
    - 服务器验证（调用 `GoPlaceProcessor.check`）
    - 验证通过 → 分配 seq，广播 `MoveBroadcastMessage`
    - 两个客户端在 EDT 上应用落子并更新棋盘

2. **白方** 同样操作，落子被服务器验证并同步

3. 如果落子违法（被占用、打劫、自杀等）：
    - 服务器拒绝并返回 `StateMessage` 以同步状态

---

## 工作原理

### 架构

```
┌─────────────────┐           ┌──────────────────┐
│  GameClient (A) │ ──TCP──→  │                  │
└─────────────────┘           │                  │
                               │  GameServer      │
┌─────────────────┐ ──TCP──→  │                  │
│  GameClient (B) │           │  (权威棋盘)      │
└─────────────────┘           │                  │
                               └──────────────────┘
```

### 消息流

1. **客户端 A 落子：**

    - `NetController.requestMove(x, y)` → `MoveRequestMessage`
    - 发送到 `GameServer`

2. **服务器处理：**

    - `GameServer.handleMoveRequest()` → 调用 `GoPlaceProcessor.check(x,y,color,1)`
    - 验证通过 → `moveSeq++` → 广播 `MoveBroadcastMessage`

3. **客户端接收：**
    - `GameClient` reader thread 解析 JSON
    - 调用 `NetControllerImpl.onMessage()` → `onMoveBroadcast()`
    - 在 EDT 上调用 `GoPanel.applyRemoteMove(x, y, color)`
    - 棋盘更新并刷新视图

### JSON 消息格式

```json
// 客户端请求
{"t":"MOVE_REQ","s":0,"p":"playerA","ts":1690000000000,"d":{"x":10,"y":5,"c":1}}

// 服务器广播
{"t":"MOVE_BC","s":123,"p":"server","ts":1690000001000,"d":{"x":10,"y":5,"c":1}}

// 完整棋盘状态
{"t":"STATE","s":0,"p":"server","ts":1690000001000,"d":{"board":"init","seq":123}}
```

---

## 故障排查

### 问题 1：无法连接

-   检查防火墙是否允许 9000 端口（或自定义端口）
-   检查 PC-A 的 IP 地址是否正确
-   查看控制台是否有错误输出

### 问题 2：落子不同步

-   查看控制台输出 `[GameClient]`、`[GameServer]` 日志
-   确认 Gson JAR 已正确加载
-   检查 `GoPlaceProcessor.check()` 是否工作正常

### 问题 3：编译错误

-   确保 Gson JAR 在 classpath 中
-   检查 Java 版本（推荐 JDK 8+）
-   清理编译缓存后重新编译

---

## 扩展建议

1. **心跳与断线检测**：在 `GameClient` 中添加 PING/ACK 机制
2. **超时提示**：在客户端等待广播超时时显示错误提示
3. **悔棋功能**：设计 `UNDO_REQ` 消息和服务器处理逻辑
4. **观战模式**：允许第三方作为观众连接到服务器
5. **ACK 确认**：为每条消息添加确认机制以提高可靠性

---

## 类文件清单

**新增网络类** (`src/com/github/lemonadedream/boardgame/controller/network/`)

-   `GameClientImpl.java` - 客户端 socket 通信
-   `GameServerImpl.java` - 服务器监听和广播
-   `NetworkManager.java` - 生命周期管理
-   `NetControllerImpl.java` - 客户端适配器
-   `ConnectionDialogImpl.java` - 连接配置 UI
-   `NetworkMessage.java`, `MoveRequestMessage.java`, `MoveBroadcastMessage.java`, `StateMessage.java` - 消息类

**修改现有类**

-   `MainWindow.java` - 在 `initGame()` 中调用 `ConnectionDialog` 并启动网络
-   `GoPanel.java` - 新增 `setNetController()` 和 `applyRemoteMove()` 方法
-   `GoBoardMouseController.java` - 在第二次点击时调用 `NetController.requestMove()` 而非直接落子

---

完成！现在可以在两台电脑间运行联网围棋游戏。
