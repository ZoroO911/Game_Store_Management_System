package game_store.Frontend.Login;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import game_store.backend.services.LoginService;
import game_store.Frontend.dashboard.DashboardPage;

public class LOGIN extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LOGIN() {
        setTitle("Login Page");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window

        // Layout setup
        setLayout(new FlowLayout());

        // Username label and field
        add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        add(usernameField);

        // Password label and field
        add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        add(passwordField);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                // Authenticate using LoginService
                LoginService loginService = new LoginService();
                int userId = loginService.authenticate(username, password); // Authenticate and get UserID

                if (userId != -1) { // Valid user
                    new DashboardPage(userId).setVisible(true); // Pass userId to DashboardPage
                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect details. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginButton);

        // Create Account button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new game_store.Frontend.CreateAccount.CreateAccount().setVisible(true);
                dispose(); // Close login window
            }
        });
        add(createAccountButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LOGIN().setVisible(true);
            }
        });
    }
}
