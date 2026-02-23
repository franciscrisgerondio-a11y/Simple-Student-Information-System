import java.awt.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class StudentDialog extends JDialog {

    private JTextField txtStudentId;
    private JTextField txtFirstName;
    private JTextField txtLastName;

    private JComboBox<String> cmbProgram;
    private JComboBox<String> cmbYear;
    private JComboBox<String> cmbGender;
    private JComboBox<String> cmbCollege;

    private boolean saved = false;

    public StudentDialog(JFrame parent,
                     String id,
                     String first,
                     String last,
                     String college,
                     String program,
                     String year,
                     String gender) {

        super(parent, true);

        setTitle(id == null ? "Add Student" : "Edit Student");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));
        setContentPane(mainPanel);

        JLabel title = new JLabel(id == null ? "Add New Student" : "Edit Student");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 12, 15));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(new EmptyBorder(20, 0, 10, 0));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        /* ================= STUDENT ID ================= */
        JLabel lblId = new JLabel("Student ID:");
        lblId.setFont(labelFont);
        formPanel.add(lblId);

        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        /* ================= FIRST NAME ================= */
        JLabel lblFirst = new JLabel("First Name:");
        lblFirst.setFont(labelFont);
        formPanel.add(lblFirst);

        txtFirstName = new JTextField();
        formPanel.add(txtFirstName);

        /* ================= LAST NAME ================= */
        JLabel lblLast = new JLabel("Last Name:");
        lblLast.setFont(labelFont);
        formPanel.add(lblLast);

        txtLastName = new JTextField();
        formPanel.add(txtLastName);

        /* ================= COLLEGE ================= */
        JLabel lblCollege = new JLabel("College:");
        lblCollege.setFont(labelFont);
        formPanel.add(lblCollege);

        cmbCollege = new JComboBox<>();
        loadCollegesFromCSV();
        formPanel.add(cmbCollege);

        /* ================= PROGRAM (Dropdown) ================= */
        JLabel lblProgram = new JLabel("Program:");
        lblProgram.setFont(labelFont);
        formPanel.add(lblProgram);

        cmbProgram = new JComboBox<>();
        formPanel.add(cmbProgram);

        /* ================= YEAR (1–6 Dropdown) ================= */
        JLabel lblYear = new JLabel("Year:");
        lblYear.setFont(labelFont);
        formPanel.add(lblYear);

        cmbYear = new JComboBox<>();
        for (int i = 1; i <= 6; i++) {
            cmbYear.addItem(String.valueOf(i));
        }
        formPanel.add(cmbYear);

        /* ================= GENDER ================= */
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        formPanel.add(lblGender);

        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(cmbGender);

        /* ===== AUTO LOAD FIRST COLLEGE PROGRAMS ===== */
        if (cmbCollege.getItemCount() > 0) {
            cmbCollege.setSelectedIndex(0);
            loadProgramsByCollege(cmbCollege.getSelectedItem().toString());
        }
        cmbCollege.addActionListener(e -> {

            if (cmbCollege.getSelectedItem() != null) {
                String selectedCollege = cmbCollege.getSelectedItem().toString();
                loadProgramsByCollege(selectedCollege);
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        styleButton(save, new Color(40, 167, 69));
        styleButton(cancel, new Color(220, 53, 69));

        buttonPanel.add(save);
        buttonPanel.add(cancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        if (id != null) {
            txtStudentId.setText(id);
            txtStudentId.setEditable(false);
            txtFirstName.setText(first);
            txtLastName.setText(last);
            cmbYear.setSelectedItem(year);
            cmbGender.setSelectedItem(gender);
            cmbCollege.setSelectedItem(college);

            // Load programs under selected college
            loadProgramsByCollege(college);

            // Then set selected program
            cmbProgram.setSelectedItem(program);
        }

        save.addActionListener(e -> saveStudent());
        cancel.addActionListener(e -> dispose());
    }

    /* ================= LOAD PROGRAMS ================= */
    private void loadProgramsByCollege(String selectedCollege) {

        cmbProgram.removeAllItems();

        for (String[] r : CsvUtils.readAllPrograms()) {

            String programCode = r[0];
            String collegeName = r[2];

            if (collegeName.equals(selectedCollege)) {
                cmbProgram.addItem(programCode);
            }
        }
    }

    /* ================= LOAD COLLEGES ================= */
    private void loadCollegesFromCSV() {
        cmbCollege.removeAllItems();

        for (String[] r : CsvUtils.readAllColleges()) {
            cmbCollege.addItem(r[1]);
        }
    }

    /* ================= STYLE BUTTON ================= */
    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 35));
    }

    /* ================= VALIDATION ================= */
    private void saveStudent() {

        String id = txtStudentId.getText().trim();
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();

        if (!Pattern.matches("\\d{4}-\\d{4}", id)) {
            JOptionPane.showMessageDialog(this,
                    "Student ID must be in format xxxx-xxxx (numbers only).",
                    "Invalid ID",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields must be filled.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        saved = true;
        dispose();
    }

    /* ================= GETTERS ================= */

    public boolean isSaved() {
        return saved;
    }

    public String getStudentId() {
        return txtStudentId.getText().trim();
    }

    public String getStudentName() {
        return txtFirstName.getText().trim();
    }

    public String getLastName() {
        return txtLastName.getText().trim();
    }

    public String getProgram() {
        return cmbProgram.getSelectedItem().toString();
    }

    public String getYear() {
        return cmbYear.getSelectedItem().toString();
    }

    public String getGender() {
        return cmbGender.getSelectedItem().toString();
    }

    public String getCollege() {
        return cmbCollege.getSelectedItem().toString();
    }
}