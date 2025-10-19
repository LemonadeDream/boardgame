package com.github.lemonadedream.boardgame.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.panel.AchievementPanel;
import com.github.lemonadedream.boardgame.view.panel.GameChoosePanel;
import com.github.lemonadedream.boardgame.view.panel.MainPanel;
import com.github.lemonadedream.boardgame.view.panel.SettingPanel;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.goPanel;

public class MainWindow extends JFrame {
    // 创建CardLayout和一个内容JPanel来切换窗口
    CardLayout cardLayout = new CardLayout();
    JPanel contentPanel = new JPanel(cardLayout);
    // 创建// 创建各个窗口的Panel实例
    MainPanel mainPanel = new MainPanel(this);
    GameChoosePanel gameChoosePanel = new GameChoosePanel(this);
    AchievementPanel achievementPanel = new AchievementPanel(this);
    SettingPanel settingPanel = new SettingPanel(this);

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
            case "退出":
                System.exit(0);
        }
    }

    // 初始化类
    public void init() {
        // 设置外层主窗口属性
        this.setTitle("Borad Game");
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 向JPanel添加创建的各个窗口实例
        contentPanel.add(mainPanel, "主界面");
        contentPanel.add(gameChoosePanel, "游戏选择界面");
        contentPanel.add(achievementPanel, "成就界面");
        contentPanel.add(settingPanel, "设置界面");
        // 给两个游戏传参
        goPanel.initGoPanel(this);
        // 外层窗口加载JPanel容器
        this.setContentPane(contentPanel);
    }

    static public void main(String[] args) throws Exception {
        // 创建程序外层主窗口实例
        MainWindow outerWindow = new MainWindow();
        // 调用初始化函数
        outerWindow.init();
        // 设置可见
        outerWindow.setVisible(true);
    }
}