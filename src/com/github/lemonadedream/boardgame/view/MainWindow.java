package com.github.lemonadedream.boardgame.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.github.lemonadedream.boardgame.controller.MusicPlayer;
import com.github.lemonadedream.boardgame.view.panel.AchievementPanel;
import com.github.lemonadedream.boardgame.view.panel.GameChoosePanel;
import com.github.lemonadedream.boardgame.view.panel.MainPanel;
import com.github.lemonadedream.boardgame.view.panel.SettingPanel;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoChoosePanel;
import com.github.lemonadedream.boardgame.view.skins.SkinChooser;

public class MainWindow extends JFrame {
    // 创建CardLayout和一个内容JPanel来切换窗口
    CardLayout cardLayout = new CardLayout();
    JPanel contentPanel = new JPanel(cardLayout);
    // 创建各个窗口的Panel实例
    MainPanel mainPanel;
    GameChoosePanel gameChoosePanel;
    AchievementPanel achievementPanel;
    SettingPanel settingPanel;
    GoChoosePanel goChoosePanel;

    // 围棋游戏面板和皮肤选择器（延迟初始化）
    private GoPanel goPanel;
    private SkinChooser skinChooser;

    // 单例设计模式,采用线程安全的写法
    private static class MainWindowHolder {
        private static final MainWindow instance = new MainWindow();
    }

    private MainWindow() {
    }

    public static final MainWindow getInstance() {
        return MainWindowHolder.instance;
    }

    // 按钮的切换功能总控制函数
    public void switchPanel(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "开始":
                cardLayout.show(contentPanel, "游戏选择界面");
                break;
            case "设置":
                cardLayout.show(contentPanel, "设置界面");
                break;
            case "成就":
                cardLayout.show(contentPanel, "成就界面");
                break;
            case "返回":
                cardLayout.show(contentPanel, "主界面");
                break;
            case "围棋游戏":
                cardLayout.show(contentPanel, "围棋选择界面");
                break;
            case "进入围棋游戏":
                this.initGame();
                cardLayout.show(contentPanel, "围棋游戏界面");
                break;
            case "退出":
                System.exit(0);
        }
    }

    // 初始化类
    public void init() {
        // 初始化各个Panel实例
        this.mainPanel = new MainPanel();
        this.gameChoosePanel = new GameChoosePanel();
        this.achievementPanel = new AchievementPanel();
        this.settingPanel = new SettingPanel();
        this.goChoosePanel = new GoChoosePanel();

        // 创建临时皮肤选择器（不依赖 GoPanel）
        this.skinChooser = new SkinChooser(null);
        goChoosePanel.setSkinChooser(skinChooser);

        // 设置外层主窗口属性
        this.setTitle("Borad Game");
        this.setSize(1523, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 向JPanel添加创建的各个窗口实例
        contentPanel.add(mainPanel, "主界面");
        contentPanel.add(gameChoosePanel, "游戏选择界面");
        contentPanel.add(achievementPanel, "成就界面");
        contentPanel.add(settingPanel, "设置界面");
        contentPanel.add(goChoosePanel, "围棋选择界面");

        // 外层窗口加载JPanel容器
        this.setContentPane(contentPanel);
    }

    // 游戏初始化
    public void initGame() {
        // 如果游戏面板已存在，先移除旧的
        if (goPanel != null) {
            contentPanel.remove(goPanel);
        }

        // 创建新的游戏面板
        goPanel = GoPanel.getGoPanel();

        // 将皮肤选择器绑定到新创建的 GoPanel
        skinChooser.setGoPanel(goPanel);

        // 应用当前已选择的皮肤
        skinChooser.applySavedSkin();

        // 添加到内容面板
        contentPanel.add(goPanel, "围棋游戏界面");
    }

    static public void main(String[] args) throws Exception {
        // 创建程序外层主窗口实例（单例模式）
        MainWindow outerWindow = getInstance();
        // 调用初始化函数
        outerWindow.init();
        // 设置可见
        outerWindow.setVisible(true);

        // 窗口显示后启动音乐播放
        SwingUtilities.invokeLater(() -> {
            MusicPlayer.getInstance().play();
        });
    }
}