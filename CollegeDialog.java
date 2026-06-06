import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CollegeDialog extends JDialog {

    private JTextField txtCollegeName;
    private JTextField txtCollegeCode;
    private boolean saved = false;

    private java.util.List<String[]> masterColleges;
    private String originalCode; // null when adding

    public CollegeDialog(JFrame parent,
                         java.util.List<String[]> masterColleges,
                         String name,
                         String code) {

        super(parent, true);

        this.masterColleges = masterColleges;
        this.originalCode   = code; // null = add mode

        setTitle(name == null ? "Add College" : "Edit College");
        setSize(450, 340);
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

        formPanel.add(styledLabel("College Name:", labelFont));
        txtCollegeName = new JTextField();
        formPanel.add(txtCollegeName);

        formPanel.add(styledLabel("College Code:", labelFont));
        txtCollegeCode = new JTextField();
        formPanel.add(txtCollegeCode);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Warn about cascade when editing
        if (name != null) {
            JLabel warn = new JLabel("<html><i>⚠ Changing the code will update all linked programs.</i></html>");
            warn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            warn.setForeground(new Color(180, 100, 0));
            warn.setBorder(new EmptyBorder(0, 0, 6, 0));
            mainPanel.add(warn, BorderLayout.SOUTH);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 247, 250));

        JButton save   = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        styleButton(save,   new Color(40, 167, 69));
        styleButton(cancel, new Color(220, 53, 69));

        buttonPanel.add(save);
        buttonPanel.add(cancel);

        // Replace SOUTH with a wrapper when editing so warn + buttons both fit
        if (name != null) {
            JPanel south = new JPanel(new BorderLayout());
            south.setBackground(new Color(245, 247, 250));
            JLabel warn = new JLabel("<html><i>⚠ Changing the code will update all linked programs.</i></html>");
            warn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            warn.setForeground(new Color(180, 100, 0));
            warn.setBorder(new EmptyBorder(4, 0, 4, 0));
            south.add(warn, BorderLayout.NORTH);
            south.add(buttonPanel, BorderLayout.SOUTH);
            mainPanel.add(south, BorderLayout.SOUTH);
        } else {
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        if (name != null) {
            txtCollegeName.setText(name);
            txtCollegeCode.setText(code);
            txtCollegeName.requestFocusInWindow();
        }

        save.addActionListener(e -> {
            String nameValue = txtCollegeName.getText().trim();
            String codeValue = txtCollegeCode.getText().trim().toUpperCase();

            if (nameValue.isEmpty() || codeValue.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "All fields must be filled.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Duplicate check: skip if code unchanged (edit mode)
            boolean codeChanged = !codeValue.equals(originalCode);
            if (originalCode == null || codeChanged) {
                for (String[] row : this.masterColleges) {
                    if (row[1].equalsIgnoreCase(codeValue)) {
                        JOptionPane.showMessageDialog(this,
                                "College code '" + codeValue + "' already exists.",
                                "Duplicate College Code",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            txtCollegeCode.setText(codeValue);
            saved = true;
            dispose();
        });

        cancel.addActionListener(e -> dispose());
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

    public boolean isSaved()       { return saved; }
    public String getCollegeName() { return txtCollegeName.getText().trim(); }
    public String getCollegeCode() { return txtCollegeCode.getText().trim().toUpperCase(); }
    public String getOriginalCode(){ return originalCode; }
}
