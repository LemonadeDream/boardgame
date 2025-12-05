package com.github.lemonadedream.boardgame.view.component;

import javax.swing.*;
import java.awt.*;
// 导入定时任务线程池执行器，用于周期性执行任务
import java.util.concurrent.ScheduledThreadPoolExecutor;
// 导入定时任务的 Future 对象，用于控制和取消定时任务
import java.util.concurrent.ScheduledFuture;
// 导入时间单位枚举，用于指定时间间隔的单位（秒、毫秒等）
import java.util.concurrent.TimeUnit;

/**
 * TimePiece: 一个可复用的计时器组件，支持启动/暂停/停止，并将时间显示到指定的 JLabel。
 * 能以计时或倒计时模式工作，内部通过定时任务周期性刷新显示。
 */
public class TimePiece {
    // 定义计时器的三种状态：停止、运行中、暂停
    public enum State {
        STOPPED, // 停止状态：计时器未启动或已重置
        RUNNING, // 运行状态：计时器正在计时
        PAUSED // 暂停状态：计时器暂停，但保留当前时间
    }

    // 时间耗尽监听器接口
    public interface TimeExpiredListener {
        /**
         * 时间耗尽时触发（仅倒计时模式）
         */
        void onTimeExpired();
    }

    // ========== 成员变量定义 ==========

    // 用于显示时间的标签组件（final 表示这个引用一旦赋值后不能改变）
    private final JLabel label;

    // 是否为倒计时模式：true = 倒计时，false = 正计时
    private final boolean countDown;

    // 初始时间（毫秒）：倒计时模式下表示起始时间，正计时模式下通常为 0
    private final long initialTime;

    // 基准时间戳（毫秒）：记录最近一次开始/恢复计时的系统时间
    // volatile 关键字确保多线程环境下的可见性（一个线程修改后，其他线程能立即看到）
    private volatile long baseTime;

    // 累计时间（毫秒）：记录暂停前已经过的时间总和
    // 例如：运行 5 秒后暂停，accMillis = 5000；再次运行 3 秒后暂停，accMillis = 8000
    private volatile long accTime;

    // 当前计时器状态：默认为停止状态
    private volatile State state = State.STOPPED;

    // 时间耗尽监听器
    private TimeExpiredListener expiredListener;

    // 标记是否已触发过时间耗尽事件（避免重复触发）
    private volatile boolean hasExpired = false;

    // ========== 静态线程池和定时任务 ==========

    // 静态线程池执行器：所有 TimePiece 实例共享这一个线程池
    // ScheduledThreadPoolExecutor 可以安排任务在指定延迟后执行，或周期性执行
    // 参数 1 表示线程池中只有 1 个线程
    private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1, r -> {
        // 自定义线程工厂：为线程池创建的线程设置名称和属性
        Thread t = new Thread(r, "TimePiece-Refresher"); // 创建线程并命名为 "TimePiece-Refresher"
        t.setDaemon(true); // 设置为守护线程：当所有非守护线程结束时，守护线程会自动终止，不会阻止程序退出
        return t;
    });

    // 当前计时器的刷新任务：保存定时任务的引用，用于控制和取消任务
    // ScheduledFuture<?> 是定时任务的返回对象，可以用来检查任务状态或取消任务
    private ScheduledFuture<?> refreshTask;

    // ========== 构造方法 ==========

    /**
     * 创建一个计时器实例
     * 
     * @param label       用于显示时间的 JLabel（标签组件）
     * @param initialTime 初始毫秒数，用于倒计时模式；非负（负数会被转为 0）
     * @param countDown   true 表示倒计时，false 表示正计时
     */
    public TimePiece(JLabel label, long initialTime, boolean countDown) {
        this.label = label; // 保存标签引用
        // Math.max(0, initialMillis) 确保初始时间不为负数（取 0 和 initialMillis 中的较大值）
        this.initialTime = Math.max(0, initialTime);
        this.countDown = countDown; // 保存计时模式
        updateLabel(); // 立即更新标签显示初始时间
    }

    // ========== 公共方法：获取器 ==========

    /**
     * 获取用于显示时间的标签组件
     * 
     * @return JLabel 对象
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * 获取当前计时器的状态
     * 
     * @return State 枚举值（STOPPED、RUNNING 或 PAUSED）
     */
    public State getState() {
        return state;
    }

    /**
     * 设置时间耗尽监听器
     * 
     * @param listener 监听器实例
     */
    public void setTimeExpiredListener(TimeExpiredListener listener) {
        this.expiredListener = listener;
    }

    // ========== 公共方法：控制计时器 ==========

    /**
     * 启动或恢复计时器
     * - 如果计时器已经在运行，则不做任何操作
     * - 如果是首次启动或从暂停恢复，记录当前系统时间作为基准
     * - 启动定时刷新任务，每 200 毫秒更新一次显示
     */
    public synchronized void start() {
        // 如果已经在运行状态，直接返回（避免重复启动）
        if (state == State.RUNNING)
            return;

        // 记录当前系统时间（毫秒）作为基准时间点
        // System.currentTimeMillis() 返回当前时间与 1970-01-01 00:00:00 之间的毫秒数
        baseTime = System.currentTimeMillis();

        // 将状态设置为运行中
        state = State.RUNNING;

        // 重置过期标记
        hasExpired = false;

        // 启动定时刷新任务（开始周期性更新显示）
        ensureRefresh();
    }

    /**
     * 暂停计时器
     * - 如果计时器不在运行状态，则不做任何操作
     * - 计算并保存已经过的时间到 accMillis
     * - 取消定时刷新任务，并手动更新一次显示
     */
    public synchronized void pause() {
        // 只有在运行状态才能暂停
        if (state != State.RUNNING)
            return;

        // 计算从 baseTime 到现在经过的时间，加上之前累计的时间
        // 例如：之前累计了 5 秒，这次又运行了 3 秒，则 accTime = 5000 + 3000 = 8000
        accTime = elapsedSinceBase() + accTime;

        // 将状态设置为暂停
        state = State.PAUSED;

        // 取消定时刷新任务（停止周期性更新）
        cancelRefresh();

        // 手动更新一次显示（显示暂停时的时间）
        updateLabel();
    }

    /**
     * 停止计时器并重置
     * - 清空累计时间
     * - 状态设置为停止
     * - 取消定时刷新任务
     * - 更新显示为初始时间
     */
    public synchronized void stop() {
        // 清空累计时间（重置为 0）
        accTime = 0;

        // 将状态设置为停止
        state = State.STOPPED;

        // 重置过期标记
        hasExpired = false;

        // 取消定时刷新任务
        cancelRefresh();

        // 更新显示（显示初始时间）
        updateLabel();
    }

    // ========== 私有辅助方法 ==========

    /**
     * 计算从基准时间到现在经过的毫秒数
     * 
     * @return 经过的毫秒数（非负）
     */
    private long elapsedSinceBase() {
        // 当前时间减去基准时间，得到经过的时间
        // Math.max(0, ...) 确保返回值不为负（防止时钟回拨等异常情况）
        return Math.max(0, System.currentTimeMillis() - baseTime);
    }

    /**
     * 确保定时刷新任务正在运行
     * - 如果任务已存在且未取消，则不重复创建
     * - 否则创建一个新的定时任务，每 200 毫秒执行一次
     */
    private void ensureRefresh() {
        // 如果刷新任务已存在且未被取消，直接返回
        if (refreshTask != null && !refreshTask.isCancelled())
            return;

        // 使用线程池的 scheduleAtFixedRate 方法创建周期性任务
        // 参数说明：
        // 1. () -> SwingUtilities.invokeLater(this::updateLabel) 要执行的任务
        // - SwingUtilities.invokeLater 确保 updateLabel 在 Swing 事件线程中执行（GUI 更新必须在此线程）
        // - this::updateLabel 是方法引用，等同于 () -> this.updateLabel()
        // 2. 0 初始延迟时间（0 表示立即开始）
        // 3. 200 任务执行间隔（每隔 200 毫秒执行一次）
        // 4. TimeUnit.MILLISECONDS 时间单位（毫秒）
        refreshTask = EXECUTOR.scheduleAtFixedRate(() -> SwingUtilities.invokeLater(this::updateLabel), 0, 200,
                TimeUnit.MILLISECONDS);
    }

    /**
     * 取消定时刷新任务
     * - 如果任务存在，则取消它并清空引用
     */
    private void cancelRefresh() {
        // 如果刷新任务不为空
        if (refreshTask != null) {
            // 取消任务
            // 参数 false 表示不中断正在执行的任务（如果任务正在运行，让它完成）
            refreshTask.cancel(false);

            // 清空引用，方便垃圾回收
            refreshTask = null;
        }
    }

    /**
     * 更新标签显示的时间
     * - 根据当前状态和模式计算应该显示的时间
     * - 格式化后设置到标签上
     */
    private void updateLabel() {
        // 定义逻辑时间变量（最终要显示的毫秒数）
        long logicalMillis;

        // 计算已使用的时间
        // - 如果正在运行：从基准时间到现在的时间 + 之前累计的时间
        // - 如果暂停或停止：只计算之前累计的时间（0 + accMillis）
        long used = (state == State.RUNNING ? elapsedSinceBase() : 0) + accTime;

        // 根据计时模式计算显示的时间
        if (countDown) {
            // 倒计时模式：初始时间 - 已使用时间
            // Math.max(0, ...) 确保倒计时不会显示负数（倒计时结束后显示 0）
            logicalMillis = Math.max(0, initialTime - used);
            
            // 检查是否时间耗尽（倒计时到 0 且尚未触发过期事件）
            if (logicalMillis == 0 && !hasExpired && state == State.RUNNING) {
                hasExpired = true;
                // 在 Swing 事件线程中触发监听器
                if (expiredListener != null) {
                    SwingUtilities.invokeLater(() -> expiredListener.onTimeExpired());
                }
            }
        } else {
            // 正计时模式：直接显示已使用的时间
            logicalMillis = used;
        }

        // 格式化时间并设置到标签上
        label.setText(formatMillis(logicalMillis));
    }

    /**
     * 将毫秒数格式化为可读的时间字符串
     * 
     * @param ms 毫秒数
     * @return 格式化后的时间字符串，如 "01:30"（1分30秒）或 "2:05:30"（2小时5分30秒）
     */
    private static String formatMillis(long ms) {
        // 将毫秒转换为总秒数（1000 毫秒 = 1 秒）
        long totalSec = ms / 1000;

        // 计算小时数（3600 秒 = 1 小时）
        long h = totalSec / 3600;

        // 计算分钟数：先用总秒数对 3600 取余得到小时后的剩余秒数，再除以 60
        // 例如：7265 秒 = 2 小时 1 分 5 秒
        // 7265 % 3600 = 65 秒（去掉 2 小时）
        // 65 / 60 = 1 分钟
        long m = (totalSec % 3600) / 60;

        // 计算秒数：总秒数对 60 取余
        // 例如：7265 秒，7265 % 60 = 5 秒
        long s = totalSec % 60;

        // 根据是否有小时数选择格式
        if (h > 0)
            // 有小时：格式为 "H:MM:SS"（如 "2:05:30"）
            // %d = 十进制整数，%02d = 至少 2 位数字，不足补 0
            return String.format("%d:%02d:%02d", h, m, s);

        // 无小时：格式为 "MM:SS"（如 "05:30"）
        return String.format("%02d:%02d", m, s);
    }

    // ========== 静态工具方法：创建完整的计时器面板 ==========

    /**
     * 创建一个带标题和时间显示的完整计时器面板
     * - 面板包含标题标签和时间标签
     * - TimePiece 对象存储在面板的 clientProperty 中，方便外部获取和控制
     * 
     * @param title       面板标题（如 "黑方用时"）
     * @param initialTime 初始时间（毫秒）
     * @param countDown   是否为倒计时模式
     * @return 包含标题、时间显示和 TimePiece 对象的 JPanel
     */
    // 说明：创建一个带标题和时间显示的面板，并把对应的 TimePiece 对象放入 clientProperty("timePiece")，方便外部获取和控制
    public static JPanel createTimeBlock(String title, long initialTime, boolean countDown) {
        // 创建主面板，使用 BorderLayout 布局管理器
        // BorderLayout 将容器分为 5 个区域：NORTH（上）、SOUTH（下）、EAST（右）、WEST（左）、CENTER（中）
        // 参数 (4, 4) 表示组件之间的水平和垂直间距都是 4 像素
        JPanel panel = new JPanel(new BorderLayout(4, 4));

        // 设置面板为透明（不绘制背景）
        panel.setOpaque(false);

        // 创建标题标签
        JLabel titleLabel = new JLabel(title);
        // 将标题字体设置为粗体
        // getFont() 获取当前字体
        // deriveFont(Font.BOLD) 创建一个粗体版本的字体
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));

        // 创建时间标签，初始文本为 "00:00"，文本居中对齐
        JLabel timeLabel = new JLabel("00:00", SwingConstants.CENTER);

        // 设置时间标签的首选大小为 110x26 像素
        timeLabel.setPreferredSize(new Dimension(110, 26));

        // 将标题标签添加到面板的上方（NORTH 区域）
        panel.add(titleLabel, BorderLayout.NORTH);

        // 将时间标签添加到面板的中央（CENTER 区域）
        panel.add(timeLabel, BorderLayout.CENTER);

        // 创建 TimePiece 计时器对象，关联到 timeLabel
        TimePiece tp = new TimePiece(timeLabel, initialTime, countDown);

        // 将 TimePiece 对象存储在面板的客户端属性中
        // clientProperty 是 JComponent 提供的键值对存储机制，可以存储任意对象
        // 外部代码可以通过 panel.getClientProperty("timePiece") 获取这个 TimePiece 对象
        panel.putClientProperty("timePiece", tp);

        // 返回创建好的面板
        return panel;
    }
}