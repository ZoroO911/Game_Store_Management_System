package game_store.Frontend.dashboard;

import game_store.backend.services.GameService;
import game_store.backend.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class GamesListPage extends JFrame {
    private JTable gamesTable;
    private DefaultTableModel tableModel;
    private int userId;

    public GamesListPage(int userId) {
        this.userId = userId;
        setTitle("Game List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Game ID", "Game Title", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        gamesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(gamesTable);

        add(scrollPane, BorderLayout.CENTER);

        // Panel to hold both buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Buy Button
        JButton buyButton = new JButton("Buy Selected Game");
        buyButton.addActionListener(new BuyButtonListener());
        buttonPanel.add(buyButton);

        // Back to Dashboard Button
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            DashboardPage dashboard = new DashboardPage(userId);
            dashboard.setVisible(true);
            dispose(); // Close the current page
        });
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        fetchGames();
    }

    private void fetchGames() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT GameID, Title, Price FROM Games";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);

            while (rs.next()) {
                int gameId = rs.getInt("GameID");
                String title = rs.getString("Title");
                double price = rs.getDouble("Price");
                tableModel.addRow(new Object[]{gameId, title, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching games.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class BuyButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int selectedRow = gamesTable.getSelectedRow();
            if (selectedRow != -1) {
                int gameId = (int) tableModel.getValueAt(selectedRow, 0);
                String title = (String) tableModel.getValueAt(selectedRow, 1);
                double price = (double) tableModel.getValueAt(selectedRow, 2);

                int confirm = JOptionPane.showConfirmDialog(null, "Buy " + title + " for ₹" + price + "?", "Confirm Purchase", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    GameService service = new GameService();
                    boolean success = service.buyGame(userId, gameId);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "Purchase successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Purchase failed. Not enough balance or already owned.", "Failed", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select a game to buy.");
            }
        }
    }

    public static void main(String[] args) {
        int userId = 101; // Replace with actual user ID
        SwingUtilities.invokeLater(() -> new GamesListPage(userId).setVisible(true));
    }
}
