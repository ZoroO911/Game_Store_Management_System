package game_store.backend.services;

import game_store.backend.database.DBConnection;
import java.sql.*;

public class CreateAccountService {
    public boolean createAccount(String username, String email, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO Users (Username, Email, Password, WalletBalance) VALUES (?, ?, ?, 0)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;  // If a row was inserted, account creation was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Implement the createUser method properly, calling createAccount
    public boolean createUser(String username, String email, String password) throws SQLException{
        return createAccount(username, email, password); // Call the createAccount method
    }
}
