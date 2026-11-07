package com.studentmaster.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Theme {

    public static final Color NAVY      = new Color(0x071A2F);
    public static final Color TEAL_DARK = new Color(0x0B4F52);
    public static final Color TEAL      = new Color(0x0F766E);
    public static final Color TEAL_SOFT = new Color(0xB7E4DF);
    public static final Color ROW_ALT   = new Color(0xEEF7F6);
    public static final Color AMBER     = new Color(0xF59E0B);
    public static final Color BLUE      = new Color(0x2563EB);
    public static final Color RED       = new Color(0xDC2626);
    public static final Color SLATE     = new Color(0x475569);
    public static final Color TEXT      = new Color(0x0F172A);
    public static final Color MUTED     = new Color(0x64748B);
    public static final Color BORDER    = new Color(0xCBD5E1);
    public static final Color DISABLED  = new Color(0xE2E8F0);

    private static final String FAMILY = pickFamily();

    private Theme() { }

    public static Font font(int style, float size) {
        return new Font(FAMILY, style, 1).deriveFont(style, size);
    }

    private static String pickFamily() {
        Set<String> available = java.util.Arrays.stream(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
                .collect(Collectors.toSet());
        for (String f : List.of("Segoe UI", "Inter", "Helvetica Neue", "Ubuntu", "Noto Sans", "DejaVu Sans")) {
            if (available.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }

    public static BufferedImage loadImage(String path) {
        try (InputStream in = Theme.class.getResourceAsStream(path)) {
            if (in == null) throw new IOException("Resource not found: " + path);
            return ImageIO.read(in);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load image " + path, e);
        }
    }

    public static ImageIcon icon(String path, int size) {
        return new ImageIcon(loadImage(path).getScaledInstance(size, size, Image.SCALE_SMOOTH));
    }

    public static void install() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        UIManager.put("OptionPane.messageFont", font(Font.PLAIN, 13));
        UIManager.put("OptionPane.buttonFont", font(Font.BOLD, 12));
        UIManager.put("Label.font", font(Font.PLAIN, 13));
        UIManager.put("Button.font", font(Font.BOLD, 12));
        UIManager.put("TextField.font", font(Font.PLAIN, 14));
        UIManager.put("nimbusSelectionBackground", TEAL);
        UIManager.put("nimbusFocus", TEAL);
    }

    public static void styleField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setCaretColor(TEAL);
        field.setBackground(Color.WHITE);
        field.setBorder(fieldBorder(BORDER, 1));
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { field.setBorder(fieldBorder(TEAL, 2)); }
            @Override public void focusLost(FocusEvent e) { field.setBorder(fieldBorder(BORDER, 1)); }
        });
    }

    private static CompoundBorder fieldBorder(Color color, int thickness) {
        int pad = 9 - (thickness - 1);
        return new CompoundBorder(new LineBorder(color, thickness, true), new EmptyBorder(pad, 12 - (thickness - 1), pad, 12 - (thickness - 1)));
    }

    public static void setFieldLocked(JTextField field, boolean locked) {
        field.setEditable(!locked);
        field.setBackground(locked ? DISABLED : Color.WHITE);
        field.setForeground(locked ? MUTED : TEXT);
    }
}
