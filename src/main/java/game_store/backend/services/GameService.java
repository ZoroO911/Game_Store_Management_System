package game_store.backend.services;

import game_store.backend.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GameService {
    // Method to get all games
    public List<Game> getAllGames() {
        List<Game> games = new ArrayList<>();
        String query = "SELECT g.GameID, g.Title, g.Price, g.ReleaseDate, ge.GenreName FROM Games g "
                + "JOIN Genres ge ON g.GenreID = ge.GenreID";

        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int gameID = rs.getInt("GameID");
                String title = rs.getString("Title");
                double price = rs.getDouble("Price");
                Date releaseDate = rs.getDate("ReleaseDate");
                String genreName = rs.getString("GenreName");
                games.add(new Game(gameID, title, genreName, price, releaseDate));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Consider a logging framework for production
        }
        return games;
    }

    // Method to buy a game
    public boolean buyGame(int userId, int gameId) {
        String getWalletQuery = "SELECT WalletBalance FROM Users WHERE UserID = ?";
        String getPriceQuery = "SELECT Price FROM Games WHERE GameID = ?";
        String updateWalletQuery = "UPDATE Users SET WalletBalance = WalletBalance - ? WHERE UserID = ?";
        String insertLibraryQuery = "INSERT INTO GLibrary (UserID, GameID, PurchaseDate) VALUES (?, ?, CURRENT_DATE)";

        Connection conn = null; // Declare the connection variable

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            double walletBalance = fetchWalletBalance(conn, getWalletQuery, userId);
            if (walletBalance < 0) return false; // User not found or error fetching balance

            double gamePrice = fetchGamePrice(conn, getPriceQuery, gameId);
            if (gamePrice < 0) return false; // Game not found or error fetching price

            if (walletBalance < gamePrice) return false; // Not enough balance

            // Update wallet balance
            try (PreparedStatement ps1 = conn.prepareStatement(updateWalletQuery)) {
                ps1.setDouble(1, gamePrice);
                ps1.setInt(2, userId);
                ps1.executeUpdate();
            }

            // Add game to the user's library
            try (PreparedStatement ps2 = conn.prepareStatement(insertLibraryQuery)) {
                ps2.setInt(1, userId);
                ps2.setInt(2, gameId);
                ps2.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace(); // Log exception
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log rollback exception
            }
            return false;
        } finally {
            // Close the connection if it was opened
            if (conn != null) {
                try {
                    if (!conn.getAutoCommit()) {
                        // Reset auto-commit if it was previously set
                        conn.setAutoCommit(true);
                    }
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Log error on connection close
                }
            }
        }
    }

    private double fetchWalletBalance(Connection conn, String query, int userId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("WalletBalance");
                }
                return -1; // User not found
            }
        }
    }

    private double fetchGamePrice(Connection conn, String query, int gameId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, gameId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Price");
                }
                return -1; // Game not found
            }
        }
    }
}
