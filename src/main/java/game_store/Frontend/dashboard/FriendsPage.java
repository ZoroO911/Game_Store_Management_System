package game_store.Frontend.dashboard;

import game_store.backend.services.FriendService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FriendsPage extends JFrame {
    private final int userId;
    private final FriendService friendService = new FriendService();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable friendTable = new JTable(tableModel);
    private final JTextField addFriendField = new JTextField(15);
    private final JButton addButton = new JButton("Add Friend");
    private final JButton backButton = new JButton("Back to Dashboard");

    public FriendsPage(int userId) {
        this.userId = userId;

        setTitle("Your Friends");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));  // Light background color for better UI

        // Header Panel for Page Title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 123, 231)); // Light blue color
        JLabel headerLabel = new JLabel("Your Friend List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Initialize table model
        tableModel.addColumn("Friend's Username");
        tableModel.addColumn("Friendship Date");

        // Friend Table Panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBackground(new Color(255, 255, 255));

        // JTable to display friends and their Friendship Date
        friendTable.setModel(tableModel);
        friendTable.setFont(new Font("Arial", Font.PLAIN, 14));
        friendTable.setBackground(new Color(245, 245, 245));  // Light background for the table
        friendTable.setForeground(new Color(55, 123, 231)); // Blue text color for friends
        friendTable.setRowHeight(30);
        JScrollPane tableScrollPane = new JScrollPane(friendTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Input Panel to add new friends
        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(new Color(255, 255, 255)); // White background for the input area
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel addFriendLabel = new JLabel("Add Friend (Username):");
        addFriendLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(addFriendLabel);

        addFriendField.setPreferredSize(new Dimension(150, 25));
        inputPanel.add(addFriendField);

        addButton.setBackground(new Color(55, 123, 231)); // Blue button
        addButton.setForeground(Color.WHITE);  // White text on blue button
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(130, 30));
        inputPanel.add(addButton);

        // Back Button Section
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setBackground(new Color(255, 255, 255));  // White background
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        backButton.setBackground(new Color(255, 0, 0));  // Red back button
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(130, 30));
        backButtonPanel.add(backButton);

        // Action listeners
        addButton.addActionListener(e -> {
            addFriend();
            loadFriends();  // Refresh the list after adding a friend
        });

        backButton.addActionListener(e -> {
            this.dispose();  // Close the current window
            new DashboardPage(userId).setVisible(true);  // Open the DashboardPage
        });

        add(backButtonPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.SOUTH);
        add(tablePanel, BorderLayout.CENTER);

        loadFriends();  // Load friends on startup
    }

    // Load and display the list of friends with their friendship date
    private void loadFriends() {
        tableModel.setRowCount(0);  // Clear the table

        List<String[]> friends = friendService.getFriends(userId);

        for (String[] friend : friends) {
            // Add each friend's username and their friendship date to the table
            tableModel.addRow(friend);
        }
    }

    // Add a new friend based on the username input
    private void addFriend() {
        String friendUsername = addFriendField.getText().trim();
        if (friendUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a username.");
            return;
        }

        boolean added = friendService.addFriend(userId, friendUsername);
        if (added) {
            JOptionPane.showMessageDialog(this, "Friend added successfully!");
            loadFriends();
            addFriendField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Could not add friend. Check username or already added.");
        }
    }

    public static void main(String[] args) {
        // Use an actual userId when calling
        SwingUtilities.invokeLater(() -> new FriendsPage(101).setVisible(true));
    }
}
