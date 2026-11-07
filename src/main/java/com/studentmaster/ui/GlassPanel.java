package com.studentmaster.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class GlassPanel extends JPanel {

    private static final int ARC = 24;
    private static final int SHADOW = 7;

    public GlassPanel(LayoutManager layout, int padding) {
        super(layout);
        setOpaque(false);
        setBorder(new EmptyBorder(padding + SHADOW, padding + SHADOW, padding + SHADOW, padding + SHADOW));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();
        for (int i = SHADOW; i >= 1; i--) {
            g2.setColor(new Color(0, 0, 0, 7));
            g2.fill(new RoundRectangle2D.Float(SHADOW - i, SHADOW - i + 3, w - 2 * (SHADOW - i), h - 2 * (SHADOW - i), ARC + i, ARC + i));
        }
        RoundRectangle2D card = new RoundRectangle2D.Float(SHADOW, SHADOW, w - 2 * SHADOW, h - 2 * SHADOW, ARC, ARC);
        g2.setColor(new Color(255, 255, 255, 236));
        g2.fill(card);
        g2.setColor(new Color(255, 255, 255, 160));
        g2.draw(card);
        g2.dispose();
        super.paintComponent(g);
    }
}
