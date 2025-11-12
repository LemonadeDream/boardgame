package com.github.lemonadedream.boardgame.controller;

import com.github.lemonadedream.boardgame.view.component.TimePiece;
import com.github.lemonadedream.boardgame.view.component.RatioButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * GoComponentsAdder：把围棋面板周围常见的 UI 组件添加到容器中。
 * - 顶部/底部按钮条：用于开始/设置/退出等操作
 * - 左侧信息区：显示对局时间、单步用时、当前回合和棋子数量
 * - 右侧聊天区：显示聊天信息，并提供输入框和发送按钮
 * - 中心占位区：用于放置实际的棋盘面板（如 GoPanel）
 *
 * 注意：该类仅负责添加组件与布局，不绑定具体事件，事件由控制器或主窗口统一管理。
 */
public class GoComponentsAdder {
    // 保存计时器面板的引用，方便外部访问和控制
    private JPanel matchTimePanel;
    private JPanel moveTimePanel;
    private JLabel roundValue;
    private JLabel pieceValue;

    // 保存按钮引用，方便外部绑定事件
    private JPanel topBar;
    private JPanel bottomBar;

    // 顶部三个按钮的引用(用于模式切换时修改标签)
    private JButton leftButton; // 对局模式:悔棋, 复盘模式:上一步
    private JButton middleButton; // 始终:退出
    private JButton rightButton; // 对局模式:认输, 复盘模式:下一步

    // 聊天相关组件引用
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendBtn;
    private int currentColor = 1; // 当前发言方颜色: 1=黑方, 2=白方

    /**
     * 获取对局时间的 TimePiece 对象
     * 
     * @return TimePiece 对象，可用于启动、暂停、停止计时
     */
    public TimePiece getMatchTimePiece() {
        if (matchTimePanel != null) {
            return (TimePiece) matchTimePanel.getClientProperty("timePiece");
        }
        return null;
    }

    /**
     * 获取步时的 TimePiece 对象
     * 
     * @return TimePiece 对象，可用于启动、暂停、停止计时
     */
    public TimePiece getMoveTimePiece() {
        if (moveTimePanel != null) {
            return (TimePiece) moveTimePanel.getClientProperty("timePiece");
        }
        return null;
    }

    /**
     * 获取回合信息标签
     * 
     * @return 显示回合数的 JLabel
     */
    public JLabel getRoundLabel() {
        return roundValue;
    }

    /**
     * 获取棋子数量标签
     * 
     * @return 显示棋子数量的 JLabel
     */
    public JLabel getPieceCountLabel() {
        return pieceValue;
    }

    /**
     * 为所有按钮绑定事件监听器
     * 
     * @param listener 事件监听器
     */
    public void bindButtonListener(ActionListener listener) {
        bindListenerToButtons(topBar, listener);
        bindListenerToButtons(bottomBar, listener);
    }

    /**
     * 递归为面板中的所有按钮绑定监听器
     */
    private void bindListenerToButtons(Container container, ActionListener listener) {
        if (container == null)
            return;

        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(listener);
            } else if (comp instanceof Container) {
                bindListenerToButtons((Container) comp, listener);
            }
        }
    }

    public void addTo(Container container) {
        if (!(container.getLayout() instanceof BorderLayout)) {
            container.setLayout(new BorderLayout());
        }

        // 顶部按钮: 三个按钮(左:悔棋/上一步, 中:退出, 右:认输/下一步)
        topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        topBar.setOpaque(false);

        leftButton = new RatioButton("悔棋", 100, 0.5f);
        leftButton.setActionCommand("undo"); // 对局模式命令
        leftButton.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体

        middleButton = new RatioButton("退出", 100, 0.5f);
        middleButton.setActionCommand("exit");
        middleButton.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体

        rightButton = new RatioButton("认输", 100, 0.5f);
        rightButton.setActionCommand("surrender"); // 对局模式命令
        rightButton.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体

        topBar.add(leftButton);
        topBar.add(middleButton);
        topBar.add(rightButton);

        // 底部按钮: 音乐控制按钮（上一首、静音、下一首）
        bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomBar.setOpaque(false);

        RatioButton prevMusicBtn = new RatioButton("上一首", 100, 0.5f);
        prevMusicBtn.setActionCommand("music_prev");
        prevMusicBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));

        RatioButton muteMusicBtn = new RatioButton("静音", 100, 0.5f);
        muteMusicBtn.setActionCommand("music_mute");
        muteMusicBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));

        RatioButton nextMusicBtn = new RatioButton("下一首", 100, 0.5f);
        nextMusicBtn.setActionCommand("music_next");
        nextMusicBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));

        bottomBar.add(prevMusicBtn);
        bottomBar.add(muteMusicBtn);
        bottomBar.add(nextMusicBtn);

        // 对局信息区：左侧竖直排列
        JPanel leftInfo = new JPanel();
        leftInfo.setOpaque(false);
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // 对局总计时间 / 单步用时 / 回合信息，使用 TimePiece 风格展示
        matchTimePanel = TimePiece.createTimeBlock("对局时间", 3600_000, true);
        moveTimePanel = TimePiece.createTimeBlock("步时", 30_000, true);

        JPanel roundInfo = new JPanel(new BorderLayout());
        roundInfo.setOpaque(false);
        JLabel roundTitle = new JLabel("回合");
        roundTitle.setFont(roundTitle.getFont().deriveFont(Font.BOLD));
        roundValue = new JLabel("0", SwingConstants.CENTER);
        roundValue.setPreferredSize(new Dimension(110, 26));
        roundInfo.add(roundTitle, BorderLayout.NORTH);
        roundInfo.add(roundValue, BorderLayout.CENTER);

        // 棋子数量展示（示例，暂未实现动态更新）
        JPanel pieceCount = new JPanel(new BorderLayout());
        pieceCount.setOpaque(false);
        JLabel pieceTitle = new JLabel("棋子数量");
        pieceTitle.setFont(pieceTitle.getFont().deriveFont(Font.BOLD));
        pieceValue = new JLabel("黑:0 白:0", SwingConstants.CENTER);
        pieceValue.setPreferredSize(new Dimension(110, 26));
        pieceCount.add(pieceTitle, BorderLayout.NORTH);
        pieceCount.add(pieceValue, BorderLayout.CENTER);

        leftInfo.add(matchTimePanel);
        leftInfo.add(Box.createVerticalStrut(8));
        leftInfo.add(moveTimePanel);
        leftInfo.add(Box.createVerticalStrut(8));
        leftInfo.add(roundInfo);
        leftInfo.add(Box.createVerticalStrut(8));
        leftInfo.add(pieceCount);

        // 聊天面板：上方显示消息，下方是输入框 + 发送按钮
        JPanel chatPanel = new JPanel(new BorderLayout(4, 4));
        chatPanel.setOpaque(false);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        chatPanel.setPreferredSize(new Dimension(150, 0)); // 固定宽度150像素，高度自适应

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        JPanel chatInput = new JPanel(new BorderLayout(4, 4));
        inputField = new JTextField();
        sendBtn = new RatioButton("发送", 100, 0.5f);
        sendBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        // 绑定发送按钮事件
        sendBtn.addActionListener(e -> sendMessage());
        // 绑定回车键发送
        inputField.addActionListener(e -> sendMessage());

        chatInput.add(inputField, BorderLayout.CENTER);
        chatInput.add(sendBtn, BorderLayout.EAST);
        chatPanel.add(chatScroll, BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);

        // 中央占位容器，用于放置棋盘面板（例如 GoPanel）
        JPanel centerOverlay = new JPanel();
        centerOverlay.setOpaque(false);

        container.add(topBar, BorderLayout.NORTH);
        container.add(bottomBar, BorderLayout.SOUTH);
        container.add(leftInfo, BorderLayout.WEST);
        container.add(chatPanel, BorderLayout.EAST);
        container.add(centerOverlay, BorderLayout.CENTER);
    }

    // ========== 模式切换方法 ==========
    /**
     * 切换到复盘模式
     * 修改顶部按钮的文本和命令
     */
    public void switchToReplayMode() {
        leftButton.setText("上一步");
        leftButton.setActionCommand("replay_prev");

        rightButton.setText("下一步");
        rightButton.setActionCommand("replay_next");

        // 禁用底部按钮(充值按钮在复盘时不可用)
        setBottomBarEnabled(false);
    }

    /**
     * 切换到对局模式
     * 恢复顶部按钮的文本和命令
     */
    public void switchToPlayingMode() {
        leftButton.setText("悔棋");
        leftButton.setActionCommand("undo");

        rightButton.setText("认输");
        rightButton.setActionCommand("surrender");

        // 启用底部按钮
        setBottomBarEnabled(true);
    }

    /**
     * 设置底部按钮条的启用状态
     */
    private void setBottomBarEnabled(boolean enabled) {
        if (bottomBar == null)
            return;

        for (Component comp : bottomBar.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            } else if (comp instanceof Container) {
                setContainerButtonsEnabled((Container) comp, enabled);
            }
        }
    }

    /**
     * 递归设置容器中所有按钮的启用状态
     */
    private void setContainerButtonsEnabled(Container container, boolean enabled) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                comp.setEnabled(enabled);
            } else if (comp instanceof Container) {
                setContainerButtonsEnabled((Container) comp, enabled);
            }
        }
    }

    // ========== 聊天功能方法 ==========
    /**
     * 发送消息到聊天区域
     */
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) {
            return;
        }

        // 根据当前颜色添加前缀
        String colorPrefix = (currentColor == 1) ? "黑" : "白";
        String formattedMessage = colorPrefix + ": " + message + "\n";

        // 添加到聊天区域
        chatArea.append(formattedMessage);

        // 清空输入框
        inputField.setText("");

        // 自动滚动到底部
        chatArea.setCaretPosition(chatArea.getDocument().getLength());

        // 切换发言方（可选，如果希望自动轮流）
        // currentColor = (currentColor == 1) ? 2 : 1;
    }

    /**
     * 设置当前发言方颜色
     * 
     * @param color 1=黑方, 2=白方
     */
    public void setCurrentColor(int color) {
        if (color == 1 || color == 2) {
            this.currentColor = color;
        }
    }

    /**
     * 获取聊天文本区域（供外部访问）
     */
    public JTextArea getChatArea() {
        return chatArea;
    }

    /**
     * 清空聊天记录
     */
    public void clearChat() {
        if (chatArea != null) {
            chatArea.setText("");
        }
    }
}