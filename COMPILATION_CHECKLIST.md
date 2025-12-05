# 编译检查清单

## 已修复的问题

### 1. RemoteMoveHandler 构造函数修复 ?

-   将 `RemoteMoveHandler(NetController)` 改为 `RemoteMoveHandler(NetControllerImpl)`
-   保持 `NetControllerImpl` 作为唯一网络适配器实现

### 2. NetworkManager 单例问题修复 ?

-   将 `NetworkManager.java` 从骨架版本升级为完整实现
-   使用 `GameClientImpl` 和 `GameServerImpl` 替代骨架类
-   添加 `isServerMode()` 和 `getClient()` 方法
-   `NetworkManagerImpl.java` 标记为已弃用（保留兼容性）

## 核心类对应关系

| 功能               | 类名                   | 文件                        |
| ------------------ | ---------------------- | --------------------------- |
| 网络管理器（单例） | `NetworkManager`       | `NetworkManager.java`       |
| 客户端实现         | `GameClientImpl`       | `GameClientImpl.java`       |
| 服务器实现         | `GameServerImpl`       | `GameServerImpl.java`       |
| 客户端适配器       | `NetControllerImpl`    | `NetControllerImpl.java`    |
| 消息分发器         | `RemoteMoveHandler`    | `RemoteMoveHandler.java`    |
| 连接对话框         | `ConnectionDialogImpl` | `ConnectionDialogImpl.java` |

## 导入关系检查

```
MainWindow.java
  ├─ imports NetworkManager (正确)
  ├─ imports ConnectionDialogImpl (正确)
  └─ imports NetControllerImpl (正确)

GoPanel.java
  ├─ imports NetControllerImpl (正确)
  └─ uses applyRemoteMove()

GoBoardMouseController.java
  ├─ imports NetControllerImpl (正确)
  ├─ imports NetworkManager (正确)
  └─ uses requestMove()

NetControllerImpl.java
  ├─ implements GameClientImpl.ClientListener (正确)
  ├─ uses RemoteMoveHandler (正确)
  └─ calls NetworkManager.getInstance() (正确)

RemoteMoveHandler.java
  ├─ references NetControllerImpl (已修复)
  └─ calls onMoveBroadcast()
```

## 编译命令

```bash
# 清理旧编译
rm -rf bin/*

# 编译 network 包（需要 Gson 在 classpath）
javac -cp lib/gson-2.10.1.jar -d bin \
    src/com/github/lemonadedream/boardgame/controller/network/*.java

# 编译整个项目
javac -cp lib/gson-2.10.1.jar -d bin \
    src/com/github/lemonadedream/boardgame/**/*.java
```

## 运行检查

编译成功后可进行以下测试：

1. **本地单 Server 测试**

    ```bash
    java -cp bin:lib/gson-2.10.1.jar \
        com.github.lemonadedream.boardgame.view.MainWindow &
    # 在 ConnectionDialog 选择"启动服务器"
    ```

2. **本地多 Client 测试**

    ```bash
    java -cp bin:lib/gson-2.10.1.jar \
        com.github.lemonadedream.boardgame.view.MainWindow &
    # 在 ConnectionDialog 选择"连接到服务器"，localhost:9000
    ```

3. **网络测试**（两台电脑）
    - PC-A: 启动服务器，IP 192.168.x.x
    - PC-B: 连接到 192.168.x.x:9000

## 可能的其他编译警告

-   未使用的导入：正常
-   原始类型：正常（如 `List` 而非 `List<T>`）
-   潜在的空指针：在运行时测试阶段会发现

都应该正常编译无误。
