package com.github.lemonadedream.boardgame.view.panel.mainGamePanel;

import java.awt.*;
import javax.swing.*;

import com.github.lemonadedream.boardgame.view.MainWindow;
import com.github.lemonadedream.boardgame.controller.ImageLoader;
import java.awt.image.BufferedImage;

public class BoardgamePanel extends JPanel {
    // ���������ã������Ա㽫�������¼��ص� / ״̬���ʣ�
    private MainWindow window;
    // ͼƬ��Դ·����ÿ��������Դ��벻ͬ·����
    protected String backgroundPath = null;
    protected String boardPath = null;
    // ����ͼƬ
    private BufferedImage backgroundImg;
    private BufferedImage boardImg;
    // ���ź������ͼ���棨��������С��̬���㣩
    private BufferedImage scaledBoardImg;
    // ����������ף����أ��ɸ�Ϊ���������㣩
    private int margin = 40;
    // ������Ϸѡ����水ť���

    // JButton backButton = new JButton("����");

    // ��������ť�󶨹���
    public void initButtons() {
        // ���а�ť������¼�������,��OuterWindow��switchPanel�л�

        // backButton.addActionListener(e -> {
        // window.switchPanel(e);
        // });
    }

    // ��װѡ��ť
    public void init() {
        // ���ð�ť���л����ܵĺ���
        initButtons();
        // ��ť���������

        // this.setLayout(new GridLayout(1, 1, 0, 50));

        // this.add(backButton);

        this.setBorder(BorderFactory.createEmptyBorder(150, 200, 250, 200));
        // ���Լ���ͼƬ��Դ�������Դ�����ڣ��򲻻��׳��쳣��
        backgroundImg = ImageLoader.load(backgroundPath);
        boardImg = ImageLoader.load(boardPath);
    }

    // ���췽����Ĭ����Դ·����
    public BoardgamePanel(MainWindow window) {
        this(window, null, null);
    }

    /**
     * ���췽���������������÷�ָ������������ͼƬ�� classpath ·����
     * �������·��Ϊ null����ʹ��Ĭ���ֶ�ֵ��
     */
    public BoardgamePanel(MainWindow window, String backgroundPath, String boardPath) {
        // ��ֵ��������
        this.window = window;
        // ������÷��ṩ���Զ���·�����򸲸�Ĭ��ֵ
        if (backgroundPath != null)
            this.backgroundPath = backgroundPath;
        if (boardPath != null)
            this.boardPath = boardPath;
        // ��ʼ����壨�����ͼƬ��
        init();
    }

    // ������С�仯ʱ�����¼�����������ͼ��
    private void updateScaledBoardIfNeeded() {
        if (boardImg == null) {
            scaledBoardImg = null;
            return;
        }
        int availableW = Math.max(1, getWidth() - margin * 2);
        int availableH = Math.max(1, getHeight() - margin * 2);
        // Ϊ�˰������ö����������Ƹ߶�Ϊ(���ø߶� * 0.6) ����ֱ��ʹ�ÿ��ø߶�
        int maxBoardH = (int) (availableH * 0.7);
        BufferedImage scaled = ImageLoader.scaleToFit(boardImg, availableW, maxBoardH);
        scaledBoardImg = scaled;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        // ���Ʊ���������б���ͼ����ѱ���ͼ����/ƽ���Ը����������
        if (backgroundImg != null) {
            int w = getWidth();
            int h = getHeight();
            // ֱ�����챳��������С�����ܱ��Σ���Ҳ��ѡ��ȱȲü�ʵ�ָ����Ӿ�
            g2.drawImage(backgroundImg, 0, 0, w, h, null);
        }

        // ���²���������ͼ�������ϲ㣩
        if (boardImg != null) {
            updateScaledBoardIfNeeded();
            if (scaledBoardImg != null) {
                int bw = scaledBoardImg.getWidth();
                // �����̾�����ˮƽ���ߣ��ö��ڿ������򣨼����ϱ� margin��
                int x = (getWidth() - bw) / 2;
                int y = margin; // �ö������Ϸ� margin
                g2.drawImage(scaledBoardImg, x, y, null);
            }
        }

        g2.dispose();
    }
}