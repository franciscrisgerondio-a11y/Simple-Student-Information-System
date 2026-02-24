import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CollegeDialog extends JDialog {

    private JTextField txtCollegeName;
    private JTextField txtCollegeCode;
    private boolean saved = false;

    private java.util.List<String[]> masterColleges;

    public CollegeDialog(JFrame parent,
                     java.util.List<String[]> masterColleges,
                     String name,
                     String code) {

        super(parent, true);

        this.masterColleges = masterColleges;

        setTitle(name == null ? "Add College" : "Edit College");
        setSize(450, 320);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        mainPanel.setBackground(new Color(245, 247, 250));
        setContentPane(mainPanel);

        JLabel title = new JLabel(name == null ? "Add New College" : "Edit College");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 12, 15));
        formPanel.setBackground(new Color(245, 247, 250));
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);

        JLabel lblName = new JLabel("College Name:");
        lblName.setFont(labelFont);
        formPanel.add(lblName);

        txtCollegeName = new JTextField();
        formPanel.add(txtCollegeName);

        JLabel lblCode = new JLabel("College Code:");
        lblCode.setFont(labelFont);
        formPanel.add(lblCode);

        txtCollegeCode = new JTextField();
        formPanel.add(txtCollegeCode);

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

        if (name != null) {
            txtCollegeName.setText(name);
            txtCollegeCode.setText(code);
        }

        save.addActionListener(e -> {

            String nameValue = txtCollegeName.getText().trim();
            String codeValue = txtCollegeCode.getText().trim();

            if (nameValue.isEmpty() || codeValue.isEmpty()) {

                JOptionPane.showMessageDialog(this,
                        "All fields must be filled.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtCollegeCode.isEditable()) {

                for (String[] row : this.masterColleges) {

                    if (row[1].equalsIgnoreCase(codeValue)) {

                        JOptionPane.showMessageDialog(this,
                                "College code already exists.",
                                "Duplicate College Code",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
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

    public String getCollegeName() {
        return txtCollegeName.getText().trim();
    }

    public String getCollegeCode() {
        return txtCollegeCode.getText().trim();
    }
}