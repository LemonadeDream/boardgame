package com.github.lemonadedream.boardgame.controller;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * 图片加载与缩放工具类。
 * <p>
 * 提供从 classpath 加载图片的方法，并提供等比缩放工具以便在 UI 中绘制适当大小的图像
 */
public class ImageLoader {
    /**
     * 加载图片资源（仅从 classpath）。
     * <p>
     * 使用相对 classpath 路径加载资源（可传入以 '/' 开头或不带 '/' 的路径）；
     * 不再尝试文件系统路径。若加载失败则返回 null。
     *
     * @param resourcePath classpath 相对路径
     * @return BufferedImage，若加载失败或 resourcePath 为 null 则返回 null
     */
    public static BufferedImage load(String resourcePath) {
        if (resourcePath == null)
            return null;
        BufferedImage image = null;
        // 尝试从 classpath 加载资源。为了兼容传入的路径格式，若没有前导 '/' 则自动补充。
        // 例如传入 "resources/images/board/xxx.png" 或 "/resources/images/board/xxx.png"
        // 都可工作。
        InputStream is = ImageLoader.class
                .getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : ("/" + resourcePath));
        if (is == null) {
            System.err.println("ImageLoader: resource not found on classpath: " + resourcePath);
        } else {
            try {
                image = ImageIO.read(is);
                if (image == null) {
                    System.err.println("ImageLoader: ImageIO.read returned null for resource: " + resourcePath);
                }
            } catch (IOException e) {
                System.err.println(
                        "ImageLoader: IOException while reading resource: " + resourcePath + " -> " + e.getMessage());
            }
        }
        return image;
    }

    /**
     * 等比缩放图片，使其最大不超过给定的宽高。
     * <p>
     * 若原图尺寸已在约束范围内，则不进行放大，直接返回原始图片引用（注意：调用者不得修改返回的原始图片）。
     * 返回的图片类型为 TYPE_INT_ARGB，以便保留透明通道。
     *
     * @param src  要缩放的源图片
     * @param maxW 最大宽度（像素）
     * @param maxH 最大高度（像素）
     * @return 缩放后的 BufferedImage；如果 src 为 null 返回 null；如果 maxW 或 maxH 非正则返回原始 src
     */
    public static BufferedImage scaleToFit(BufferedImage src, int maxW, int maxH) {
        if (src == null)
            return null;
        if (maxW <= 0 || maxH <= 0)
            return src;
        // 计算等比缩放比例（取水平方向和垂直方向允许比例的最小值）
        double ratio = Math.min((double) maxW / src.getWidth(), (double) maxH / src.getHeight());
        // 如果原图已经小于等于目标尺寸，则不放大，直接返回原图引用（避免不必要的内存分配）
        if (ratio >= 1.0)
            return src; // 不放大，直接返回原图

        // 计算目标像素尺寸（至少为 1）
        int w = Math.max(1, (int) Math.round(src.getWidth() * ratio));
        int h = Math.max(1, (int) Math.round(src.getHeight() * ratio));

        // 创建带透明通道的新 BufferedImage，并使用 Graphics2D 在其上绘制缩放后的图片
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        // 为了获得较高质量的缩放结果，设置插值与渲染提示为高质量选项
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 将源图片绘制到目标大小，从而实现缩放
        g.drawImage(src, 0, 0, w, h, null);
        // 释放图形上下文资源
        g.dispose();
        return resized;
    }
}