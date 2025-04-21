package game_store.backend.services;

import game_store.backend.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    // View all users
    public void viewUsers() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Users")) {
            while (rs.next()) {
                System.out.printf("ID: %d | Name: %s | Wallet: %.2f\n",
                        rs.getInt("UserID"), rs.getString("Username"), rs.getDouble("WalletBalance"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get wallet balance for a user
    public double getWalletBalance(int userId) {
        String query = "SELECT WalletBalance FROM Users WHERE UserID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("WalletBalance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // return -1 if failed
    }

    // Top up wallet balance
    public boolean topUpWallet(int userId, double amount) {
        String query = "UPDATE Users SET WalletBalance = WalletBalance + ? WHERE UserID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDouble(1, amount);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get the list of friends of the user
    public List<String> getFriends(int userId) {
        List<String> friends = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.Username FROM Friends f " +
                    "JOIN Users u ON (f.UserID1 = u.UserID OR f.UserID2 = u.UserID) " +
                    "WHERE (f.UserID1 = ? OR f.UserID2 = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Add each friend's username to the list
                String username = rs.getString("Username");
                // Exclude the logged-in user from the friend list
                if (!username.equals(getUsernameById(userId))) {
                    friends.add(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    // Add a new friend for the user
    public boolean addFriend(int userId, int friendId) {
        // Check if the user is already friends with the given friendId
        if (isAlreadyFriends(userId, friendId)) {
            return false; // Return false if they are already friends
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO Friends (UserID1, UserID2, FriendshipDate) VALUES (?, ?, NOW())";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to check if the user and friendId are already friends
    private boolean isAlreadyFriends(int userId, int friendId) {
        String query = "SELECT COUNT(*) FROM Friends WHERE " +
                "(UserID1 = ? AND UserID2 = ?) OR (UserID1 = ? AND UserID2 = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // If there is already a friendship
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to get username by userId (for exclusion in friend list)
    private String getUsernameById(int userId) {
        String query = "SELECT Username FROM Users WHERE UserID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}
