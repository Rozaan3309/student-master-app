package com.studentmaster.ui;

import com.studentmaster.util.Validators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReportRangeDialog extends JDialog {

    private final JTextField fromField = new JTextField(8);
    private final JTextField toField = new JTextField(8);
    private final JLabel errorLabel = new JLabel(" ");
    private int[] result;

    private ReportRangeDialog(Frame owner, int minId, int maxId) {
        super(owner, "Print Student Report", true);
        setName("rangeDialog");

        // header strip with the background image
        BackgroundPanel header = new BackgroundPanel("/images/background.jpg", new BorderLayout(14, 0));
        header.setBorder(new EmptyBorder(14, 20, 14, 20));
        header.setPreferredSize(new Dimension(420, 84));
        header.add(new JLabel(Theme.icon("/images/logo.png", 48)), BorderLayout.WEST);
        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        JLabel t = new JLabel("Student Data Report");
        t.setFont(Theme.font(Font.BOLD, 19)); t.setForeground(Color.WHITE);
        JLabel s = new JLabel("Choose which student IDs to include");
        s.setFont(Theme.font(Font.PLAIN, 12)); s.setForeground(new Color(0xBFE9E4));
        titles.add(t); titles.add(s);
        header.add(titles, BorderLayout.CENTER);

        fromField.setName("fromIdField");
        toField.setName("toIdField");
        fromField.setText(String.valueOf(minId));
        toField.setText(String.valueOf(maxId));
        Theme.styleField(fromField);
        Theme.styleField(toField);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(18, 24, 6, 24));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; c.insets = new Insets(0, 0, 4, 12);
        c.gridx = 0; c.gridy = 0; form.add(label("From Student ID"), c);
        c.gridx = 1; c.insets = new Insets(0, 0, 4, 0); form.add(label("To Student ID"), c);
        c.gridy = 1; c.gridx = 0; c.insets = new Insets(0, 0, 8, 12); form.add(fromField, c);
        c.gridx = 1; c.insets = new Insets(0, 0, 8, 0); form.add(toField, c);
        JLabel hint = new JLabel("IDs currently in the database: " + minId + " - " + maxId);
        hint.setFont(Theme.font(Font.PLAIN, 12)); hint.setForeground(Theme.MUTED);
        c.gridy = 2; c.gridx = 0; c.gridwidth = 2; c.insets = new Insets(0, 0, 4, 0); form.add(hint, c);
        errorLabel.setFont(Theme.font(Font.BOLD, 12)); errorLabel.setForeground(Theme.RED);
        c.gridy = 3; form.add(errorLabel, c);

        RoundedButton cancel = new RoundedButton("Cancel", Theme.SLATE, Color.WHITE);
        RoundedButton ok = new RoundedButton("Generate PDF", Theme.TEAL, Color.WHITE);
        cancel.setName("rangeCancelButton");
        ok.setName("rangeGenerateButton");
        cancel.addActionListener(e -> dispose());
        ok.addActionListener(e -> submit());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(new EmptyBorder(4, 14, 18, 14));
        buttons.add(cancel); buttons.add(ok);

        JPanel root = new JPanel(new BorderLayout());
        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
        getRootPane().setDefaultButton(ok);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(Font.BOLD, 12));
        l.setForeground(Theme.SLATE);
        return l;
    }

    private void submit() {
        String err = Validators.studentId(fromField.getText());
        if (err != null) { fail("\"From\": " + err, fromField); return; }
        err = Validators.studentId(toField.getText());
        if (err != null) { fail("\"To\": " + err, toField); return; }
        int from = Integer.parseInt(fromField.getText().trim());
        int to = Integer.parseInt(toField.getText().trim());
        if (from > to) { fail("\"From\" must not be greater than \"To\".", fromField); return; }
        result = new int[]{from, to};
        dispose();
    }

    private void fail(String message, JTextField field) {
        errorLabel.setText(message);
        field.requestFocusInWindow();
        field.selectAll();
    }

    public static int[] ask(Frame owner, int minId, int maxId) {
        ReportRangeDialog d = new ReportRangeDialog(owner, minId, maxId);
        d.setVisible(true);
        return d.result;
    }
}
