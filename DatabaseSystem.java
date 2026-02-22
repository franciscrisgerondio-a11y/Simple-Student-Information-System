import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class DatabaseSystem extends JFrame {

    private JTable table;
    private DefaultTableModel studentModel;
    private DefaultTableModel collegeModel;
    private DefaultTableModel programModel;
    private String currentView = "STUDENT";
    private JTextField searchField;

    private int currentPage = 1;
    private final int rowsPerPage = 10;
    private int totalPages = 1;
    private int sortColumn = -1;
    private boolean sortAscending = true;

    private JLabel pageLabel;
    private JButton btnPrev;
    private JButton btnNext;

    private java.util.List<String[]> masterStudents = new ArrayList<>();
    private java.util.List<String[]> masterColleges = new ArrayList<>();
    private java.util.List<String[]> masterPrograms = new ArrayList<>();

    public DatabaseSystem() {
        setTitle("Student Directory");
        setSize(1100, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createPaginationPanel(), BorderLayout.SOUTH);

        loadStudents();
        loadColleges();
        loadPrograms();
    }

    private void applyActionColumnFormatting() {

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        table.getColumn("Actions").setPreferredWidth(160);
        table.getColumn("Actions").setMaxWidth(180);
        table.getColumn("Actions").setMinWidth(140);
    }

    /* ================= HEADER ================= */
    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        header.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("Student Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel switchPanel = new JPanel();
        JButton studentBtn = new JButton("Students");
        JButton collegeBtn = new JButton("Colleges");
        JButton programBtn = new JButton("Programs");
        JButton addBtn = new JButton("Add");

        switchPanel.add(studentBtn);
        switchPanel.add(collegeBtn);
        switchPanel.add(programBtn);
        switchPanel.add(addBtn);

        studentBtn.addActionListener(e -> {
            currentView = "STUDENT";
            title.setText("Student Directory");
            table.setModel(studentModel);

            applyActionColumnFormatting();
            refresh();
        });

        collegeBtn.addActionListener(e -> {
            currentView = "COLLEGE";
            title.setText("College Directory");
            table.setModel(collegeModel);

            applyActionColumnFormatting();
            refresh();
        });

        programBtn.addActionListener(e -> {
            currentView = "PROGRAM";
            title.setText("Program Directory");
            table.setModel(programModel);

            applyActionColumnFormatting();
            refresh();
        });

        addBtn.addActionListener(e -> {
            switch (currentView) {
                case "STUDENT" -> addStudent();
                case "COLLEGE" -> addCollege();
                case "PROGRAM" -> addProgram();
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(switchPanel, BorderLayout.EAST);

        return header;
    }

    /* ================= CONTENT ================= */
    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(createTable(), BorderLayout.CENTER);

        return panel;
    }

    /* ================= TABLE ================= */
    private JScrollPane createTable() {

        studentModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name","College", "Program", "Year", "Gender", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 7;
            }
        };

        collegeModel = new DefaultTableModel(
            new String[]{"College Name", "College Code", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 2;
            }
        };

        programModel = new DefaultTableModel(
            new String[]{"Program Code", "Program Name", "College", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3;
            }
        };

        table = new JTable(studentModel);
        table.setRowHeight(36);
        table.setDefaultRenderer(Object.class, new HighlightRenderer());

        // Sort
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                int col = table.columnAtPoint(e.getPoint());

                // ignore Actions column
                if (table.getColumnName(col).equals("Actions")) return;

                if (sortColumn == col)
                    sortAscending = !sortAscending;
                else {
                    sortColumn = col;
                    sortAscending = true;
                }

                currentPage = 1; // reset to first page
                refresh();
            }
        });

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        return new JScrollPane(table);
    }

    /* ================= PAGINATION ================= */
    private JPanel createPaginationPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        btnPrev = new JButton("<< Prev");
        btnNext = new JButton("Next >>");
        pageLabel = new JLabel();

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                refresh();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                refresh();
            }
        });

        panel.add(btnPrev);
        panel.add(pageLabel);
        panel.add(btnNext);

        return panel;
    }

    /* ================= DATA ================= */
    private void loadStudents() {

        masterStudents = CsvUtils.readAll();

        refresh(); 
    }

    private void loadColleges() {

        masterColleges = CsvUtils.readAllColleges();

        refresh();
    }

    private void loadPrograms() {

        masterPrograms = CsvUtils.readAllPrograms();

        refresh();
    }

    private void refresh() {

        String keyword = searchField.getText().toLowerCase();

        java.util.List<String[]> source;

        source = switch (currentView) {
            case "STUDENT" -> masterStudents;
            case "COLLEGE" -> masterColleges;
            default -> masterPrograms;
        };

        // Search Filter
        java.util.List<String[]> filtered = new ArrayList<>();

        for (String[] row : source) {

            boolean match = false;

            for (String cell : row) {
                if (cell.toLowerCase().contains(keyword)) {
                    match = true;
                    break;
                }
            }

            if (match) filtered.add(row);
        }

        if (sortColumn >= 0) {

            filtered.sort((a, b) -> {

                // prevent sorting on Actions column
                if (sortColumn >= a.length) return 0;

                String valA = a[sortColumn];
                String valB = b[sortColumn];

                int result;

                // numeric safe compare
                try {
                    result = Integer.compare(
                            Integer.parseInt(valA),
                            Integer.parseInt(valB)
                    );
                } catch (NumberFormatException ex) {
                    result = valA.compareToIgnoreCase(valB);
                }

                return sortAscending ? result : -result;
            });
        }
        totalPages = (int) Math.ceil((double) filtered.size() / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, filtered.size());

        // Clear table
        DefaultTableModel activeModel =
                (DefaultTableModel) table.getModel();
        activeModel.setRowCount(0);

        // Add only page rows
        for (int i = start; i < end; i++) {
            String[] row = filtered.get(i);

            switch (currentView) {
                case "STUDENT" -> activeModel.addRow(new Object[]{row[0],row[1],row[2],row[3],row[4],row[5],row[6],"Actions"});
                case "COLLEGE" -> activeModel.addRow(new Object[]{row[0],row[1],"Actions"});
                default -> activeModel.addRow(new Object[]{row[0],row[1],row[2],"Actions"});
            }
        }

        pageLabel.setText("Page " + currentPage + " of " + totalPages);
    }
    /* ================= ACTIONS ================= */

    private void addStudent() {
        StudentDialog dialog = new StudentDialog(this, null, null, null, null, null, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            studentModel.addRow(new Object[]{
                    dialog.getStudentId(),
                    dialog.getStudentName(),
                    dialog.getLastName(),
                    dialog.getCollege(),
                    dialog.getProgram(),
                    dialog.getYear(),
                    dialog.getGender(),
                    "Actions"
            });

            saveStudentsToCSV();
        }
    }

    private void editStudent(int row) {

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }

        String id = studentModel.getValueAt(row, 0).toString();
        String first = studentModel.getValueAt(row, 1).toString();
        String last = studentModel.getValueAt(row, 2).toString();
        String college = studentModel.getValueAt(row, 3).toString();
        String program = studentModel.getValueAt(row, 4).toString();
        String year = studentModel.getValueAt(row, 5).toString();
        String gender = studentModel.getValueAt(row, 6).toString();
        

        StudentDialog dialog = new StudentDialog(this, id, first, last, college, program, year, gender);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            studentModel.setValueAt(dialog.getStudentName(), row, 1);
            studentModel.setValueAt(dialog.getLastName(), row, 2);
            studentModel.setValueAt(dialog.getCollege(), row, 3);
            studentModel.setValueAt(dialog.getProgram(), row, 4);
            studentModel.setValueAt(dialog.getYear(), row, 5);
            studentModel.setValueAt(dialog.getGender(), row, 6);

            saveStudentsToCSV();
        }
    }

    private void saveStudentsToCSV() {
        ArrayList<String[]> data = new ArrayList<>();

        for (int i = 0; i < studentModel.getRowCount(); i++) {
            data.add(new String[]{
                    studentModel.getValueAt(i, 0).toString(),
                    studentModel.getValueAt(i, 1).toString(),
                    studentModel.getValueAt(i, 2).toString(),
                    studentModel.getValueAt(i, 3).toString(),
                    studentModel.getValueAt(i, 4).toString(),
                    studentModel.getValueAt(i, 5).toString(),
                    studentModel.getValueAt(i, 6).toString()
            });
        }

        CsvUtils.writeAll(data);
    }

    private void saveCollegesToCSV() {

        ArrayList<String[]> data = new ArrayList<>();

        for (int i = 0; i < collegeModel.getRowCount(); i++) {
            data.add(new String[]{
                    collegeModel.getValueAt(i, 0).toString(),
                    collegeModel.getValueAt(i, 1).toString()
            });
        }

        CsvUtils.writeAllColleges(data);
    }

    private void addCollege() {

        CollegeDialog dialog = new CollegeDialog(this, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            collegeModel.addRow(new Object[]{
                    dialog.getCollegeName(),
                    dialog.getCollegeCode(),
                    "Actions"
            });

            saveCollegesToCSV();
        }
    }

    private void editCollege(int row) {

        if (row < 0) return;

        String name = collegeModel.getValueAt(row, 0).toString();
        String code = collegeModel.getValueAt(row, 1).toString();

        CollegeDialog dialog = new CollegeDialog(this, name, code);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            collegeModel.setValueAt(dialog.getCollegeName(), row, 0);
            collegeModel.setValueAt(dialog.getCollegeCode(), row, 1);

            saveCollegesToCSV();
        }
    }

    private void saveProgramsToCSV() {

        ArrayList<String[]> data = new ArrayList<>();

        for (int i = 0; i < programModel.getRowCount(); i++) {
            data.add(new String[]{
                    programModel.getValueAt(i, 0).toString(),
                    programModel.getValueAt(i, 1).toString(),
                    programModel.getValueAt(i, 2).toString()
            });
        }

        CsvUtils.writeAllPrograms(data);
    }

    private void addProgram() {

        ProgramDialog dialog = new ProgramDialog(this, null, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            programModel.addRow(new Object[]{
                    dialog.getProgramCode(),
                    dialog.getProgramName(),
                    dialog.getCollege(),
                    "Actions"
            });

            saveProgramsToCSV();
        }
    }

    private void editProgram(int row) {

        if (row < 0) return;

        String code = programModel.getValueAt(row, 0).toString();
        String name = programModel.getValueAt(row, 1).toString();
        String college = programModel.getValueAt(row, 2).toString();

        ProgramDialog dialog = new ProgramDialog(this, code, name, college);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            programModel.setValueAt(dialog.getProgramCode(), row, 0);
            programModel.setValueAt(dialog.getProgramName(), row, 1);
            programModel.setValueAt(dialog.getCollege(), row, 2);

            saveProgramsToCSV();
        }
    }

    /* ================= BUTTONS ================= */

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            add(new JButton("Edit"));
            add(new JButton("Delete"));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {

        JPanel panel = new JPanel();
        JButton edit = new JButton("Edit");
        JButton delete = new JButton("Delete");

        public ButtonEditor() {

            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.add(edit);
            panel.add(delete);

            edit.addActionListener(e -> {

                int viewRow = table.getSelectedRow();
                if (viewRow < 0) return;

                int row = table.convertRowIndexToModel(viewRow);

                switch (currentView) {

                    case "STUDENT" -> editStudent(row);

                    case "COLLEGE" -> editCollege(row);

                    case "PROGRAM" -> editProgram(row);
                }

                fireEditingStopped();
            });

            delete.addActionListener(e -> {

                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }

                int viewRow = table.getSelectedRow();
                if (viewRow < 0) return;

                int row = table.convertRowIndexToModel(viewRow);

                int confirm = JOptionPane.showConfirmDialog(
                        DatabaseSystem.this,
                        "Are you sure you want to delete this record?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    fireEditingStopped();
                    return;
                }

                /* ================= STUDENT DELETE ================= */
                switch (currentView) {
                    case "STUDENT" -> {
                        String studentId = table.getValueAt(viewRow, 0).toString();

                        masterStudents.removeIf(s -> s[0].equals(studentId));

                        CsvUtils.writeAll(masterStudents);
                        refresh();
                        fireEditingStopped();
                    }
                    case "COLLEGE" ->                     {
                        String collegeCode = table.getValueAt(viewRow, 1).toString();

                        int cascadeConfirm = JOptionPane.showConfirmDialog(
                                DatabaseSystem.this,
                                """
                                Deleting this college will also delete its programs
                                and set affected students to NULL.

                                Continue?
                                """,
                                "Cascade Delete Warning",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (cascadeConfirm != JOptionPane.YES_OPTION) {
                            fireEditingStopped();
                            return;
                        }

                        // Remove college
                        masterColleges.removeIf(c -> c[1].equals(collegeCode));

                        // Remove programs under college
                        masterPrograms.removeIf(p -> p[2].equals(collegeCode));

                        // Update students
                        for (String[] s : masterStudents) {
                            if (s[3].equals(collegeCode)) {
                                s[3] = "NULL";
                                s[4] = "NULL";
                            }
                        }

                        CsvUtils.writeAllColleges(masterColleges);
                        CsvUtils.writeAllPrograms(masterPrograms);
                        CsvUtils.writeAll(masterStudents);

                        refresh();
                    }
                    case "PROGRAM" ->                         {
                        String programCode = table.getValueAt(viewRow, 0).toString();

                        int cascadeConfirm = JOptionPane.showConfirmDialog(
                                DatabaseSystem.this,
                                "Deleting this program will set affected students to NULL.\n\nContinue?",
                                "Cascade Delete Warning",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );

                        if (cascadeConfirm != JOptionPane.YES_OPTION) {
                            fireEditingStopped();
                            return;
                        }

                        // Remove program
                        masterPrograms.removeIf(p -> p[0].equals(programCode));

                        // Update students
                        for (String[] s : masterStudents) {
                            if (s[4].equals(programCode)) {
                                s[4] = "NULL";
                            }
                        }

                        CsvUtils.writeAllPrograms(masterPrograms);
                        CsvUtils.writeAll(masterStudents);

                        refresh();
                        }
                    default -> {
                    }
                }

                refresh();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    class HighlightRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (value == null) return c;

            String text = value.toString();
            String keyword = searchField.getText().toLowerCase();

            if (!keyword.isEmpty() && text.toLowerCase().contains(keyword)) {

                String highlighted = text.replaceAll(
                        "(?i)(" + keyword + ")",
                        "<span style='background:yellow;'>$1</span>"
                );

                setText("<html>" + highlighted + "</html>");
            } else {
                setText(text);
            }

            return c;
        }
    }

    /* ================= MAIN ================= */

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException ignored) {}

        SwingUtilities.invokeLater(() -> new DatabaseSystem().setVisible(true));
    }
}