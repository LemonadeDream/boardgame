package com.github.lemonadedream.boardgame.controller.network;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;

/**
 * 完整的 ConnectionDialog 实现 - 配置网络连接
 */
public class ConnectionDialogImpl extends JDialog {
    public static class ConnectionConfig {
        public boolean startServer;
        public String host;
        public int port;
        public String playerId;
    }

    private ConnectionConfig result = null;
    private JRadioButton serverRadio;
    private JRadioButton clientRadio;
    private JTextField hostField;
    private JTextField portField;
    private JTextField playerIdField;

    public ConnectionDialogImpl(Frame owner) {
        super(owner, "网络连接设置", true);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 中心面板
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 5, 5));

        // Server/Client 选择
        serverRadio = new JRadioButton("启动服务器");
        clientRadio = new JRadioButton("连接到服务器");
        ButtonGroup group = new ButtonGroup();
        group.add(serverRadio);
        group.add(clientRadio);
        clientRadio.setSelected(true);

        centerPanel.add(new JLabel("模式选择:"));
        JPanel radioPanel = new JPanel();
        radioPanel.add(serverRadio);
        radioPanel.add(clientRadio);
        centerPanel.add(radioPanel);

        // Host
        centerPanel.add(new JLabel("服务器地址:"));
        hostField = new JTextField("localhost", 15);
        centerPanel.add(hostField);

        // Port
        centerPanel.add(new JLabel("端口:"));
        portField = new JTextField("9000", 15);
        centerPanel.add(portField);

        // PlayerId
        centerPanel.add(new JLabel("玩家ID:"));
        playerIdField = new JTextField("player1", 15);
        centerPanel.add(playerIdField);

        add(centerPanel, BorderLayout.CENTER);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("连接");
        JButton cancelButton = new JButton("取消");

        okButton.addActionListener(e -> {
            result = new ConnectionConfig();
            result.startServer = serverRadio.isSelected();
            result.host = hostField.getText();
            try {
                result.port = Integer.parseInt(portField.getText());
            } catch (NumberFormatException ex) {
                result.port = 9000;
            }
            result.playerId = playerIdField.getText();
            setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            result = null;
            setVisible(false);
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 250);
        setResizable(false);
    }

    public ConnectionConfig showDialog() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
}
