package com.studentmaster.ui;

import com.studentmaster.model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StudentCard extends JPanel {

    private Student student;

    public StudentCard() {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 140));
        setMinimumSize(new Dimension(300, 140));
    }

    public void setStudent(Student student) {
        this.student = student;
        repaint();
    }

    static Color colorFor(int studentId) {
        float hue = (float) ((Math.abs(studentId) * 0.6180339887) % 1.0);
        return Color.getHSBColor(hue, 0.62f, 0.80f);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        Color base = student == null ? new Color(0x94A3B8) : colorFor(student.getStudentId());
        Color top = Color.getHSBColor(Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null)[0], 0.65f, 0.95f);
        Color bottom = Color.getHSBColor(Color.RGBtoHSB(base.getRed(), base.getGreen(), base.getBlue(), null)[0], 0.75f, 0.45f);
        if (student == null) { top = new Color(0xCBD5E1); bottom = new Color(0x64748B); }
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 22, 22);
        g2.setPaint(new GradientPaint(0, 0, top, w, h, bottom));
        g2.fill(shape);
        g2.setClip(shape);
        g2.setColor(new Color(255, 255, 255, 36));
        g2.fillOval(w - 110, -50, 170, 170);
        g2.fillOval(-40, h - 70, 120, 120);
        g2.setClip(null);

        int d = 60, ax = 18, ay = 18;
        g2.setColor(new Color(255, 255, 255, 235));
        g2.fillOval(ax, ay, d, d);
        g2.setColor(student == null ? Theme.SLATE : bottom);
        g2.setFont(Theme.font(Font.BOLD, 24));
        String initials = student == null ? "?" : initials(student.getName());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(initials, ax + (d - fm.stringWidth(initials)) / 2, ay + (d - fm.getHeight()) / 2 + fm.getAscent());

        g2.setColor(Color.WHITE);
        if (student == null) {
            g2.setFont(Theme.font(Font.BOLD, 16));
            g2.drawString("No student selected", 94, 46);
            g2.setFont(Theme.font(Font.PLAIN, 12));
            g2.drawString("Pick a row in the table", 94, 66);
        } else {
            g2.setFont(Theme.font(Font.BOLD, 16));
            g2.drawString(fit(g2, student.getName(), w - 108), 94, 46);
            g2.setFont(Theme.font(Font.PLAIN, 12));
            g2.drawString("Student ID  #" + student.getStudentId(), 94, 66);
            g2.setFont(Theme.font(Font.PLAIN, 13));
            g2.drawString(fit(g2, student.getEmail(), w - 36), 18, h - 22);
        }
        g2.dispose();
    }

    private static String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        String s = parts[0].substring(0, 1);
        if (parts.length > 1) s += parts[parts.length - 1].substring(0, 1);
        return s.toUpperCase();
    }

    private static String fit(Graphics2D g2, String text, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) return text;
        while (text.length() > 1 && fm.stringWidth(text + "…") > maxWidth) text = text.substring(0, text.length() - 1);
        return text + "…";
    }
}
