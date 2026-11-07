package com.studentmaster;

import com.studentmaster.dao.IStudentDAO;
import com.studentmaster.dao.StudentDAO;
import com.studentmaster.report.ReportGenerator;
import com.studentmaster.ui.MainFrame;
import com.studentmaster.ui.Theme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Theme.install();
            IStudentDAO dao = new StudentDAO();
            new MainFrame(dao, new ReportGenerator()).setVisible(true);
        });
    }
}
