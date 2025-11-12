package com.github.lemonadedream.boardgame.view.skins;

import com.github.lemonadedream.boardgame.controller.ImageLoader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JComponent;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.github.lemonadedream.boardgame.view.panel.mainGamePanel.GoPanel;

public class SkinChooser {
    protected ImageLoader imgLoader;
    protected GoPanel goPanel;

    // 资源文件的基础路径（相对于项目根目录）
    private static final String RESOURCE_BASE_PATH = "src/resources/images/";

    // 当前选中的皮肤路径
    private String currentBlackStonePath = "resources/images/stones/pic1.png";
    private String currentWhiteStonePath = "resources/images/stones/pic2.png";
    private String currentBackgroundPath = "resources/images/board/goBackground.jpg";
    private String currentBoardPath = "resources/images/board/goBoard.png";

    // 当前棋盘透明度
    private float currentBoardOpacity = 1.0f;

    // 皮肤预设
    public static class SkinPreset {
        public final String blackPath;
        public final String whitePath;

        public SkinPreset(String blackPath, String whitePath) {
            this.blackPath = blackPath;
            this.whitePath = whitePath;
        }
    }

    private final Map<String, SkinPreset> stonePresets = new HashMap<>();

    public SkinChooser(GoPanel goPanel) {
        this.goPanel = goPanel;
        registerDefaultPresets();
    }

    /**
     * 设置 GoPanel（用于延迟绑定）
     * 
     * @param goPanel GoPanel 实例
     */
    public void setGoPanel(GoPanel goPanel) {
        this.goPanel = goPanel;
    }

    /**
     * 应用当前已保存的皮肤设置
     */
    public void applySavedSkin() {
        if (goPanel != null) {
            goPanel.setStoneImages(currentBlackStonePath, currentWhiteStonePath);
            goPanel.setBackgroundImage(currentBackgroundPath);
            goPanel.setBoardImage(currentBoardPath);
            goPanel.setBoardOpacity(currentBoardOpacity);
        }
    }

    private void registerDefaultPresets() {
        // 注册默认的棋子皮肤预设
        stonePresets.put("default",
                new SkinPreset("resources/images/stones/pic1.png", "resources/images/stones/white.png"));
        stonePresets.put("green_lightblue",
                new SkinPreset("resources/images/stones/pic_green.png", "resources/images/stones/pic_lightblue.png"));
        stonePresets.put("red_pureblue",
                new SkinPreset("resources/images/stones/pic_red.png", "resources/images/stones/pic_lightblue.png"));
        stonePresets.put("pr_white",
                new SkinPreset("resources/images/stones/pic_pr.png", "resources/images/stones/white.png"));
    }

    // 按预设key设置棋子皮肤
    public void setStoneSkin(String presetKey) {
        SkinPreset preset = stonePresets.get(presetKey);
        if (preset != null) {
            currentBlackStonePath = preset.blackPath;
            currentWhiteStonePath = preset.whitePath;
            // 如果 goPanel 已初始化，立即应用
            if (goPanel != null) {
                goPanel.setStoneImages(preset.blackPath, preset.whitePath);
            }
        }
    }

    // 直接按路径设置棋子皮肤
    public void setStoneSkin(String blackPath, String whitePath) {
        currentBlackStonePath = blackPath;
        currentWhiteStonePath = whitePath;
        // 如果 goPanel 已初始化，立即应用
        if (goPanel != null) {
            goPanel.setStoneImages(blackPath, whitePath);
        }
    }

    // 弹出文件选择器让用户选择黑方棋子图片
    public void chooseBlackStone(JComponent parent) {
        String path = chooseImageFile(parent, "选择黑方棋子图片");
        if (path != null) {
            setStoneSkin(path, currentWhiteStonePath);
        }
    }

    // 弹出文件选择器让用户选择白方棋子图片
    public void chooseWhiteStone(JComponent parent) {
        String path = chooseImageFile(parent, "选择白方棋子图片");
        if (path != null) {
            setStoneSkin(currentBlackStonePath, path);
        }
    }

    // 弹出文件选择器让用户选择背景图片
    public void chooseBackground(JComponent parent) {
        String path = chooseImageFile(parent, "选择背景图片");
        if (path != null) {
            currentBackgroundPath = path;
            // TODO: 实现背景切换功能（需要 GoPanel 支持）
            System.out.println("已选择背景: " + path);
        }
    }

    // 弹出文件选择器让用户选择棋盘图片
    public void chooseBoard(JComponent parent) {
        String path = chooseImageFile(parent, "选择棋盘图片");
        if (path != null) {
            currentBoardPath = path;
            // TODO: 实现棋盘切换功能（需要 GoPanel 支持）
            System.out.println("已选择棋盘: " + path);
        }
    }

    // ========== 新增：预设选择方法 ==========

    /**
     * 设置棋盘预设
     * 
     * @param presetKey "default" = goBoard.png, "colorless" = goBoard_colorless.png
     */
    public void setBoardPreset(String presetKey) {
        switch (presetKey) {
            case "default":
                currentBoardPath = "resources/images/board/goBoard.png";
                break;
            case "colorless":
                currentBoardPath = "resources/images/board/goBoard_colorless.png";
                break;
            default:
                System.out.println("未知的棋盘预设: " + presetKey);
                return;
        }
        System.out.println("已设置棋盘预设: " + presetKey + " -> " + currentBoardPath);

        // 如果 goPanel 已初始化，立即应用
        if (goPanel != null) {
            goPanel.setBoardImage(currentBoardPath);
        }
    }

    /**
     * 设置背景预设
     * 
     * @param presetKey "default" = goBackground.jpg, "ano" = AnobackGround.png
     */
    public void setBackgroundPreset(String presetKey) {
        switch (presetKey) {
            case "default":
                currentBackgroundPath = "resources/images/board/goBackground.jpg";
                break;
            case "ano":
                currentBackgroundPath = "resources/images/board/AnobackGround.png";
                break;
            default:
                System.out.println("未知的背景预设: " + presetKey);
                return;
        }
        System.out.println("已设置背景预设: " + presetKey + " -> " + currentBackgroundPath);

        // 如果 goPanel 已初始化，立即应用
        if (goPanel != null) {
            goPanel.setBackgroundImage(currentBackgroundPath);
        }
    }

    // 通用的图片文件选择器
    private String chooseImageFile(JComponent parent, String title) {
        JFileChooser fileChooser = new JFileChooser();

        // 设置默认打开目录为资源文件夹
        File defaultDir = new File(RESOURCE_BASE_PATH);
        if (defaultDir.exists()) {
            fileChooser.setCurrentDirectory(defaultDir);
        }

        fileChooser.setDialogTitle(title);
        fileChooser.setFileFilter(new FileNameExtensionFilter("图片文件 (*.png, *.jpg, *.jpeg)", "png", "jpg", "jpeg"));

        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // 获取所有可用的预设名称
    public String[] getAvailablePresets() {
        return stonePresets.keySet().toArray(new String[0]);
    }

    // Getter方法
    public String getCurrentBlackStonePath() {
        return currentBlackStonePath;
    }

    public String getCurrentWhiteStonePath() {
        return currentWhiteStonePath;
    }

    public String getCurrentBackgroundPath() {
        return currentBackgroundPath;
    }

    public String getCurrentBoardPath() {
        return currentBoardPath;
    }

    /**
     * 设置棋盘透明度
     * 
     * @param opacity 透明度值 (0.0f = 完全透明, 1.0f = 完全不透明)
     */
    public void setBoardOpacity(float opacity) {
        currentBoardOpacity = Math.max(0.0f, Math.min(1.0f, opacity));
        // 如果 goPanel 已初始化，立即应用
        if (goPanel != null) {
            goPanel.setBoardOpacity(currentBoardOpacity);
        }
    }

    /**
     * 获取当前棋盘透明度
     * 
     * @return 当前透明度值
     */
    public float getBoardOpacity() {
        return currentBoardOpacity;
    }
}