# 时间显示和回合计数修复说明

## 问题原因

之前的代码中，`GoPanel` 创建了 `GoComponentsAdder` 和 `GoBoardMouseController`，但：

1. ? `GoComponentsAdder` 创建后没有保存引用
2. ? `GoBoardMouseController` 创建时没有传入计时器（传的是 null）
3. ? 没有实现棋盘点击的回调逻辑来更新回合数和棋子数量

## 修复内容

### 1. 保存组件引用

```java
// 添加成员变量
private GoComponentsAdder componentsAdder;
private GoBoardMouseController mouseController;
```

### 2. 正确初始化控制器

```java
// 保存 GoComponentsAdder 引用
this.componentsAdder = new GoComponentsAdder();
this.componentsAdder.addTo(this);

// 创建控制器时传入计时器
this.mouseController = new GoBoardMouseController(
    this,
    this::onBoardClicked,                    // 点击回调
    componentsAdder.getMatchTimePiece(),     // 对局计时器
    componentsAdder.getMoveTimePiece()       // 步时计时器
);
```

### 3. 实现信息更新逻辑

```java
private void onBoardClicked(int row, int col, MouseEvent e) {
    // 只在确认落子后更新（位置有棋子时）
    if (boardModel.getCurStatus()[row][col] != GoBoard.EMPTY) {
        updateGameInfo();
    }
}

private void updateGameInfo() {
    // 更新回合数
    JLabel roundLabel = componentsAdder.getRoundLabel();
    roundLabel.setText(String.valueOf(currentRound + 1));

    // 统计并更新棋子数量
    int blackCount = 0, whiteCount = 0;
    // ... 遍历棋盘统计 ...
    pieceLabel.setText(String.format("黑:%d 白:%d", blackCount, whiteCount));
}
```

## 现在的工作流程

### ? 游戏启动

-   UI 组件正确初始化
-   计时器显示 `00:00`
-   回合数显示 `1`
-   棋子数量显示 `黑:0 白:0`

### ? 第一次落子

1. **首次点击**：显示辅助线（预览）
2. **二次点击确认**：
    - ? 对局计时器启动
    - ? 步时计时器启动
    - ? 回合数更新为 `2`
    - ? 棋子数量更新（如 `黑:1 白:0`）

### ? 后续落子

1. **首次点击**：显示辅助线
2. **二次点击确认**：
    - ? 步时计时器重置并重启
    - ? 对局计时器继续运行
    - ? 回合数递增
    - ? 棋子数量实时更新

## 测试方法

1. 运行游戏
2. 在棋盘上任意位置点击两次落子
3. 观察：
    - 左侧 "对局时间" 开始计时
    - 左侧 "步时" 开始计时并在每次落子后重置
    - "回合" 数字递增
    - "棋子数量" 实时更新

## 相关文件

-   `GoPanel.java` - 主要修改
-   `GoBoardMouseController.java` - 时间控制逻辑
-   `GoComponentsAdder.java` - UI 组件管理
-   `TimePiece.java` - 计时器实现

---

修复日期：2025-10-27
