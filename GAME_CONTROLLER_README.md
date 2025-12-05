# 围棋游戏控制器使用说明

## 概述

`GoGameController` 是围棋游戏的按钮控制器，负责处理游戏中的各种按钮事件。

## 已实现的功能

### 1. 暂停/继续功能

-   **按钮**: "暂停" / "继续"
-   **功能**:
    -   点击"暂停"会暂停对局计时器和步时计时器
    -   按钮文本会自动切换为"继续"
    -   点击"继续"恢复计时器，按钮文本切换回"暂停"
    -   显示暂停提示对话框

### 2. 悔棋功能

-   **按钮**: "悔棋"
-   **功能**:
    -   弹出确认对话框
    -   确认后更新回合数（减 1）
    -   刷新棋盘显示
    -   暂停状态下无法悔棋
    -   **注意**: 棋盘模型的悔棋逻辑需要在 `GoBoard` 中实现 `undo()` 方法

### 3. 认输功能

-   **按钮**: "认输"
-   **功能**:
    -   弹出确认对话框
    -   确认后停止所有计时器
    -   显示游戏结束提示
    -   **待完善**: 记录对局结果、显示详细信息

### 4. 界面跳转功能

-   **充值按钮**: 显示"功能暂未开放"提示
-   **设置按钮**: 跳转到设置界面
-   **成就按钮**: 跳转到成就界面

## 代码结构

### GoGameController.java

控制器类，实现 `ActionListener` 接口

-   构造函数接收 `MainWindow`、`GoPanel`、`GoComponentsAdder` 三个参数
-   `actionPerformed()` 方法根据按钮文本分发事件

### GoComponentsAdder.java

组件管理器，负责创建和管理游戏界面组件

-   `bindButtonListener()` 方法用于绑定事件监听器到所有按钮
-   提供访问计时器和信息标签的 getter 方法

### GoPanel.java

围棋游戏面板

-   在构造函数中创建 `GoGameController` 实例
-   调用 `componentsAdder.bindButtonListener()` 绑定控制器

## 使用方法

控制器已自动在 `GoPanel` 中创建和绑定，无需手动操作：

```java
// 在 GoPanel 构造函数中
GoGameController gameController = new GoGameController(window, this, componentsAdder);
componentsAdder.bindButtonListener(gameController);
```

## 待完善功能

1. **悔棋**: 需要在 `GoBoard` 类中实现 `undo()` 方法
2. **认输**: 可以添加对局结果记录、统计信息等
3. **充值**: 根据需求实现充值功能

## 注意事项

-   所有按钮事件都通过 `ActionCommand` 识别，确保按钮文本与 switch-case 中的字符串一致
-   暂停状态通过 `isPaused` 标志位管理
-   计时器的暂停/恢复使用 `TimePiece` 的 `pause()` 和 `start()` 方法
