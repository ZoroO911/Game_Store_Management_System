package game_store.Frontend.CreateAccount;

import game_store.Frontend.Login.LOGIN;
import game_store.backend.services.CreateAccountService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CreateAccount extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createAccountButton;
    private JButton backToLoginButton;

    private CreateAccountService createAccountService = new CreateAccountService(); // Create service instance

    public CreateAccount() {
        setTitle("Create Account");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 450);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add UI components
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        usernameField = new JTextField(); gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(); gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(); gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Confirm Password:"), gbc);
        confirmPasswordField = new JPasswordField(); gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        createAccountButton = new JButton("Create Account");
        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(createAccountButton, gbc);

        backToLoginButton = new JButton("Back to Login");
        gbc.gridy = 5;
        panel.add(backToLoginButton, gbc);

        // Button actions
        createAccountButton.addActionListener(e -> validateForm());
        backToLoginButton.addActionListener(e -> {
            dispose(); // Close CreateAccount window
            new LOGIN().setVisible(true); // Open login page
        });

        add(panel);
        setVisible(true);
    }

    private void validateForm() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation: exactly one @ and only one . after @
        String[] emailParts = email.split("@");
        if (emailParts.length != 2 || emailParts[1].split("\\.").length != 2) {
            JOptionPane.showMessageDialog(this, "Invalid email format. Use format like user@example.com", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.matches("^(?=.*\\d).{8,}$")) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long and include a digit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create the account in the database
        try {
            createAccountService.createUser(username, email, password);
            JOptionPane.showMessageDialog(this, "Account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close the account creation window
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating account. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateAccount::new);
    }
}
