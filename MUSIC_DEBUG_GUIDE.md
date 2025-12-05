# 音乐播放问题诊断和解决方案

## 问题分析

### 根本原因

**Java 原生不支持 MP3 格式！**

Java 的 `javax.sound.sampled` 包原生只支持以下格式：

-   WAV (PCM)
-   AIFF
-   AU

要播放 MP3 文件，必须添加外部库。

## 可能的错误信息

运行程序后，控制台可能显示以下错误之一：

1. **UnsupportedAudioFileException**

    ```
    javax.sound.sampled.UnsupportedAudioFileException: could not get audio input stream from input stream
    ```

2. **文件找不到**

    ```
    ? 错误: 无法找到音乐文件: /resources/music/...
    ```

3. **格式转换失败**
    ```
    AudioSystem.getAudioInputStream() 抛出异常
    ```

## 解决方案

### 方案 1: 使用 MP3SPI 库（推荐）

#### 步骤 1: 下载所需的 JAR 文件

需要 3 个 JAR 文件：

1. `mp3spi-1.9.5.jar` - MP3 Service Provider Interface
2. `jlayer-1.0.1.jar` - MP3 解码器
3. `tritonus_share-0.3.6.jar` - 共享库

下载地址：

-   Maven Central: https://mvnrepository.com/artifact/com.googlecode.soundlibs/mp3spi
-   或使用项目中的 `lib/` 目录

#### 步骤 2: 添加到项目

1. 将 JAR 文件复制到项目的 `lib/` 目录
2. 在 Eclipse/IntelliJ 中添加到构建路径：
    - Eclipse: 右键项目 -> Build Path -> Configure Build Path -> Add External JARs
    - IntelliJ: File -> Project Structure -> Libraries -> + -> Java

#### 步骤 3: 验证

添加库后，重新运行程序，MP3 应该能正常播放。

### 方案 2: 转换为 WAV 格式

如果不想添加外部库，可以将 MP3 转换为 WAV：

1. 使用转换工具（如 Audacity, FFmpeg）
2. 转换命令（FFmpeg）：

    ```bash
    ffmpeg -i "09 - City of Tears.mp3" "09 - City of Tears.wav"
    ffmpeg -i "15 - Resting Grounds.mp3" "15 - Resting Grounds.wav"
    ffmpeg -i "23 - White Palace.mp3" "23 - White Palace.wav"
    ```

3. 修改 `MusicPlayer.java` 中的文件扩展名：
    ```java
    musicFiles.add("/resources/music/09 - City of Tears.wav");
    musicFiles.add("/resources/music/15 - Resting Grounds.wav");
    musicFiles.add("/resources/music/23 - White Palace.wav");
    ```

### 方案 3: 使用 JavaFX MediaPlayer（需要 JavaFX）

如果项目已包含 JavaFX，可以使用 `javafx.scene.media.MediaPlayer`，它原生支持 MP3。

## 调试步骤

### 1. 运行程序查看控制台输出

添加的调试信息会显示：

```
=== 程序启动 ===
? MainWindow 实例创建完成
=== 初始化音乐列表 ===
音乐列表已添加 3 首歌曲:
  [0] /resources/music/09 - City of Tears.mp3
  [1] /resources/music/15 - Resting Grounds.mp3
  [2] /resources/music/23 - White Palace.mp3
? MainWindow 初始化完成
? MainWindow 窗口已显示
=== 准备启动音乐播放器 ===
? MusicPlayer 实例获取成功
=== MusicPlayer.play() 被调用 ===
音乐文件总数: 3
尝试加载音乐文件 [0]: /resources/music/09 - City of Tears.mp3
? 音乐文件加载成功
? 播放音乐时出错: javax.sound.sampled.UnsupportedAudioFileException
```

### 2. 根据错误信息判断

-   **如果看到 "UnsupportedAudioFileException"** → 需要添加 MP3 支持库
-   **如果看到 "无法找到音乐文件"** → 检查文件路径和编译输出
-   **如果没有任何输出** → 检查 `main()` 方法是否被调用

## 推荐配置（使用 MP3SPI）

### 下载链接

```
https://mvnrepository.com/artifact/com.googlecode.soundlibs/mp3spi/1.9.5
https://mvnrepository.com/artifact/javazoom/jlayer/1.0.1
https://mvnrepository.com/artifact/com.googlecode.soundlibs/tritonus-share/0.3.7.4
```

### Maven 依赖（如果使用 Maven）

```xml
<dependency>
    <groupId>com.googlecode.soundlibs</groupId>
    <artifactId>mp3spi</artifactId>
    <version>1.9.5</version>
</dependency>
<dependency>
    <groupId>javazoom</groupId>
    <artifactId>jlayer</artifactId>
    <version>1.0.1</version>
</dependency>
<dependency>
    <groupId>com.googlecode.soundlibs</groupId>
    <artifactId>tritonus-share</artifactId>
    <version>0.3.7.4</version>
</dependency>
```

## 验证清单

运行程序后，检查：

-   [ ] 控制台显示 "=== 程序启动 ==="
-   [ ] 音乐列表显示 3 首歌曲
-   [ ] 显示 "? 音乐文件加载成功"
-   [ ] 显示 "? 创建 AudioInputStream 成功"
-   [ ] 显示 "? 音频格式转换成功"
-   [ ] 显示 "? Clip 打开成功"
-   [ ] 显示 "? 成功: 正在播放 - ..."
-   [ ] Clip 状态显示 "isRunning: true"
-   [ ] **听到音乐声音**

## 快速测试

创建简单测试代码验证 MP3 支持：

```java
import javax.sound.sampled.*;
import java.io.File;

public class AudioTest {
    public static void main(String[] args) {
        try {
            File mp3File = new File("src/resources/music/09 - City of Tears.mp3");
            AudioInputStream stream = AudioSystem.getAudioInputStream(mp3File);
            System.out.println("? MP3 支持正常！");
            System.out.println("格式: " + stream.getFormat());
        } catch (Exception e) {
            System.err.println("? MP3 不支持: " + e.getClass().getName());
            System.err.println("需要添加 MP3SPI 库！");
        }
    }
}
```

## 下一步

1. **立即**: 运行程序查看控制台输出
2. **如果出现 UnsupportedAudioFileException**: 下载并添加 MP3SPI 库
3. **如果文件找不到**: 检查编译输出目录（bin/resources/music/）
4. **如果仍无声音**: 检查系统音量、音频设备
