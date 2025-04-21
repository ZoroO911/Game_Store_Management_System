package game_store.backend.services;

import game_store.backend.database.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class RefundService {
    public void requestRefund(Scanner scanner) {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.print("Transaction ID: ");
            int tid = scanner.nextInt();
            System.out.print("User ID: ");
            int uid = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Reason: ");
            String reason = scanner.nextLine();

            String q = "INSERT INTO RefundRequests (TransactionID, UserID, Reason, RequestDate, Status) VALUES (?, ?, ?, CURDATE(), 'Pending')";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, tid);
            ps.setInt(2, uid);
            ps.setString(3, reason);
            ps.executeUpdate();
            System.out.println("Refund Requested");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
