# 围棋游戏胜负判断实现说明

## ? 概述

已成功将胜负判断逻辑集成到 `GoBoardMouseController` 中，实现了：

1. ? 每次落子后自动检查游戏是否结束
2. ? 支持多种结束条件（无法落子、时间耗尽、认输）
3. ? 游戏结束后弹窗询问是否进入复盘模式
4. ? 提供回调接口供面板处理跳转逻辑

---

## ? 实现位置

### 核心逻辑在 `GoBoardMouseController`

```
confirmMove()
    ↓
落子成功后调用 checkGameEnd()
    ↓
判断结束条件:
  - 时间耗尽 → handleGameEnd()
  - 无法落子 → handleGameEnd()
    ↓
弹窗询问 → 跳转复盘面板
```

---

## ? 修改的文件

### 1. `GoWinLose.java`

-   **修复**：`dfs` 方法缺少的花括号
-   **删除**：未使用的 `Stack` 导入
-   **保留**：`isGameOver()` 方法用于判断是否无合法落子

### 2. `GoBoardMouseController.java`

**新增内容：**

-   `GameEndListener` 接口：游戏结束回调
-   `GoPlaceProcessor processor` 字段：复用处理器实例
-   `GoWinLose winLoseChecker` 字段：胜负判断器
-   `checkGameEnd()` 方法：检查游戏是否结束
-   `checkNoLegalMoves()` 方法：检查是否无合法落子
-   `handleGameEnd()` 方法：处理游戏结束逻辑
-   `triggerGameEnd()` 方法：供认输功能调用

**修改内容：**

-   `confirmMove()` 方法：添加胜负判断调用

### 3. `GoGameController.java`

**修改内容：**

-   `handleSurrender()` 方法：调用 `mouseController.triggerGameEnd("认输")`

### 4. `GoPanel.java`

**新增内容：**

-   `onGameEnd()` 方法：游戏结束回调（预留跳转逻辑接口）

---

## ? 工作流程

### 场景 1：正常落子导致对方无法继续

```
玩家A落子 → confirmMove()成功
           ↓
         切换到玩家B
           ↓
         checkGameEnd()
           ↓
         遍历所有点检查玩家B是否有合法落子
           ↓
         无合法落子 → handleGameEnd(玩家A, "对方无法落子")
           ↓
         弹窗："玩家A获胜！是否进入复盘？"
```

### 场景 2：时间耗尽

```
玩家A落子成功 → checkGameEnd()
              ↓
            检查计时器
              ↓
            moveTimer显示 "00:00"
              ↓
            handleGameEnd(玩家B, "步时耗尽")
              ↓
            弹窗："玩家B获胜！是否进入复盘？"
```

### 场景 3：认输

```
玩家A点击"认输"按钮 → GoGameController.handleSurrender()
                     ↓
                   确认对话框
                     ↓
                   mouseController.triggerGameEnd("认输")
                     ↓
                   handleGameEnd(玩家B, "认输")
                     ↓
                   弹窗："玩家B获胜！是否进入复盘？"
```

---

## ?? 重要注意事项

### 1. 时间判断条件需要调整

**当前代码：**

```java
if (matchTimeText.equals("00:00") || matchTimeText.equals("0:00:00")) {
    handleGameEnd(3 - currentColor, "对局时间耗尽");
}
```

**问题：** 你原来的 `GoWinLose.isGameOver()` 中是这样判断的：

```java
if (matchTimer.getLabel().getText().equals("1:00:00")) {
    return true;
}
```

这看起来是在判断"等于初始值"而不是"归零"。

**建议：** 根据你的实际需求选择：

-   如果是倒计时归零，使用 `"00:00"`
-   如果是判断达到某个时间阈值，改为你需要的时间字符串

### 2. 复盘面板跳转逻辑待实现

在 `GoPanel.onGameEnd()` 方法中预留了跳转逻辑：

```java
private void onGameEnd(int winnerColor, String reason) {
    // TODO: 实现跳转到复盘面板
    // 方案1: 直接调用 MainWindow 的方法（需要保存 window 引用）
    // window.switchToReplayPanel();

    // 方案2: 使用 ActionEvent 触发面板切换
    // ActionEvent replayEvent = new ActionEvent(this,
    //     ActionEvent.ACTION_PERFORMED, "复盘");
    // window.switchPanel(replayEvent);
}
```

**实现步骤：**

1. 在 `MainWindow` 中添加复盘面板（ReplayPanel）
2. 在 `switchPanel()` 方法中添加 `case "复盘"` 分支
3. 在 `GoPanel.onGameEnd()` 中调用跳转逻辑

### 3. 性能优化建议

当前每次落子后都会遍历所有 19×19=361 个点来检查是否有合法落子。

**优化方案：**

-   只在"双方连续 pass"或"棋盘快满"时才检查
-   添加一个 pass 按钮，连续两次 pass 才判定游戏结束
-   使用启发式方法减少遍历范围

---

## ? 下一步建议

### 必须完成：

1. **调整时间判断条件**（见上方"重要注意事项"第 1 点）
2. **实现复盘面板跳转**（见上方"重要注意事项"第 2 点）

### 可选优化：

3. 添加"双方 Pass"机制（更符合围棋规则）
4. 在游戏结束时保存棋谱（用于复盘）
5. 显示详细的胜负信息（黑方/白方棋子数、目数等）
6. 添加音效提示（游戏结束音效）

---

## ? 示例代码：实现复盘面板跳转

### 步骤 1：在 `GoPanel` 中保存 MainWindow 引用

```java
private MainWindow window;

public GoPanel(MainWindow window) {
    super(window, "resources/images/board/goBackground.jpg",
          "resources/images/board/goBoard.png");
    this.window = window; // 保存引用
    // ... 其他初始化代码
}
```

### 步骤 2：在 `onGameEnd` 中调用跳转

```java
private void onGameEnd(int winnerColor, String reason) {
    // 创建 ActionEvent 并触发面板切换
    java.awt.event.ActionEvent replayEvent =
        new java.awt.event.ActionEvent(
            this,
            java.awt.event.ActionEvent.ACTION_PERFORMED,
            "复盘"
        );
    window.switchPanel(replayEvent);
}
```

### 步骤 3：在 `MainWindow` 中添加复盘面板

```java
// 在 init() 方法中添加
ReplayPanel replayPanel = new ReplayPanel(this);
contentPanel.add(replayPanel, "复盘界面");

// 在 switchPanel() 中添加
case "复盘":
    cardLayout.show(contentPanel, "复盘界面");
    break;
```

---

## ? 测试建议

1. **测试时间耗尽**：将初始时间设置为很短（如 5 秒），观察是否正确判定
2. **测试无法落子**：在小棋盘上快速下满，观察是否正确判定
3. **测试认输功能**：点击认输按钮，确认弹窗和跳转正常
4. **测试复盘跳转**：选择"是"进入复盘，确认面板切换成功

---

## ? 常见问题

**Q: 为什么不在 `TimePiece` 中监听时间归零？**  
A: 计时器是通用组件，不应该耦合游戏逻辑。在 `GoBoardMouseController` 中检查更合理。

**Q: 能否在每次时间变化时检查，而不是每次落子后？**  
A: 可以，但会增加复杂度。当前方案简单有效，性能开销可忽略。

**Q: `GoWinLose.isGameOver()` 中还保留了时间判断，会冲突吗？**  
A: 不会。现在主要使用 `GoBoardMouseController.checkGameEnd()`，`GoWinLose.isGameOver()` 仅用于判断无法落子。可以移除其中的时间判断代码以避免混淆。

---

## ? 相关文件

-   `src/com/github/lemonadedream/boardgame/controller/GoBoardMouseController.java`
-   `src/com/github/lemonadedream/boardgame/controller/GoGameController.java`
-   `src/com/github/lemonadedream/boardgame/module/GoGameModel/GoLogic/GoWinLose.java`
-   `src/com/github/lemonadedream/boardgame/view/panel/mainGamePanel/GoPanel.java`

---

**文档更新时间：** 2025 年 10 月 30 日
