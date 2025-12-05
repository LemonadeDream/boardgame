package com.github.lemonadedream.boardgame.controller.network;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 简单的 Swing 对话框骨架, 用于配置网络连接(Server/Client)
 * 如何使用:
 * ConnectionDialog dlg = new ConnectionDialog(ownerFrame);
 * ConnectionConfig cfg = dlg.showDialog();
 */
public class ConnectionDialog extends JDialog {
    public static class ConnectionConfig {
        public boolean startServer;
        public String host;
        public int port;
        public String playerId;
    }

    private ConnectionConfig result = null;

    public ConnectionDialog(Frame owner) {
        super(owner, "连接设置", true);
        setLayout(new BorderLayout());
        JPanel p = new JPanel();
        p.add(new JLabel("(骨架) 请实现UI或直接使用 NetworkManager API"));
        add(p, BorderLayout.CENTER);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> {
            // 默认返回空配置(调用方根据需要处理)
            result = new ConnectionConfig();
            result.startServer = false;
            result.host = "localhost";
            result.port = 9000;
            result.playerId = "player1";
            setVisible(false);
        });
        add(ok, BorderLayout.SOUTH);
        pack();
    }

    public ConnectionConfig showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
}
