package com.studentmaster.ui;

import com.studentmaster.dao.DataAccessException;
import com.studentmaster.dao.DuplicateStudentException;
import com.studentmaster.dao.IStudentDAO;
import com.studentmaster.model.Student;
import com.studentmaster.report.ReportException;
import com.studentmaster.report.ReportGenerator;
import com.studentmaster.util.Validators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class MainFrame extends JFrame {

    private enum Status { INFO, SUCCESS, ERROR }

    private final IStudentDAO dao;
    private final ReportGenerator reportGenerator;

    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable table = new JTable(tableModel);
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final HintTextField searchField = new HintTextField("Search by ID, name or email...");

    private final RoundedButton addButton = new RoundedButton("Add", Theme.TEAL, Color.WHITE);
    private final RoundedButton updateButton = new RoundedButton("Update", Theme.BLUE, Color.WHITE);
    private final RoundedButton deleteButton = new RoundedButton("Delete", Theme.RED, Color.WHITE);
    private final RoundedButton clearButton = new RoundedButton("New / Clear", Theme.SLATE, Color.WHITE);
    private final RoundedButton reportButton = new RoundedButton("Generate Report (PDF)", Theme.AMBER, Theme.NAVY);

    private final JLabel modeLabel = new JLabel();
    private final JLabel countLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");
    private final JLabel dbLabel = new JLabel();
    private final StudentCard studentCard = new StudentCard();

    private boolean ignoreSelection;

    public MainFrame(IStudentDAO dao, ReportGenerator reportGenerator) {
        super("Student Master");
        this.dao = dao;
        this.reportGenerator = reportGenerator;

        setIconImage(Theme.loadImage("/images/logo.png"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BackgroundPanel root = new BackgroundPanel("/images/background.jpg", new BorderLayout());
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
        setContentPane(root);

        nameComponents();
        wireActions();
        setSize(1120, 760);
        setMinimumSize(new Dimension(980, 720));
        setLocationRelativeTo(null);

        enterNewMode();
        refreshTable();
    }


    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(7, 26, 47, 170));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Theme.AMBER);
                g.fillRect(0, getHeight() - 3, getWidth(), 3);
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(12, 24, 15, 24));

        JPanel titles = new JPanel(new GridLayout(2, 1));
        titles.setOpaque(false);
        JLabel title = new JLabel("Student Master");
        title.setFont(Theme.font(Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Manage student records and print ID-range reports");
        subtitle.setFont(Theme.font(Font.PLAIN, 13));
        subtitle.setForeground(new Color(0x8FE3D8));
        titles.add(title);
        titles.add(subtitle);

        JPanel left = new JPanel(new BorderLayout(14, 0));
        left.setOpaque(false);
        left.add(new JLabel(Theme.icon("/images/logo.png", 54)), BorderLayout.WEST);
        left.add(titles, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.add(reportButton);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new BorderLayout(10, 0));
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(10, 16, 4, 16));
        center.add(buildTablePanel(), BorderLayout.CENTER);
        center.add(buildFormPanel(), BorderLayout.EAST);
        return center;
    }

    private JComponent buildTablePanel() {
        GlassPanel panel = new GlassPanel(new BorderLayout(0, 12), 14);

        Theme.styleField(searchField);
        countLabel.setFont(Theme.font(Font.BOLD, 13));
        countLabel.setForeground(Theme.TEAL);
        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);
        top.add(searchField, BorderLayout.CENTER);
        top.add(countLabel, BorderLayout.EAST);

        table.setRowSorter(sorter);
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(Theme.font(Font.PLAIN, 13));
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(120);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                setForeground(sel ? Color.WHITE : Theme.TEXT);
                setBackground(sel ? Theme.TEAL : (row % 2 == 0 ? Color.WHITE : Theme.ROW_ALT));
                setFont(Theme.font(sel ? Font.BOLD : Font.PLAIN, 13));
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, cell);
        table.setDefaultRenderer(Integer.class, cell);

        JTableHeader th = table.getTableHeader();
        th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(10, 36));
        th.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                l.setOpaque(true);
                l.setBackground(Theme.TEAL_DARK);
                l.setForeground(Color.WHITE);
                l.setFont(Theme.font(Font.BOLD, 13));
                l.setBorder(new EmptyBorder(0, 12, 0, 12));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildFormPanel() {
        GlassPanel panel = new GlassPanel(new GridBagLayout(), 16);
        panel.setPreferredSize(new Dimension(350, 10));

        JLabel heading = new JLabel("Student Details");
        heading.setFont(Theme.font(Font.BOLD, 18));
        heading.setForeground(Theme.TEAL_DARK);
        modeLabel.setFont(Theme.font(Font.BOLD, 12));

        Theme.styleField(idField);
        Theme.styleField(nameField);
        Theme.styleField(emailField);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0; c.insets = new Insets(0, 0, 2, 0); panel.add(heading, c);
        c.gridy = 1; c.insets = new Insets(0, 0, 12, 0); panel.add(modeLabel, c);
        c.gridy = 2; c.insets = new Insets(0, 0, 3, 0); panel.add(fieldLabel("Student ID"), c);
        c.gridy = 3; c.insets = new Insets(0, 0, 10, 0); panel.add(idField, c);
        c.gridy = 4; c.insets = new Insets(0, 0, 3, 0); panel.add(fieldLabel("Full name"), c);
        c.gridy = 5; c.insets = new Insets(0, 0, 10, 0); panel.add(nameField, c);
        c.gridy = 6; c.insets = new Insets(0, 0, 3, 0); panel.add(fieldLabel("Email"), c);
        c.gridy = 7; c.insets = new Insets(0, 0, 14, 0); panel.add(emailField, c);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 10, 10));
        buttons.setOpaque(false);
        buttons.add(addButton); buttons.add(updateButton);
        buttons.add(deleteButton); buttons.add(clearButton);
        c.gridy = 8; c.insets = new Insets(0, 0, 14, 0); panel.add(buttons, c);

        c.gridy = 9; c.insets = new Insets(0, 0, 0, 0); panel.add(studentCard, c);

        c.gridy = 10; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        JPanel filler = new JPanel(); filler.setOpaque(false); panel.add(filler, c);
        return panel;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(7, 26, 47, 190));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(8, 24, 8, 24));
        statusLabel.setFont(Theme.font(Font.BOLD, 13));
        statusLabel.setForeground(Color.WHITE);
        dbLabel.setFont(Theme.font(Font.PLAIN, 12));
        bar.add(statusLabel, BorderLayout.CENTER);
        bar.add(dbLabel, BorderLayout.EAST);
        return bar;
    }

    private static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font(Font.BOLD, 12));
        l.setForeground(Theme.SLATE);
        return l;
    }

    private void nameComponents() {
        idField.setName("idField");
        nameField.setName("nameField");
        emailField.setName("emailField");
        searchField.setName("searchField");
        table.setName("studentTable");
        addButton.setName("addButton");
        updateButton.setName("updateButton");
        deleteButton.setName("deleteButton");
        clearButton.setName("clearButton");
        reportButton.setName("reportButton");
        statusLabel.setName("statusLabel");
    }

    // --------------------------------------------------------------- actions

    private void wireActions() {
        addButton.addActionListener(e -> onAdd());
        updateButton.addActionListener(e -> onUpdate());
        deleteButton.addActionListener(e -> onDelete());
        clearButton.addActionListener(e -> { enterNewMode(); idField.requestFocusInWindow(); });
        reportButton.addActionListener(e -> onReport());

        java.awt.event.ActionListener enter = e -> { if (addButton.isEnabled()) onAdd(); else onUpdate(); };
        idField.addActionListener(enter);
        nameField.addActionListener(enter);
        emailField.addActionListener(enter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting() || ignoreSelection) return;
            int viewRow = table.getSelectedRow();
            if (viewRow < 0) { enterNewMode(); return; }
            int row = table.convertRowIndexToModel(viewRow);
            Student s = new Student((Integer) tableModel.getValueAt(row, 0),
                    (String) tableModel.getValueAt(row, 1), (String) tableModel.getValueAt(row, 2));
            enterEditMode(s);
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
        countLabel.setText(table.getRowCount() + " of " + tableModel.getRowCount() + " students");
    }

    private void onAdd() {
        Student s = readForm();
        if (s == null) return;
        try {
            dao.addStudent(s);
            searchField.setText("");
            refreshTable();
            selectById(s.getStudentId());
            setStatus("Student " + s.getStudentId() + " (" + s.getName() + ") added successfully!", Status.SUCCESS);
        } catch (DuplicateStudentException ex) {
            setStatus(ex.getMessage(), Status.ERROR);
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\nPlease choose a different ID.",
                    "Duplicate ID", JOptionPane.WARNING_MESSAGE);
            idField.requestFocusInWindow();
        } catch (DataAccessException ex) {
            showError(ex);
        }
    }

    private void onUpdate() {
        Student s = readForm();
        if (s == null) return;
        try {
            if (dao.updateStudent(s)) {
                refreshTable();
                selectById(s.getStudentId());
                setStatus("Student " + s.getStudentId() + " updated successfully!", Status.SUCCESS);
            } else {
                refreshTable();
                setStatus("Student " + s.getStudentId() + " no longer exists.", Status.ERROR);
            }
        } catch (DataAccessException ex) {
            showError(ex);
        }
    }

    private void onDelete() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Nothing selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        int id = (Integer) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete \"" + name + "\" (ID " + id + ")?\nThis cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            boolean removed = dao.deleteStudent(id);
            refreshTable();
            enterNewMode();
            setStatus(removed ? "Student " + id + " (" + name + ") deleted." : "Student " + id + " was already gone.",
                    removed ? Status.SUCCESS : Status.ERROR);
        } catch (DataAccessException ex) {
            showError(ex);
        }
    }

    private void onReport() {
        List<Student> all;
        try {
            all = dao.getAllStudents();
        } catch (DataAccessException ex) {
            showError(ex);
            return;
        }
        if (all.isEmpty()) {
            JOptionPane.showMessageDialog(this, "There are no students to report yet.", "Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int[] range = ReportRangeDialog.ask(this, all.get(0).getStudentId(), all.get(all.size() - 1).getStudentId());
        if (range == null) return;

        int count;
        try {
            count = dao.findByIdRange(range[0], range[1]).size();
        } catch (DataAccessException ex) {
            showError(ex);
            return;
        }
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "No students found with an ID between " + range[0] + " and " + range[1] + ".",
                    "Nothing to print", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        reportButton.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Generating report for IDs " + range[0] + " - " + range[1] + "...", Status.INFO);
        new SwingWorker<Path, Void>() {
            @Override protected Path doInBackground() {
                return reportGenerator.generateStudentReport(range[0], range[1], Path.of("reports"));
            }
            @Override protected void done() {
                reportButton.setEnabled(true);
                setCursor(Cursor.getDefaultCursor());
                try {
                    Path pdf = get();
                    setStatus("Report saved: " + pdf, Status.SUCCESS);
                    offerToOpen(pdf, count);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    setStatus("Report failed: " + cause.getMessage(), Status.ERROR);
                    JOptionPane.showMessageDialog(MainFrame.this, cause.getMessage(), "Report error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void offerToOpen(Path pdf, int count) {
        Object[] options = {"Open PDF", "Close"};
        int pick = JOptionPane.showOptionDialog(this,
                "Report created for " + count + " student(s).\n" + pdf.toAbsolutePath(),
                "Report Generated", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (pick == 0 && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(pdf.toString()));
            } catch (Exception ex) {
                setStatus("Could not open the PDF automatically: " + ex.getMessage(), Status.ERROR);
            }
        }
    }

    private Student readForm() {
        String err = Validators.studentId(idField.getText());
        if (err != null) return invalid(err, idField);
        err = Validators.name(nameField.getText());
        if (err != null) return invalid(err, nameField);
        err = Validators.email(emailField.getText());
        if (err != null) return invalid(err, emailField);
        return new Student(Integer.parseInt(idField.getText().trim()), nameField.getText().trim(), emailField.getText().trim());
    }

    private Student invalid(String message, JTextField field) {
        setStatus(message, Status.ERROR);
        JOptionPane.showMessageDialog(this, message, "Please check the form", JOptionPane.WARNING_MESSAGE);
        field.requestFocusInWindow();
        return null;
    }

    private void enterNewMode() {
        ignoreSelection = true;
        table.clearSelection();
        ignoreSelection = false;
        idField.setText(""); nameField.setText(""); emailField.setText("");
        Theme.setFieldLocked(idField, false);
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        studentCard.setStudent(null);
        modeLabel.setText("NEW STUDENT");
        modeLabel.setForeground(Theme.TEAL);
    }

    private void enterEditMode(Student s) {
        idField.setText(String.valueOf(s.getStudentId()));
        nameField.setText(s.getName());
        emailField.setText(s.getEmail());
        Theme.setFieldLocked(idField, true);
        addButton.setEnabled(false);
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
        studentCard.setStudent(s);
        modeLabel.setText("EDITING STUDENT #" + s.getStudentId());
        modeLabel.setForeground(Theme.BLUE);
    }

    private void refreshTable() {
        try {
            List<Student> students = dao.getAllStudents();
            ignoreSelection = true;
            tableModel.setRowCount(0);
            for (Student s : students) tableModel.addRow(new Object[]{s.getStudentId(), s.getName(), s.getEmail()});
            ignoreSelection = false;
            applyFilter();
            dbLabel.setText("\u25CF Database connected");
            dbLabel.setForeground(new Color(0x86EFAC));
        } catch (DataAccessException ex) {
            ignoreSelection = false;
            dbLabel.setText("\u25CF Database offline");
            dbLabel.setForeground(new Color(0xFCA5A5));
            showError(ex);
        }
    }

    private void selectById(int id) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if ((Integer) tableModel.getValueAt(i, 0) == id) {
                int view = table.convertRowIndexToView(i);
                if (view < 0) return;
                table.setRowSelectionInterval(view, view);
                table.scrollRectToVisible(table.getCellRect(view, 0, true));
                return;
            }
        }
    }

    private void setStatus(String message, Status type) {
        statusLabel.setText(message);
        statusLabel.setForeground(switch (type) {
            case SUCCESS -> new Color(0x86EFAC);
            case ERROR -> new Color(0xFCA5A5);
            default -> Color.WHITE;
        });
    }

    private void showError(DataAccessException ex) {
        setStatus(ex.getMessage(), Status.ERROR);
        JOptionPane.showMessageDialog(this,
                ex.getMessage() + "\n\nCheck that MySQL is running and that db.properties / DB_* settings are correct.",
                "Database error", JOptionPane.ERROR_MESSAGE);
    }
}
