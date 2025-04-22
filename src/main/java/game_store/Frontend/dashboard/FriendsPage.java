package game_store.Frontend.dashboard;

import game_store.backend.services.FriendService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
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
        setSize(750, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 123, 231));
        JLabel headerLabel = new JLabel("Your Friend List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table setup
        tableModel.addColumn("Friend's Username");
        tableModel.addColumn("Friendship Date");
        tableModel.addColumn("Action");

        friendTable.setFont(new Font("Arial", Font.PLAIN, 14));
        friendTable.setRowHeight(30);
        friendTable.setBackground(new Color(245, 245, 245));
        friendTable.setForeground(new Color(55, 123, 231));

        JScrollPane tableScrollPane = new JScrollPane(friendTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Add custom renderer and editor for remove button
        friendTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        friendTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Input Panel with Add + Back buttons
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Friend Username:"));

        addFriendField.setPreferredSize(new Dimension(150, 25));
        inputPanel.add(addFriendField);

        addButton.setBackground(new Color(55, 123, 231));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setPreferredSize(new Dimension(130, 30));
        inputPanel.add(addButton);

        backButton.setBackground(new Color(255, 0, 0));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(170, 30));
        inputPanel.add(backButton);

        add(inputPanel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> {
            addFriend();
            loadFriends();
        });

        backButton.addActionListener(e -> {
            this.dispose();
            new DashboardPage(userId).setVisible(true);
        });

        loadFriends();
    }

    private void loadFriends() {
        tableModel.setRowCount(0);
        List<String[]> friends = friendService.getFriends(userId);
        for (String[] friend : friends) {
            tableModel.addRow(new Object[]{friend[0], friend[1], "Remove"});
        }
    }

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

    // ButtonRenderer for JTable
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(new Color(255, 51, 51));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ButtonEditor for JTable
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button = new JButton("Remove");
        private String username;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button.setBackground(new Color(255, 51, 51));
            button.setForeground(Color.WHITE);
            button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            username = (String) table.getValueAt(row, 0);
            button.setText("Remove");
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to remove " + username + "?",
                        "Confirm Remove", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    friendService.removeFriend(userId, username);
                    loadFriends();
                }
            }
            clicked = false;
            return "Remove";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FriendsPage(101).setVisible(true));
    }
}
