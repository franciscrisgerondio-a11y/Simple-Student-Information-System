import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.*;

public class Main extends JFrame {

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

    public Main() {
        setTitle("Student Directory");
        setSize(1100, 603);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(true);

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
        header.setBackground(new Color(100, 109, 237));

        JLabel title = new JLabel("Student Directory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel switchPanel = new JPanel();
        switchPanel.setBackground(new Color(100, 109, 237));
        
        JButton studentBtn = new JButton("Students");
        JButton collegeBtn = new JButton("Colleges");
        JButton programBtn = new JButton("Programs");
        JButton addBtn = new JButton("Add");

        styleButton(studentBtn, new Color(100, 149, 237));
        styleButton(collegeBtn, new Color(100, 149, 237));
        styleButton(programBtn, new Color(100, 149, 237));
        styleButton(addBtn, new Color(40, 167, 69));

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
        panel.setBackground(new Color(150, 209, 237));

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
        searchPanel.setBackground(new Color(150, 209, 237));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(createTable(), BorderLayout.CENTER);

        return panel;
    }

    /* ================= TABLE ================= */
    private JScrollPane createTable() {

        studentModel = new DefaultTableModel(
            new String[]{"ID", "First Name", "Last Name", "Program", "Year", "Gender", "Actions"}, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 6;
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
        table.getTableHeader().setDefaultRenderer(new SortHeaderRenderer(table));

        table.setBackground(new Color(219, 234, 254));
        table.setShowVerticalLines(true);
        table.setSelectionBackground(new Color(247, 215, 144)); 
        table.setSelectionForeground(Color.BLACK);
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
                table.getTableHeader().repaint();
            }
        });

        table.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ButtonEditor());

        return new JScrollPane(table);
    }

    /* ================= PAGINATION ================= */
    private JPanel createPaginationPanel() {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        panel.setBackground(new Color(150, 209, 237));

        btnPrev = new JButton("<< Prev");
        btnNext = new JButton("Next >>");

        styleButton(btnPrev, new Color(220, 53, 69));
        styleButton(btnNext, new Color(40, 167, 69));
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
                case "STUDENT" -> activeModel.addRow(new Object[]{row[0],row[1],row[2],row[3],row[4],row[5],"Actions"});
                case "COLLEGE" -> activeModel.addRow(new Object[]{row[0],row[1],"Actions"});
                default -> activeModel.addRow(new Object[]{row[0],row[1],row[2],"Actions"});
            }
        }

        pageLabel.setText("Page " + currentPage + " of " + totalPages);
    }
    /* ================= ACTIONS ================= */

    private void addStudent() {

        StudentDialog dialog = new StudentDialog(this, masterStudents, masterPrograms, masterColleges, null, null, null, null, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            masterStudents.add(new String[]{
                    dialog.getStudentId(),
                    dialog.getStudentName(),
                    dialog.getLastName(),
                    dialog.getProgram(),
                    dialog.getYear(),
                    dialog.getGender()
            });

            CsvUtils.writeAll(masterStudents);
            refresh();
        }
    }

    private void editStudent(int row) {

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }

        String[] data = masterStudents.get(row);

        StudentDialog dialog = new StudentDialog(
                this,
                masterStudents,
                masterPrograms,
                masterColleges,
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5]
        );

        dialog.setVisible(true);

        if (dialog.isSaved()) {
            masterStudents.set(row, new String[]{
                    dialog.getStudentId(),
                    dialog.getStudentName(),
                    dialog.getLastName(),
                    dialog.getProgram(),
                    dialog.getYear(),
                    dialog.getGender()
            });

            CsvUtils.writeAll(masterStudents);
            refresh();
        }
    }

    private void addCollege() {

        CollegeDialog dialog = new CollegeDialog(this, masterColleges, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            masterColleges.add(new String[]{
                    dialog.getCollegeName(),
                    dialog.getCollegeCode()
            });

            CsvUtils.writeAllColleges(masterColleges);
            refresh();
        }
    }

    private void editCollege(int row) {

        if (row < 0) return;

        String[] data = masterColleges.get(row);

        CollegeDialog dialog = new CollegeDialog(this, masterColleges, data[0], data[1]);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            masterColleges.set(row, new String[]{
                    dialog.getCollegeName(),
                    dialog.getCollegeCode()
            });

            CsvUtils.writeAllColleges(masterColleges);
            refresh();
        }
    }

    private void addProgram() {

        ProgramDialog dialog = new ProgramDialog(this, masterPrograms, masterColleges,null, null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            masterPrograms.add(new String[]{
                    dialog.getProgramCode(),
                    dialog.getProgramName(),
                    dialog.getCollege()
            });

            CsvUtils.writeAllPrograms(masterPrograms);
            refresh();
        }
    }

    private void editProgram(int row) {

        if (row < 0) return;

        String[] data = masterPrograms.get(row);

        ProgramDialog dialog = new ProgramDialog(this, masterPrograms, masterColleges, data[0], data[1], data[2]);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            masterPrograms.set(row, new String[]{
                    dialog.getProgramCode(),
                    dialog.getProgramName(),
                    dialog.getCollege()
            });

            CsvUtils.writeAllPrograms(masterPrograms);
            refresh();
        }
    }

    class SortHeaderRenderer implements TableCellRenderer {

        private final TableCellRenderer defaultRenderer;

        public SortHeaderRenderer(JTable table) {
            this.defaultRenderer = table.getTableHeader().getDefaultRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = defaultRenderer.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (c instanceof JLabel label) {

                label.setHorizontalAlignment(SwingConstants.CENTER);

                String text = value.toString();

                if (column == sortColumn &&
                        !text.equals("Actions")) {

                    text += sortAscending ? " ▲" : " ▼";
                }

                label.setText(text);
            }

            return c;
        }
    }

    /* ================= BUTTONS ================= */

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JButton edit = new JButton("Edit");
            JButton delete = new JButton("Delete");

            edit.setBackground(new Color(40, 167, 69));
            edit.setForeground(Color.WHITE);

            delete.setBackground(new Color(220, 53, 69));
            delete.setForeground(Color.WHITE);

            add(edit);
            add(delete);
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

                int confirm = JOptionPane.showConfirmDialog(
                        Main.this,
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
                    case "COLLEGE" -> {

                        String collegeCode = table.getValueAt(viewRow, 1).toString();

                        int cascadeConfirm = JOptionPane.showConfirmDialog(
                                Main.this,
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

                        java.util.List<String> programsToDelete = new ArrayList<>();

                        for (String[] p : masterPrograms) {
                            if (p[2].equals(collegeCode)) {
                                programsToDelete.add(p[0]); // program code
                            }
                        }

                        for (String[] s : masterStudents) {
                            if (programsToDelete.contains(s[3])) {
                                s[3] = "NULL";
                            }
                        }

                        masterPrograms.removeIf(p -> p[2].equals(collegeCode));

                        masterColleges.removeIf(c -> c[1].equals(collegeCode));

                        CsvUtils.writeAll(masterStudents);
                        CsvUtils.writeAllPrograms(masterPrograms);
                        CsvUtils.writeAllColleges(masterColleges);

                        refresh();
                    }
                    case "PROGRAM" ->                         {
                        String programCode = table.getValueAt(viewRow, 0).toString();

                        int cascadeConfirm = JOptionPane.showConfirmDialog(
                                Main.this,
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
                            if (s[3].equals(programCode)) {
                                s[3] = "NULL";
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

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 35));
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

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}