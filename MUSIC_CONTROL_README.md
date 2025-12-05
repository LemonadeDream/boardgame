# 音乐控制功能说明

## 概述

本系统已集成背景音乐播放功能，支持循环播放、切换曲目和静音控制。

## 功能特性

### 1. MusicPlayer 音乐控制器

-   **单例模式**: 全局唯一的音乐播放器实例
-   **循环播放**: 每首音乐单曲循环播放
-   **自动切换**: 支持上一首/下一首，首尾循环
-   **静音控制**: 可切换静音/取消静音状态
-   **音量控制**: 默认音量 80%，可通过代码调整

### 2. 控制界面

#### 设置界面（SettingPanel）

布局：2×2 网格

```
[返回]     [静音]
[上一首]   [下一首]
```

#### 游戏界面（GoPanel 底部）

布局：1×3 横向排列

```
[上一首]   [静音]   [下一首]
```

### 3. 音乐文件管理

#### 当前音乐列表

位于 `src/resources/music/` 目录：

1. `09 - City of Tears.mp3`
2. `15 - Resting Grounds.mp3`
3. `23 - White Palace.mp3`

#### 添加新音乐

在 `MusicPlayer.java` 的 `initMusicList()` 方法中添加：

```java
private void initMusicList() {
    musicFiles = new ArrayList<>();
    musicFiles.add("/resources/music/09 - City of Tears.mp3");
    musicFiles.add("/resources/music/15 - Resting Grounds.mp3");
    musicFiles.add("/resources/music/23 - White Palace.mp3");

    // 添加新音乐（只需添加这一行）
    musicFiles.add("/resources/music/your_new_song.mp3");
}
```

**步骤**：

1. 将音乐文件（支持 MP3 格式）放入 `src/resources/music/` 目录
2. 在 `initMusicList()` 方法中添加路径
3. 重新编译运行即可

## API 使用说明

### 获取播放器实例

```java
MusicPlayer player = MusicPlayer.getInstance();
```

### 主要方法

| 方法                    | 说明                     |
| ----------------------- | ------------------------ |
| `play()`                | 播放当前曲目（循环播放） |
| `stop()`                | 停止播放                 |
| `next()`                | 播放下一首（循环）       |
| `previous()`            | 播放上一首（循环）       |
| `toggleMute()`          | 切换静音状态             |
| `mute()`                | 静音                     |
| `unmute()`              | 取消静音                 |
| `setVolume(float)`      | 设置音量（0.0 ~ 1.0）    |
| `getCurrentMusicName()` | 获取当前音乐文件名       |
| `isMuted()`             | 获取静音状态             |
| `getMusicCount()`       | 获取音乐总数             |

### 使用示例

```java
// 启动音乐播放（已在MainWindow.main()中自动调用）
MusicPlayer.getInstance().play();

// 切换到下一首
MusicPlayer.getInstance().next();

// 静音/取消静音
MusicPlayer.getInstance().toggleMute();

// 设置音量为50%
MusicPlayer.getInstance().setVolume(0.5f);

// 获取当前播放的音乐名称
String currentMusic = MusicPlayer.getInstance().getCurrentMusicName();
System.out.println("正在播放: " + currentMusic);
```

## 实现细节

### 音频格式支持

-   支持 MP3 格式（通过 AudioSystem 转换为 PCM）
-   自动处理音频格式转换
-   使用 Clip 进行播放控制

### 循环机制

-   单曲循环：使用 `Clip.LOOP_CONTINUOUSLY`
-   列表循环：使用模运算实现首尾循环

### 线程安全

-   使用单例模式确保全局唯一实例
-   播放控制方法线程安全

## 注意事项

1. **音乐文件路径**: 必须以 `/resources/music/` 开头
2. **文件格式**: 推荐使用 MP3 格式
3. **文件大小**: 建议单个文件不超过 10MB
4. **启动时机**: 音乐在 MainWindow 启动时自动播放
5. **资源释放**: 程序退出时自动释放音频资源

## 扩展建议

### 未来可扩展功能

-   [ ] 音量调节滑块
-   [ ] 播放进度条
-   [ ] 播放列表显示
-   [ ] 随机播放模式
-   [ ] 播放历史记录
-   [ ] 音乐文件动态加载（从外部文件夹）
-   [ ] 支持更多音频格式（WAV、OGG 等）

### 自定义播放列表

可以创建多个播放列表，在不同场景使用不同的音乐：

```java
// 在 MusicPlayer 中添加方法
public void loadPlaylist(String playlistName) {
    musicFiles.clear();
    switch (playlistName) {
        case "relaxing":
            musicFiles.add("/resources/music/calm1.mp3");
            musicFiles.add("/resources/music/calm2.mp3");
            break;
        case "intense":
            musicFiles.add("/resources/music/battle1.mp3");
            musicFiles.add("/resources/music/battle2.mp3");
            break;
    }
    currentIndex = 0;
    play();
}
```

## 文件清单

### 新增文件

-   `src/com/github/lemonadedream/boardgame/controller/MusicPlayer.java` - 音乐控制器

### 修改文件

-   `src/com/github/lemonadedream/boardgame/view/MainWindow.java` - 添加音乐启动
-   `src/com/github/lemonadedream/boardgame/view/panel/SettingPanel.java` - 添加音乐控制按钮
-   `src/com/github/lemonadedream/boardgame/controller/GoComponentsAdder.java` - 替换底部按钮
-   `src/com/github/lemonadedream/boardgame/controller/GoGameButtonController.java` - 添加音乐控制事件处理

## 技术栈

-   Java Sound API
-   javax.sound.sampled.\*
-   Swing UI Components
