import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ProgramDialog extends JDialog {

    private JTextField txtProgramCode;
    private JTextField txtProgramName;
    private JComboBox<String> cmbCollege;
    private boolean saved = false;

    public ProgramDialog(JFrame parent, String programCode, String programName, String college) {

        super(parent, true);

        setTitle(programName == null ? "Add Program" : "Edit Program");
        setSize(450, 350);
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

        /* ===== Program Code ===== */
        form.add(new JLabel("Program Code:"));
        txtProgramCode = new JTextField();
        form.add(txtProgramCode);

        /* ===== Program Name ===== */
        form.add(new JLabel("Program Name:"));
        txtProgramName = new JTextField();
        form.add(txtProgramName);

        /* ===== College ===== */
        form.add(new JLabel("College:"));
        cmbCollege = new JComboBox<>();

        for (String[] r : CsvUtils.readAllColleges()) {
            cmbCollege.addItem(r[1]);
        }

        form.add(cmbCollege);

        mainPanel.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setBackground(new Color(245, 247, 250));

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        styleButton(save, new Color(40, 167, 69));
        styleButton(cancel, new Color(220, 53, 69));

        buttons.add(save);
        buttons.add(cancel);

        mainPanel.add(buttons, BorderLayout.SOUTH);

        /* ===== If Editing ===== */
        if (programName != null) {
            txtProgramCode.setText(programCode);
            txtProgramCode.setEditable(false);
            txtProgramName.setText(programName);
            cmbCollege.setSelectedItem(college);
        }

        save.addActionListener(e -> {
            

            if (txtProgramCode.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Program code required.");
                return;
            }

            if (txtProgramName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Program name required.");
                return;
            }

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

    public boolean isSaved() {
        return saved;
    }

    public String getProgramCode() {
        return txtProgramCode.getText().trim();
    }

    public String getProgramName() {
        return txtProgramName.getText().trim();
    }

    public String getCollege() {
        return cmbCollege.getSelectedItem().toString();
    }
}