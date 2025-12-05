package com.github.lemonadedream.boardgame.controller;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MusicPlayer: 音乐播放控制器
 * 支持循环播放、切换曲目、静音等功能
 * 使用单例模式确保全局只有一个播放器实例
 */
public class MusicPlayer {
    private static MusicPlayer instance;

    // 音乐文件列表（相对路径）
    private List<String> musicFiles;
    private int currentIndex = 0;

    // 播放控制
    private Clip clip;
    private boolean isMuted = false;
    private float volume = 0.8f; // 默认音量80%

    // 私有构造函数
    private MusicPlayer() {
        initMusicList();
    }

    /**
     * 获取单例实例
     */
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    /**
     * 初始化音乐列表
     * 可以轻松添加新的音乐文件
     */
    private void initMusicList() {
        musicFiles = new ArrayList<>();
        musicFiles.add("/resources/music/07 - Reflection.mp3");
        musicFiles.add("/resources/music/09 - City of Tears.mp3");
        musicFiles.add("/resources/music/15 - Resting Grounds.mp3");
        musicFiles.add("/resources/music/23 - White Palace.mp3");
        musicFiles.add("/resources/music/25 - Radiance.mp3");

        // 添加新音乐只需在这里添加路径即可
        // musicFiles.add("/resources/music/new_song.mp3");
    }

    /**
     * 开始播放当前曲目
     */
    public void play() {
        if (musicFiles.isEmpty()) {
            return;
        }

        stop(); // 停止当前播放

        try {
            String musicPath = musicFiles.get(currentIndex);
            InputStream audioSrc = getClass().getResourceAsStream(musicPath);

            if (audioSrc == null) {
                System.err.println("无法找到音乐文件: " + musicPath);
                return;
            }

            // 使用BufferedInputStream提高读取效率
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            // 转换音频格式以支持MP3
            AudioFormat baseFormat = audioStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);

            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, audioStream);

            clip = AudioSystem.getClip();
            clip.open(decodedStream);

            // 设置音量
            setVolume(volume);

            // 循环播放当前曲目
            clip.loop(Clip.LOOP_CONTINUOUSLY);

            // 应用静音状态
            if (isMuted) {
                mute();
            } else {
                clip.start();
            }

        } catch (Exception e) {
            System.err.println("播放音乐时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    /**
     * 播放下一首
     */
    public void next() {
        if (musicFiles.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex + 1) % musicFiles.size();
        play();
    }

    /**
     * 播放上一首
     */
    public void previous() {
        if (musicFiles.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex - 1 + musicFiles.size()) % musicFiles.size();
        play();
    }

    /**
     * 切换静音状态
     */
    public void toggleMute() {
        if (isMuted) {
            unmute();
        } else {
            mute();
        }
    }

    /**
     * 静音
     */
    public void mute() {
        isMuted = true;
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * 取消静音
     */
    public void unmute() {
        isMuted = false;
        if (clip != null && !clip.isRunning()) {
            clip.start();
        }
    }

    /**
     * 设置音量
     * 
     * @param volume 音量值 0.0f ~ 1.0f
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));

        if (clip != null && clip.isOpen()) {
            try {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(this.volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            } catch (Exception e) {
                System.err.println("设置音量失败: " + e.getMessage());
            }
        }
    }

    /**
     * 获取当前音乐名称
     */
    public String getCurrentMusicName() {
        if (musicFiles.isEmpty()) {
            return "无音乐";
        }
        String path = musicFiles.get(currentIndex);
        return path.substring(path.lastIndexOf("/") + 1);
    }

    /**
     * 获取静音状态
     */
    public boolean isMuted() {
        return isMuted;
    }

    /**
     * 获取当前音乐索引
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 获取音乐总数
     */
    public int getMusicCount() {
        return musicFiles.size();
    }

    /**
     * 释放资源
     */
    public void dispose() {
        stop();
        clip = null;
    }
}
