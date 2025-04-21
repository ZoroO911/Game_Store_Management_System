package game_store.Frontend.dashboard;

import game_store.backend.services.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WalletPage extends JFrame {
    private final int userId;
    private final UserService userService = new UserService();
    private final JLabel walletBalanceLabel = new JLabel("₹0.00");
    private final JTextField topUpAmountField = new JTextField(15);
    private final JButton topUpButton = new JButton("Top Up");
    private final JButton backButton = new JButton("Back to Dashboard");

    public WalletPage(int userId) {
        this.userId = userId;

        setTitle("Wallet");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Light background for the page

        // Header Panel for Page Title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 123, 231)); // Light blue color
        JLabel headerLabel = new JLabel("Your Wallet", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(255, 255, 255));  // White background for content area
        contentPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Wallet Balance Display
        JPanel balancePanel = new JPanel();
        balancePanel.setBackground(new Color(255, 255, 255));
        balancePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JLabel balanceLabel = new JLabel("Current Balance: ");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        balanceLabel.setForeground(new Color(55, 123, 231)); // Blue color for the balance text
        balancePanel.add(balanceLabel);

        walletBalanceLabel.setFont(new Font("Arial", Font.BOLD, 24));
        walletBalanceLabel.setForeground(new Color(0, 204, 0)); // Green color for the balance value
        balancePanel.add(walletBalanceLabel);

        contentPanel.add(balancePanel);

        // Top Up Section
        JPanel topUpPanel = new JPanel();
        topUpPanel.setBackground(new Color(255, 255, 255));
        topUpPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JLabel topUpLabel = new JLabel("Top Up Amount: ");
        topUpLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topUpPanel.add(topUpLabel);

        topUpAmountField.setPreferredSize(new Dimension(150, 30));
        topUpPanel.add(topUpAmountField);

        topUpButton.setBackground(new Color(55, 123, 231)); // Blue button
        topUpButton.setForeground(Color.WHITE);  // White text on blue button
        topUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        topUpButton.setPreferredSize(new Dimension(120, 30));
        topUpPanel.add(topUpButton);

        // Add action listener to the Top Up button
        topUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                topUpWallet();
            }
        });

        contentPanel.add(topUpPanel);

        // Back Button Section
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBackground(new Color(255, 255, 255));
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        backButton.setBackground(new Color(255, 0, 0)); // Red color for back button
        backButton.setForeground(Color.WHITE);  // White text on red button
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(150, 30));
        backButtonPanel.add(backButton);

        // Add action listener to the Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToDashboard();
            }
        });

        contentPanel.add(backButtonPanel);

        add(contentPanel, BorderLayout.CENTER);
        loadWalletBalance();
    }

    // Load the current wallet balance
    private void loadWalletBalance() {
        double balance = userService.getWalletBalance(userId);
        walletBalanceLabel.setText("₹" + String.format("%.2f", balance));
    }

    // Handle Top Up logic
    private void topUpWallet() {
        try {
            double amount = Double.parseDouble(topUpAmountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount greater than 0.");
                return;
            }

            boolean success = userService.topUpWallet(userId, amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "Wallet topped up successfully!");
                loadWalletBalance(); // Refresh the balance
                topUpAmountField.setText(""); // Clear the input field
            } else {
                JOptionPane.showMessageDialog(this, "Failed to top up the wallet. Please try again.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    // Go back to the DashboardPage
    private void goBackToDashboard() {
        this.dispose(); // Close the current window
        new DashboardPage(userId).setVisible(true); // Open the DashboardPage
    }

    public static void main(String[] args) {
        // Use an actual userId when calling
        SwingUtilities.invokeLater(() -> new WalletPage(101).setVisible(true));
    }
}
