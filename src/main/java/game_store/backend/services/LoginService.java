package game_store.backend.services;

import game_store.backend.database.DBConnection;
import java.sql.*;

public class LoginService {
    // Method to authenticate user based on username and password and return UserID
    public int authenticate(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT UserID FROM Users WHERE Username = ? AND Password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt("UserID"); // Return UserID if authentication is successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // If authentication fails or user not found
    }
}
