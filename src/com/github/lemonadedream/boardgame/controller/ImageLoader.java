package com.github.lemonadedream.boardgame.controller;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * ͼƬ���������Ź����ࡣ
 * <p>
 * �ṩ�� classpath ����ͼƬ�ķ��������ṩ�ȱ����Ź����Ա��� UI �л����ʵ���С��ͼ��
 */
public class ImageLoader {
    /**
     * ����ͼƬ��Դ������ classpath����
     * <p>
     * ʹ����� classpath ·��������Դ���ɴ����� '/' ��ͷ�򲻴� '/' ��·������
     * ���ٳ����ļ�ϵͳ·����������ʧ���򷵻� null��
     *
     * @param resourcePath classpath ���·��
     * @return BufferedImage��������ʧ�ܻ� resourcePath Ϊ null �򷵻� null
     */
    public static BufferedImage load(String resourcePath) {
        if (resourcePath == null)
            return null;
        BufferedImage image = null;
        try {
            // ���Դ� classpath ������Դ��Ϊ�˼��ݴ����·����ʽ����û��ǰ�� '/' ���Զ����䡣
            // ���紫�� "resources/images/board/xxx.png" �� "/resources/images/board/xxx.png"
            // ���ɹ�����
            InputStream is = ImageLoader.class
                    .getResourceAsStream(resourcePath.startsWith("/") ? resourcePath : ("/" + resourcePath));
            if (is != null) {
                // ʹ�� ImageIO ��ȡ InputStream Ϊ BufferedImage
                // ImageIO.read ������ֱ����ȡ��ɻ��׳� IOException�������ļ��𻵻��ʽ��֧�֣�
                image = ImageIO.read(is);
            }
        } catch (IOException e) {
            // ��ʱ������쳣�����÷��� null �����պ�ɸ�Ϊ�׳�����ʱ�쳣���¼��־��
        }
        return image;
    }

    /**
     * �ȱ�����ͼƬ��ʹ����󲻳��������Ŀ�ߡ�
     * <p>
     * ��ԭͼ�ߴ�����Լ����Χ�ڣ��򲻽��зŴ�ֱ�ӷ���ԭʼͼƬ���ã�ע�⣺�����߲����޸ķ��ص�ԭʼͼƬ����
     * ���ص�ͼƬ����Ϊ TYPE_INT_ARGB���Ա㱣��͸��ͨ����
     *
     * @param src  Ҫ���ŵ�ԴͼƬ
     * @param maxW ����ȣ����أ�
     * @param maxH ���߶ȣ����أ�
     * @return ���ź�� BufferedImage����� src Ϊ null ���� null����� maxW �� maxH �����򷵻�ԭʼ src
     */
    public static BufferedImage scaleToFit(BufferedImage src, int maxW, int maxH) {
        if (src == null)
            return null;
        if (maxW <= 0 || maxH <= 0)
            return src;
        // ����ȱ����ű�����ȡˮƽ����ʹ�ֱ���������������Сֵ��
        double ratio = Math.min((double) maxW / src.getWidth(), (double) maxH / src.getHeight());
        // ���ԭͼ�Ѿ�С�ڵ���Ŀ��ߴ磬�򲻷Ŵ�ֱ�ӷ���ԭͼ���ã����ⲻ��Ҫ���ڴ���䣩
        if (ratio >= 1.0)
            return src; // ���Ŵ�ֱ�ӷ���ԭͼ

        // ����Ŀ�����سߴ磨����Ϊ 1��
        int w = Math.max(1, (int) Math.round(src.getWidth() * ratio));
        int h = Math.max(1, (int) Math.round(src.getHeight() * ratio));

        // ������͸��ͨ������ BufferedImage����ʹ�� Graphics2D �����ϻ������ź��ͼƬ
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        // Ϊ�˻�ýϸ����������Ž�������ò�ֵ����Ⱦ��ʾΪ������ѡ��
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // ��ԴͼƬ���Ƶ�Ŀ���С���Ӷ�ʵ������
        g.drawImage(src, 0, 0, w, h, null);
        // �ͷ�ͼ����������Դ
        g.dispose();
        return resized;
    }
}