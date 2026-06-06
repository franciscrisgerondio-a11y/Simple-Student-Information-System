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
    private String originalId; // null when adding

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
        this.originalId     = id; // null = add mode

        setTitle(id == null ? "Add Student" : "Edit Student");
        setSize(500, 520);
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

        /* ===== Student ID ===== */
        formPanel.add(styledLabel("Student ID:", labelFont));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        /* ===== First Name ===== */
        formPanel.add(styledLabel("First Name:", labelFont));
        txtFirstName = new JTextField();
        formPanel.add(txtFirstName);

        /* ===== Last Name ===== */
        formPanel.add(styledLabel("Last Name:", labelFont));
        txtLastName = new JTextField();
        formPanel.add(txtLastName);

        /* ===== College ===== */
        formPanel.add(styledLabel("College:", labelFont));
        cmbCollege = new JComboBox<>();
        for (String[] r : this.masterColleges) {
            cmbCollege.addItem(r[1]); // college code
        }
        formPanel.add(cmbCollege);

        cmbCollege.addActionListener(e -> {
            if (cmbCollege.getSelectedItem() != null)
                loadProgramsByCollege(cmbCollege.getSelectedItem().toString());
        });

        /* ===== Program ===== */
        formPanel.add(styledLabel("Program:", labelFont));
        cmbProgram = new JComboBox<>();
        formPanel.add(cmbProgram);

        // Initial program load
        if (cmbCollege.getItemCount() > 0) {
            loadProgramsByCollege(cmbCollege.getItemAt(0));
        }

        /* ===== Year ===== */
        formPanel.add(styledLabel("Year:", labelFont));
        cmbYear = new JComboBox<>();
        for (int i = 1; i <= 4; i++) cmbYear.addItem(String.valueOf(i));
        formPanel.add(cmbYear);

        /* ===== Gender ===== */
        formPanel.add(styledLabel("Gender:", labelFont));
        cmbGender = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        formPanel.add(cmbGender);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton save   = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        styleButton(save,   new Color(40, 167, 69));
        styleButton(cancel, new Color(220, 53, 69));

        buttonPanel.add(save);
        buttonPanel.add(cancel);

        if (id != null) {
            // Edit mode — show cascade warning
            JPanel south = new JPanel(new BorderLayout());
            south.setBackground(new Color(245, 247, 250));
            JLabel warn = new JLabel("<html><i>⚠ Changing the Student ID will replace the primary key.</i></html>");
            warn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            warn.setForeground(new Color(180, 100, 0));
            warn.setBorder(new EmptyBorder(4, 0, 4, 0));
            south.add(warn, BorderLayout.NORTH);
            south.add(buttonPanel, BorderLayout.SOUTH);
            mainPanel.add(south, BorderLayout.SOUTH);
        } else {
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        /* ===== Pre-fill in edit mode ===== */
        if (id != null) {
            txtStudentId.setText(id);
            txtFirstName.setText(first);
            txtLastName.setText(last);
            cmbYear.setSelectedItem(year);
            cmbGender.setSelectedItem(gender);

            // Find the college that owns this program, then select both
            if (program != null && !program.equals("NULL")) {
                for (String[] r : this.masterPrograms) {
                    if (r[0].equals(program)) {
                        cmbCollege.setSelectedItem(r[2]); // triggers loadProgramsByCollege via listener
                        break;
                    }
                }
            }
            // Select the program after college is set
            cmbProgram.setSelectedItem(program);

            txtStudentId.requestFocusInWindow();
        }

        save.addActionListener(e -> saveStudent());
        cancel.addActionListener(e -> dispose());
    }

    private void loadProgramsByCollege(String collegeCode) {
        cmbProgram.removeAllItems();
        for (String[] r : this.masterPrograms) {
            if (r[2].equals(collegeCode)) {
                cmbProgram.addItem(r[0]); // program code
            }
        }
    }

    private void saveStudent() {
        String newId = txtStudentId.getText().trim();
        String first = txtFirstName.getText().trim();
        String last  = txtLastName.getText().trim();

        if (!Pattern.matches("\\d{4}-\\d{4}", newId)) {
            JOptionPane.showMessageDialog(this,
                    "Student ID must be in format YYYY-NNNN (numbers only).",
                    "Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (first.isEmpty() || last.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields must be filled.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Duplicate check: skip if ID unchanged in edit mode
        boolean idChanged = !newId.equals(originalId);
        if (originalId == null || idChanged) {
            for (String[] row : this.masterStudents) {
                if (row[0].equals(newId)) {
                    JOptionPane.showMessageDialog(this,
                            "Student ID '" + newId + "' already exists.",
                            "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        saved = true;
        dispose();
    }

    private JLabel styledLabel(String text, Font font) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        return l;
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 35));
    }

    public boolean isSaved()        { return saved; }
    public String getStudentId()    { return txtStudentId.getText().trim(); }
    public String getStudentName()  { return txtFirstName.getText().trim(); }
    public String getLastName()     { return txtLastName.getText().trim(); }
    public String getOriginalId()   { return originalId; }
    public String getProgram() {
        Object sel = cmbProgram.getSelectedItem();
        return sel == null ? "NULL" : sel.toString();
    }
    public String getYear()   { return cmbYear.getSelectedItem().toString(); }
    public String getGender() { return cmbGender.getSelectedItem().toString(); }
}
