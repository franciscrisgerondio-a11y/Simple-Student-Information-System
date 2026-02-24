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

    private java.util.List<String[]> masterStudents;
    private java.util.List<String[]> masterPrograms;
    private java.util.List<String[]> masterColleges;

    public StudentDialog(JFrame parent,
                     java.util.List<String[]> masterStudents,
                     java.util.List<String[]> masterPrograms,
                     java.util.List<String[]> masterColleges,
                     String id,
                     String first,
                     String last,
                     String program,
                     String year,
                     String gender) {

        super(parent, true);

        this.masterStudents = masterStudents;
        this.masterPrograms = masterPrograms;
        this.masterColleges = masterColleges;

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
        for (String[] r : this.masterColleges) {
            cmbCollege.addItem(r[1]);
        }
        formPanel.add(cmbCollege);

        cmbCollege.addActionListener(e -> {
            String selectedCollege = cmbCollege.getSelectedItem().toString();
            loadProgramsByCollege(selectedCollege);
        });

        /* ================= PROGRAM ================= */
        JLabel lblProgram = new JLabel("Program:");
        lblProgram.setFont(labelFont);
        formPanel.add(lblProgram);

        cmbProgram = new JComboBox<>();
        formPanel.add(cmbProgram);

        // Initial load
        if (cmbCollege.getItemCount() > 0) {
            loadProgramsByCollege(cmbCollege.getItemAt(0));
        }

        /* ================= YEAR (1–4 Dropdown) ================= */
        JLabel lblYear = new JLabel("Year:");
        lblYear.setFont(labelFont);
        formPanel.add(lblYear);

        cmbYear = new JComboBox<>();
        for (int i = 1; i <= 4; i++) {
            cmbYear.addItem(String.valueOf(i));
        }
        formPanel.add(cmbYear);

        /* ================= GENDER ================= */
        JLabel lblGender = new JLabel("Gender:");
        lblGender.setFont(labelFont);
        formPanel.add(lblGender);

        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(cmbGender);

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

            for (String[] r : this.masterPrograms) {
                if (r[0].equals(program)) {
                    cmbCollege.setSelectedItem(r[2]);
                    loadProgramsByCollege(r[2]);
                    break;
                }
}
            cmbProgram.setSelectedItem(program);

        }

        save.addActionListener(e -> saveStudent());
        cancel.addActionListener(e -> dispose());
    }

    /* ================= LOAD PROGRAMS ================= */
    private void loadProgramsByCollege(String collegeCode) {

        cmbProgram.removeAllItems();

        for (String[] r : this.masterPrograms) {
            if (r[2].equals(collegeCode)) {
                cmbProgram.addItem(r[0]); // program code
            }
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

        /* ================= ID FORMAT VALIDATION ================= */
        if (!Pattern.matches("\\d{4}-\\d{4}", id)) {
            JOptionPane.showMessageDialog(this,
                    "Student ID must be in format xxxx-xxxx (numbers only).",
                    "Invalid ID",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        /* ================= EMPTY FIELD VALIDATION ================= */
        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields must be filled.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        /* ================= DUPLICATE ID CHECK ================= */
    if (txtStudentId.isEditable()) {

        for (String[] row : this.masterStudents) {

            if (row[0].equals(id)) {

                JOptionPane.showMessageDialog(this,
                        "Student ID already exists.",
                        "Duplicate ID",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
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
        Object selected = cmbProgram.getSelectedItem();
        return selected == null ? "NULL" : selected.toString();
    }

    public String getYear() {
        return cmbYear.getSelectedItem().toString();
    }

    public String getGender() {
        return cmbGender.getSelectedItem().toString();
    }

}