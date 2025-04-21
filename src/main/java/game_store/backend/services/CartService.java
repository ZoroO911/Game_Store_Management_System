package game_store.backend.services;

import game_store.backend.database.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartService {

    public List<String[]> getUserPurchasedGames(int userId) {
        List<String[]> games = new ArrayList<>();

        String query = "SELECT g.Title, g.developer, g.releaseDate FROM GLibrary i " +
                "JOIN Games g ON i.GameID = g.GameID WHERE i.UserID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] game = new String[3];
                game[0] = rs.getString("Title");
                game[1] = rs.getString("developer");
                game[2] = rs.getString("releaseDate");
                games.add(game);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return games;
    }
}
