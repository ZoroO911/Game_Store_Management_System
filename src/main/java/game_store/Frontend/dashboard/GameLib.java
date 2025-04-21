package game_store.Frontend.dashboard;

import game_store.backend.services.CartService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GameLib extends JFrame {
    private final int userId;
    private final CartService cartService = new CartService();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable gameTable = new JTable(tableModel);

    public GameLib(int userId) {
        this.userId = userId;

        setTitle("My Library");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240)); // Light background

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(55, 123, 231));
        JLabel headerLabel = new JLabel("Your Purchased Games", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table Setup
        tableModel.addColumn("Title");
        tableModel.addColumn("Developer");
        tableModel.addColumn("Release Date");

        gameTable.setModel(tableModel);
        gameTable.setFont(new Font("Arial", Font.PLAIN, 14));
        gameTable.setRowHeight(30);
        gameTable.setBackground(new Color(245, 245, 245));
        gameTable.setForeground(new Color(55, 123, 231));

        JScrollPane scrollPane = new JScrollPane(gameTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load purchased games
        loadPurchasedGames();
    }

    private void loadPurchasedGames() {
        List<String[]> games = cartService.getUserPurchasedGames(userId);
        tableModel.setRowCount(0); // Clear previous data

        if (games.isEmpty()) {
            tableModel.addRow(new String[]{"No games found", "", ""});
        } else {
            for (String[] game : games) {
                tableModel.addRow(game);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameLib(101).setVisible(true));
    }
}
