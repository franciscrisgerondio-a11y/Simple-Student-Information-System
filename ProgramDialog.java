import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProgramDialog extends JDialog {

    private JTextField txtProgramCode;
    private JTextField txtProgramName;
    private JComboBox<String> cmbCollege;
    private boolean saved = false;

    private java.util.List<String[]> masterPrograms;
    private java.util.List<String[]> masterColleges;
    private String originalCode; // null when adding

    public ProgramDialog(JFrame parent,
                         java.util.List<String[]> masterPrograms,
                         java.util.List<String[]> masterColleges,
                         String programCode,
                         String programName,
                         String college) {

        super(parent, true);

        this.masterPrograms = masterPrograms;
        this.masterColleges = masterColleges;
        this.originalCode   = programCode; // null = add mode

        setTitle(programName == null ? "Add Program" : "Edit Program");
        setSize(450, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));
        setContentPane(mainPanel);

        JLabel title = new JLabel(programName == null ? "Add New Program" : "Edit Program");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 12, 15));
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(new EmptyBorder(20, 0, 10, 0));

        form.add(new JLabel("Program Code:"));
        txtProgramCode = new JTextField();
        form.add(txtProgramCode);

        form.add(new JLabel("Program Name:"));
        txtProgramName = new JTextField();
        form.add(txtProgramName);

        form.add(new JLabel("College:"));
        cmbCollege = new JComboBox<>();
        for (String[] r : this.masterColleges) {
            cmbCollege.addItem(r[1]); // college code
        }
        form.add(cmbCollege);

        mainPanel.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 247, 250));

        JButton save   = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        styleButton(save,   new Color(40, 167, 69));
        styleButton(cancel, new Color(220, 53, 69));

        buttons.add(save);
        buttons.add(cancel);

        if (programName != null) {
            JPanel south = new JPanel(new BorderLayout());
            south.setBackground(new Color(245, 247, 250));
            JLabel warn = new JLabel("<html><i>⚠ Changing the code will update all enrolled students.</i></html>");
            warn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            warn.setForeground(new Color(180, 100, 0));
            warn.setBorder(new EmptyBorder(4, 0, 4, 0));
            south.add(warn, BorderLayout.NORTH);
            south.add(buttons, BorderLayout.SOUTH);
            mainPanel.add(south, BorderLayout.SOUTH);
        } else {
            mainPanel.add(buttons, BorderLayout.SOUTH);
        }

        if (programName != null) {
            txtProgramCode.setText(programCode);
            txtProgramName.setText(programName);
            cmbCollege.setSelectedItem(college);
            txtProgramCode.requestFocusInWindow();
        }

        save.addActionListener(e -> {
            String code = txtProgramCode.getText().trim().toUpperCase();
            String name = txtProgramName.getText().trim();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Program code required.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Program name required.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (cmbCollege.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Please select a college.",
                        "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean codeChanged = !code.equals(originalCode);
            if (originalCode == null || codeChanged) {
                for (String[] row : this.masterPrograms) {
                    if (row[0].equalsIgnoreCase(code)) {
                        JOptionPane.showMessageDialog(this,
                                "Program code '" + code + "' already exists.",
                                "Duplicate Program Code",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            txtProgramCode.setText(code);
            saved = true;
            dispose();
        });

        cancel.addActionListener(e -> dispose());
    }

    private void styleButton(JButton button, Color color) {
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(100, 35));
    }

    public boolean isSaved()        { return saved; }
    public String getProgramCode()  { return txtProgramCode.getText().trim().toUpperCase(); }
    public String getProgramName()  { return txtProgramName.getText().trim(); }
    public String getCollege()      { return cmbCollege.getSelectedItem().toString(); }
    public String getOriginalCode() { return originalCode; }
}
