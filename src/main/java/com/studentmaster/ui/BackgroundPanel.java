package com.studentmaster.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundPanel extends JPanel {

    private final BufferedImage image;
    private BufferedImage cache;

    public BackgroundPanel(String resourcePath, LayoutManager layout) {
        super(layout);
        this.image = Theme.loadImage(resourcePath);
        setOpaque(true);
        setBackground(Theme.NAVY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        if (cache == null || cache.getWidth() != w || cache.getHeight() != h) {
            cache = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D c = cache.createGraphics();
            c.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            c.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            double scale = Math.max((double) w / image.getWidth(), (double) h / image.getHeight());
            int dw = (int) Math.ceil(image.getWidth() * scale), dh = (int) Math.ceil(image.getHeight() * scale);
            c.drawImage(image, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
            c.dispose();
        }
        g.drawImage(cache, 0, 0, null);
    }
}
