package com.github.lemonadedream.boardgame.view.panel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;

public class GameChoosePanel extends JPanel {
    // ����������
    private MainWindow window;
    // ������Ϸѡ����水ť���
    JButton chessChoose = new JButton("��������");
    JButton gomokuChoose = new JButton("Χ��");
    JButton backButton = new JButton("����");

    // ��������ť�󶨹���
    public void initButtons() {
        // ���а�ť������¼�������,��OuterWindow��switchPanel�л�
        chessChoose.addActionListener(e -> {
            window.switchPanel(e);
        });
        gomokuChoose.addActionListener(e -> {
            window.switchPanel(e);
        });
        backButton.addActionListener(e -> {
            window.switchPanel(e);
        });
    }

    // ��װѡ��ť
    public void init() {
        // ���ð�ť���л����ܵĺ���
        initButtons();
        // ��ť���������
        this.setLayout(new GridLayout(3, 1, 0, 50));
        this.add(chessChoose);
        this.add(gomokuChoose);
        this.add(backButton);
        this.setBorder(BorderFactory.createEmptyBorder(150, 250, 250, 250));
    }

    public GameChoosePanel(MainWindow window) {
        // ��ֵ
        this.window = window;
        init();
    }
}
