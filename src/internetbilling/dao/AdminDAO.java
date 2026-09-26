package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Admin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for Administrator accounts.
 */
public class AdminDAO {

    /**
     * Validates administrator login credentials.
     * @param username admin username
     * @param password admin password
     * @return Admin object if valid, null otherwise
     * @throws SQLException on database query failure
     */
    public Admin validateAdmin(String username, String password) throws SQLException {
        String sql = "SELECT admin_id, username, password FROM Admin WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Finds admin by username.
     */
    public Admin findByUsername(String username) throws SQLException {
        String sql = "SELECT admin_id, username, password FROM Admin WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("admin_id"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    /**
     * Updates admin password.
     */
    public boolean changePassword(int adminId, String newPassword) throws SQLException {
        String sql = "UPDATE Admin SET password = ? WHERE admin_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, adminId);
            return ps.executeUpdate() > 0;
        }
    }
}
