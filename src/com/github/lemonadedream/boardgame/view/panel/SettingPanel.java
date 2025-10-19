package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class SettingPanel extends JPanel {
    // ����������
    private MainWindow window;
    // ������Ϸѡ����水ť���
    JButton backButton = new JButton("����");

    // ��������ť�󶨹���
    public void initButtons() {
        // ���а�ť������¼�������,��OuterWindow��switchPanel�л�
        backButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // ��װѡ��ť
    public void init() {
        // ���ð�ť���л����ܵĺ���
        initButtons();
        // ��ť���������
        this.setLayout(new GridLayout(1, 1, 0, 50));
        this.add(backButton);
        this.setBorder(BorderFactory.createEmptyBorder(150, 250, 250, 250));
    }

    // ���췽��
    public SettingPanel(MainWindow window) {
        // ��ֵ��������
        this.window = window;
        // ��ʼ�����
        init();
    }
}
