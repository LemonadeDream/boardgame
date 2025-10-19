package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class MainPanel extends JPanel {
    // ����������
    private MainWindow window;
    // ���������水ť���
    JButton beginButton = new JButton("��ʼ");
    JButton settingButton = new JButton("����");
    JButton closeButton = new JButton("�˳�");
    JButton achivementButton = new JButton("�ɾ�");

    // ��������ť�󶨹���
    public void initButtons() {
        // ���а�ť������¼�������,��OuterWindow��switchPanel�л�
        beginButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        settingButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        closeButton.addActionListener(e -> {
            window.switchPanel(e);
        });
        achivementButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // ��ʼ������,����ť�����Ű�
    public void init() {
        // ���ð�ť���л����ܵĺ���
        initButtons();
        // �����水ť���������
        this.setLayout(new GridLayout(4, 1, 0, 30));
        this.add(beginButton);
        this.add(settingButton);
        this.add(achivementButton);
        this.add(closeButton);
        // �������ñ߿�
        this.setBorder(BorderFactory.createEmptyBorder(150, 200, 150, 200));
    }

    // ���췽��
    public MainPanel(MainWindow window) {
        // ��ֵ
        this.window = window;
        // ���ó�ʼ������
        init();
    }
}