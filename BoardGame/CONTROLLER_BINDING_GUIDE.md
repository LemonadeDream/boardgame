# GoBoardMouseController 绑定指南

## 概述

`GoBoardMouseController` 已在构造函数中自动完成监听器绑定,无需手动调用 `addMouseListener()`。

## 使用方式

### 方式 1:在 GoPanel 构造函数中直接绑定(推荐)

修改 `GoPanel.java` 的构造函数:

```java
private GoPanel(MainWindow window) {
    super(window, "resources/images/board/goBackground.jpg", "resources/images/board/goBoard.png");
    setLayout(new BorderLayout());

    // 初始化棋盘模型
    this.boardModel = new GoBoard();

    // 将围棋相关组件添加到本面板
    new GoComponentsAdder().addTo(this);

    // 创建并绑定鼠标控制器(可选传入监听器)
    new GoBoardMouseController(this, null);
    // 或使用自定义监听器:
    // new GoBoardMouseController(this, (row, col, e) -> {
    //     System.out.println("外部监听: 点击了 (" + row + "," + col + ")");
    // });
}
```

### 方式 2:在 GoComponentsAdder 中统一管理(可选)

如果希望在 `GoComponentsAdder` 中统一管理所有控制器,可修改如下:

```java
public class GoComponentsAdder {
    public void addTo(Container container) {
        // ...原有代码...

        // 如果容器是 GoPanel,则绑定鼠标控制器
        if (container instanceof GoPanel) {
            GoPanel goPanel = (GoPanel) container;
            new GoBoardMouseController(goPanel, null);
        }
    }
}
```

### 方式 3:在外部显式创建控制器(灵活性最高)

```java
// 在主窗口或其他管理类中
GoPanel goPanel = GoPanel.getGoPanel(mainWindow);
GoBoardMouseController controller = new GoBoardMouseController(goPanel, (row, col, e) -> {
    // 自定义点击处理逻辑
    if (e.getButton() == MouseEvent.BUTTON3) { // 右键点击
        System.out.println("右键点击: (" + row + "," + col + ")");
    }
});

// 后续可调用控制器方法
controller.setCurrentColor(GoBoard.WHITE); // 切换为白棋先手
controller.reset(); // 重置控制器状态
```

## 控制器功能说明

### 自动功能(无需额外配置)

-   ? 监听鼠标点击事件
-   ? 像素坐标转换为棋盘逻辑坐标
-   ? 两次点击确认机制(首次预览+二次确认)
-   ? 落子合法性检查(调用 GoPlaceProcessor)
-   ? 自动触发棋盘重绘

### 可选配置

-   传入 `BoardClickListener` 实现自定义点击响应
-   调用 `setCurrentColor(int)` 切换落子颜色
-   调用 `reset()` 重置控制器状态

## 交互流程

```
用户点击棋盘
    ↓
pixelToBoard() 坐标转换
    ↓
首次点击? → GoPlaceProcessor.check(method=0) 检查合法性
    ↓
合法 → showAuxiliaryLine() 显示辅助线
    ↓
二次点击同位置? → GoPlaceProcessor.check(method=1) 确认落子
    ↓
更新棋盘状态 → refreshBoard() 触发重绘
```

## 注意事项

1. **控制器已自动注册监听器**:构造函数中执行了 `panel.addMouseListener(this)`,无需重复添加
2. **棋子图片需预先加载**:调用 `goPanel.setStoneImages(blackPath, whitePath)` 设置棋子图片
3. **辅助线功能预留**:`showAuxiliaryLine()` 方法当前为空实现,可后续扩展(如绘制半透明预览棋子)
4. **错误处理**:非法落子会在控制台输出错误信息,建议后续通过 UI 提示用户

## 示例:完整初始化代码

```java
// 在 GoPanel 构造函数中
private GoPanel(MainWindow window) {
    super(window, "resources/images/board/goBackground.jpg", "resources/images/board/goBoard.png");
    setLayout(new BorderLayout());

    // 1. 初始化棋盘模型
    this.boardModel = new GoBoard();

    // 2. 设置棋子图片(用户选择后调用)
    // TODO: 让用户选择图片,然后调用:
    // setStoneImages("选中的黑棋路径", "选中的白棋路径");

    // 3. 添加UI组件
    new GoComponentsAdder().addTo(this);

    // 4. 绑定鼠标控制器(自动完成交互逻辑)
    new GoBoardMouseController(this, null);
}
```

## 后续扩展建议

-   [ ] 实现辅助线绘制(十字线、半透明预览棋子)
-   [ ] 添加右键菜单(取消落子、棋盘分析等)
-   [ ] 实现悔棋功能(调用 `boardModel.boardStatusPop()`)
-   [ ] 集成计时器更新左侧信息面板
-   [ ] 添加落子音效
