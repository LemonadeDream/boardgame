# 皮肤选择功能使用指南

## 功能概述

本项目为围棋游戏添加了完整的皮肤选择功能，允许用户自定义黑白棋子、背景和棋盘的外观。

## 主要特性

### 1. 资源路径配置

-   资源文件基础路径：`src/resources/images/`
-   文件选择器会自动定位到该路径，方便用户选择图片

### 2. 棋子皮肤选择方式

#### 方式一：使用预设皮肤（推荐）

在围棋选择界面提供了 4 种预设皮肤：

-   **预设 1: 默认** - `pic1.png` 和 `pic2.png`
-   **预设 2: 绿蓝** - `pic_green.png` 和 `pic_lightblue.png`
-   **预设 3: 红蓝** - `pic_red.png` 和 `pic_pureblue.png`
-   **预设 4: 紫白** - `pic_pr.png` 和 `white.png`

点击对应按钮即可快速切换皮肤。

#### 方式二：自定义选择

-   **选择黑方棋子**：点击"选择黑方棋子"按钮，弹出文件选择器
-   **选择白方棋子**：点击"选择白方棋子"按钮，弹出文件选择器
-   **选择背景**：点击"选择背景"按钮（功能待实现）
-   **选择棋盘**：点击"选择棋盘"按钮（功能待实现）

### 3. 使用流程

1. 启动游戏，进入主界面
2. 点击"开始"按钮，进入游戏选择界面
3. 点击"围棋游戏"，进入围棋选择界面
4. 在此界面可以：
    - 点击预设按钮快速切换皮肤
    - 或点击自定义按钮选择本地图片文件
5. 点击"进入围棋游戏"，开始游戏
6. 游戏中会使用你选择的皮肤

### 4. 代码实现示例

#### 通过 lambda 表达式传递多个参数

```java
// 方式1：直接调用预设
button.addActionListener(e -> skinChooser.setStoneSkin("default"));

// 方式2：传入两个路径参数
button.addActionListener(e -> skinChooser.setStoneSkin(
    "resources/images/stones/pic1.png",
    "resources/images/stones/pic2.png"
));

// 方式3：调用文件选择器
button.addActionListener(e -> skinChooser.chooseBlackStone(this));
```

#### 使用 putClientProperty 存储参数

```java
JButton button = new JButton("自定义皮肤");
button.putClientProperty("blackPath", "path/to/black.png");
button.putClientProperty("whitePath", "path/to/white.png");
button.addActionListener(e -> {
    JComponent src = (JComponent) e.getSource();
    String bp = (String) src.getClientProperty("blackPath");
    String wp = (String) src.getClientProperty("whitePath");
    skinChooser.setStoneSkin(bp, wp);
});
```

### 5. 扩展预设皮肤

在 `SkinChooser.java` 的 `registerDefaultPresets()` 方法中添加新预设：

```java
stonePresets.put("custom_key",
    new SkinPreset("path/to/black.png", "path/to/white.png"));
```

## 技术要点

### 文件结构

```
BoardGame/
├── src/
│   ├── com/github/lemonadedream/boardgame/
│   │   ├── view/
│   │   │   ├── skins/
│   │   │   │   └── SkinChooser.java        // 皮肤选择器
│   │   │   └── panel/
│   │   │       └── mainGamePanel/
│   │   │           ├── GoChoosePanel.java  // 围棋选择界面
│   │   │           └── GoPanel.java        // 围棋游戏面板
│   └── resources/
│       └── images/
│           ├── stones/                      // 棋子图片
│           └── board/                       // 背景和棋盘图片
```

### 关键类说明

#### SkinChooser

-   管理皮肤预设和路径
-   提供文件选择器功能
-   与 GoPanel 交互更新皮肤

#### GoChoosePanel

-   提供用户界面
-   绑定按钮事件
-   使用 lambda 表达式传递参数

#### GoPanel

-   接收皮肤路径
-   调用 ImageLoader 加载图片
-   重绘棋盘显示新皮肤

## 注意事项

1. 必须先点击"进入围棋游戏"初始化 GoPanel，皮肤选择器才能正常工作
2. 支持的图片格式：PNG、JPG、JPEG
3. 背景和棋盘切换功能需要在 GoPanel 中实现相关接口
4. 自定义图片路径使用绝对路径，预设皮肤使用相对路径

## 未来改进

-   [ ] 实现背景图片切换功能
-   [ ] 实现棋盘图片切换功能
-   [ ] 添加皮肤预览功能
-   [ ] 支持皮肤配置保存和加载
-   [ ] 添加更多预设皮肤选项
