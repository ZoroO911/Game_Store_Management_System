package game_store.backend.services;

import game_store.backend.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendService {

    // Get the list of friends and their friendship date for a user
    public List<String[]> getFriends(int userId) {
        List<String[]> friends = new ArrayList<>();
        String query = "SELECT u.Username, f.FriendshipDate " +
                "FROM Friends f " +
                "JOIN Users u ON (f.UserID1 = u.UserID OR f.UserID2 = u.UserID) " +
                "WHERE (f.UserID1 = ? OR f.UserID2 = ?) AND u.UserID != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("Username");
                Date friendshipDate = rs.getDate("FriendshipDate");
                friends.add(new String[]{username, friendshipDate.toString()});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }

    // Add a friend based on the username and return true if added
    public boolean addFriend(int userId, String friendUsername) {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if the friend exists
            String checkUserQuery = "SELECT UserID FROM Users WHERE Username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
            checkStmt.setString(1, friendUsername);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int friendId = rs.getInt("UserID");

                // Check if already friends
                String checkFriendQuery = "SELECT * FROM Friends WHERE (UserID1 = ? AND UserID2 = ?) OR (UserID1 = ? AND UserID2 = ?)";
                PreparedStatement checkFriendStmt = conn.prepareStatement(checkFriendQuery);
                checkFriendStmt.setInt(1, userId);
                checkFriendStmt.setInt(2, friendId);
                checkFriendStmt.setInt(3, friendId);
                checkFriendStmt.setInt(4, userId);
                ResultSet friendRs = checkFriendStmt.executeQuery();

                if (!friendRs.next()) {
                    // Add as friends
                    String addFriendQuery = "INSERT INTO Friends (UserID1, UserID2, FriendshipDate) VALUES (?, ?, ?)";
                    PreparedStatement addFriendStmt = conn.prepareStatement(addFriendQuery);
                    addFriendStmt.setInt(1, userId);
                    addFriendStmt.setInt(2, friendId);
                    addFriendStmt.setDate(3, new Date(System.currentTimeMillis())); // Today's date
                    addFriendStmt.executeUpdate();

                    return true;  // Successfully added as friends
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
