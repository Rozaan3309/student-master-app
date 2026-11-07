package com.studentmaster.tools;

import com.studentmaster.dao.IStudentDAO;
import com.studentmaster.dao.StudentDAO;
import com.studentmaster.report.ReportGenerator;
import com.studentmaster.ui.MainFrame;
import com.studentmaster.ui.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public class CaptureRunner {

    private static final int TEST_ID = 2001;
    private static final Path DIR = Path.of("captures");
    private static Robot robot;
    private static MainFrame frame;

    public static void main(String[] args) throws Exception {
        Files.createDirectories(DIR);
        robot = new Robot();
        IStudentDAO dao = new StudentDAO();
        dao.deleteStudent(TEST_ID);

        onEdt(() -> {
            Theme.install();
            frame = new MainFrame(dao, new ReportGenerator());
            frame.setVisible(true);
        });
        pause(1500);
        shot("01_read_student_list");

        setText("idField", String.valueOf(TEST_ID));
        setText("nameField", "Nadia Rahmawati");
        setText("emailField", "nadia.rahmawati@students.campus.id");
        shot("02_create_form_filled");
        onEdt(() -> ((AbstractButton) find("addButton")).doClick());
        shot("03_create_success");

        setText("searchField", "nadia");
        shot("04_read_search");
        setText("searchField", "");
        pause(300);

        selectRow(TEST_ID);
        setText("nameField", "Nadia Rahmawati Putri");
        setText("emailField", "nadia.putri@students.campus.id");
        shot("05_update_form_edited");
        onEdt(() -> ((AbstractButton) find("updateButton")).doClick());
        shot("06_update_success");

        SwingUtilities.invokeLater(() -> ((AbstractButton) find("deleteButton")).doClick());
        JDialog confirm = waitForDialog("Confirm Deletion");
        shot("07_delete_confirm");
        clickButton(confirm, "Yes");
        pause(600);
        shot("08_delete_success");

        SwingUtilities.invokeLater(() -> ((AbstractButton) find("reportButton")).doClick());
        waitForDialog("Print Student Report");
        setText("fromIdField", "1010");
        setText("toIdField", "1030");
        shot("09_report_range_form");
        SwingUtilities.invokeLater(() -> ((AbstractButton) find("rangeGenerateButton")).doClick());
        JDialog done = waitForDialog("Report Generated");
        shot("10_report_generated");
        clickButton(done, "Close");
        pause(300);
        System.out.println("Captures saved in " + DIR.toAbsolutePath());
        System.exit(0);
    }

    private static void shot(String name) throws Exception {
        pause(700);
        Rectangle[] bounds = new Rectangle[1];
        onEdt(() -> bounds[0] = new Rectangle(frame.getLocationOnScreen(), frame.getSize()));
        BufferedImage img = robot.createScreenCapture(bounds[0]);
        ImageIO.write(img, "png", DIR.resolve(name + ".png").toFile());
        System.out.println("  saved " + name + ".png");
    }

    private static void onEdt(Runnable r) throws Exception {
        SwingUtilities.invokeAndWait(r);
    }

    private static void pause(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    private static void setText(String fieldName, String text) throws Exception {
        onEdt(() -> ((JTextField) find(fieldName)).setText(text));
    }

    private static void selectRow(int id) throws Exception {
        onEdt(() -> {
            JTable table = (JTable) find("studentTable");
            TableModel m = table.getModel();
            for (int i = 0; i < m.getRowCount(); i++) {
                if ((Integer) m.getValueAt(i, 0) == id) {
                    int v = table.convertRowIndexToView(i);
                    table.setRowSelectionInterval(v, v);
                    table.scrollRectToVisible(table.getCellRect(v, 0, true));
                }
            }
        });
    }

    private static JDialog waitForDialog(String title) throws Exception {
        for (int i = 0; i < 300; i++) {
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog d && d.isShowing() && title.equals(d.getTitle())) {
                    pause(400);
                    return d;
                }
            }
            pause(100);
        }
        throw new IllegalStateException("Dialog not shown: " + title);
    }

    private static void clickButton(Container root, String text) throws Exception {
        for (Component c : all(root)) {
            if (c instanceof AbstractButton b && text.equals(b.getText())) {
                SwingUtilities.invokeLater(b::doClick);
                return;
            }
        }
        throw new IllegalStateException("Button not found: " + text);
    }

    private static Component find(String name) {
        for (Window w : Window.getWindows()) {
            for (Component c : all(w)) {
                if (name.equals(c.getName())) return c;
            }
        }
        throw new IllegalStateException("Component not found: " + name);
    }

    private static java.util.List<Component> all(Container root) {
        java.util.List<Component> list = new java.util.ArrayList<>();
        list.add(root);
        for (Component c : root.getComponents()) {
            if (c instanceof Container cont) list.addAll(all(cont)); else list.add(c);
        }
        return list;
    }
}
