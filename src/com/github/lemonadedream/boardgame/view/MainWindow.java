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
    // ����CardLayout��һ������JPanel���л�����
    CardLayout cardLayout = new CardLayout();
    JPanel contentPanel = new JPanel(cardLayout);
    // ����// �����������ڵ�Panelʵ��
    MainPanel mainPanel = new MainPanel(this);
    GameChoosePanel gameChoosePanel = new GameChoosePanel(this);
    AchievementPanel achievementPanel = new AchievementPanel(this);
    SettingPanel settingPanel = new SettingPanel(this);

    // ��ť���л������ܿ��ƺ���
    public void switchPanel(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "��ʼ":
                cardLayout.show(contentPanel, "��Ϸѡ�����");
                break;
            case "����":
                cardLayout.show(contentPanel, "���ý���");
                break;
            case "�ɾ�":
                cardLayout.show(contentPanel, "�ɾͽ���");
                break;
            case "����":
                cardLayout.show(contentPanel, "������");
                break;
            case "�˳�":
                System.exit(0);
        }
    }

    // ��ʼ����
    public void init() {
        // �����������������
        this.setTitle("Borad Game");
        this.setSize(800, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // ��JPanel��Ӵ����ĸ�������ʵ��
        contentPanel.add(mainPanel, "������");
        contentPanel.add(gameChoosePanel, "��Ϸѡ�����");
        contentPanel.add(achievementPanel, "�ɾͽ���");
        contentPanel.add(settingPanel, "���ý���");
        // ��������Ϸ����
        goPanel.initGoPanel(this);
        // ��㴰�ڼ���JPanel����
        this.setContentPane(contentPanel);
    }

    static public void main(String[] args) throws Exception {
        // �����������������ʵ��
        MainWindow outerWindow = new MainWindow();
        // ���ó�ʼ������
        outerWindow.init();
        // ���ÿɼ�
        outerWindow.setVisible(true);
    }
}