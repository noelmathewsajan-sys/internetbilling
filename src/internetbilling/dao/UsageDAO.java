package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Usage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Usage table.
 */
public class UsageDAO {

    /**
     * Retrieves all usage records with customer name and plan data limit.
     */
    public List<Usage> getAllUsage() throws SQLException {
        List<Usage> list = new ArrayList<>();
        String sql = "SELECT u.usage_id, u.customer_id, u.billing_month, u.data_used, u.extra_data, " +
                "c.name AS customer_name, p.data_limit " +
                "FROM " + DBConnection.getUsageTable() + " u " +
                "JOIN Customers c ON u.customer_id = c.customer_id " +
                "JOIN Plans p ON c.plan_id = p.plan_id " +
                "ORDER BY u.billing_month DESC, u.customer_id";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUsage(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves usage by usage ID.
     */
    public Usage getUsageById(int usageId) throws SQLException {
        String sql = "SELECT u.usage_id, u.customer_id, u.billing_month, u.data_used, u.extra_data, " +
                "c.name AS customer_name, p.data_limit " +
                "FROM " + DBConnection.getUsageTable() + " u " +
                "JOIN Customers c ON u.customer_id = c.customer_id " +
                "JOIN Plans p ON c.plan_id = p.plan_id " +
                "WHERE u.usage_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsage(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves usage by customer ID and billing month.
     */
    public Usage getUsageByCustomerAndMonth(String customerId, String billingMonth) throws SQLException {
        String sql = "SELECT u.usage_id, u.customer_id, u.billing_month, u.data_used, u.extra_data, " +
                "c.name AS customer_name, p.data_limit " +
                "FROM " + DBConnection.getUsageTable() + " u " +
                "JOIN Customers c ON u.customer_id = c.customer_id " +
                "JOIN Plans p ON c.plan_id = p.plan_id " +
                "WHERE u.customer_id = ? AND u.billing_month = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            ps.setString(2, billingMonth.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsage(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves all usage history for a customer.
     */
    public List<Usage> getUsageByCustomer(String customerId) throws SQLException {
        List<Usage> list = new ArrayList<>();
        String sql = "SELECT u.usage_id, u.customer_id, u.billing_month, u.data_used, u.extra_data, " +
                "c.name AS customer_name, p.data_limit " +
                "FROM " + DBConnection.getUsageTable() + " u " +
                "JOIN Customers c ON u.customer_id = c.customer_id " +
                "JOIN Plans p ON c.plan_id = p.plan_id " +
                "WHERE u.customer_id = ? " +
                "ORDER BY u.billing_month DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUsage(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if usage already exists for customer + month.
     */
    public boolean isUsageExists(String customerId, String billingMonth) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + DBConnection.getUsageTable() + " WHERE customer_id = ? AND billing_month = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            ps.setString(2, billingMonth.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Adds a usage record and returns the generated usage ID.
     */
    public int addUsage(Usage usage) throws SQLException {
        String sql = "INSERT INTO " + DBConnection.getUsageTable() + " (customer_id, billing_month, data_used, extra_data) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usage.getCustomerId().trim());
            ps.setString(2, usage.getBillingMonth().trim());
            ps.setBigDecimal(3, usage.getDataUsed());
            ps.setBigDecimal(4, usage.getExtraData());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Updates an existing usage record.
     */
    public boolean updateUsage(Usage usage) throws SQLException {
        String sql = "UPDATE " + DBConnection.getUsageTable() + " SET data_used = ?, extra_data = ? WHERE customer_id = ? AND billing_month = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, usage.getDataUsed());
            ps.setBigDecimal(2, usage.getExtraData());
            ps.setString(3, usage.getCustomerId().trim());
            ps.setString(4, usage.getBillingMonth().trim());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches usage records by customer ID, customer name, or month.
     */
    public List<Usage> searchUsage(String keyword) throws SQLException {
        List<Usage> list = new ArrayList<>();
        String sql = "SELECT u.usage_id, u.customer_id, u.billing_month, u.data_used, u.extra_data, " +
                "c.name AS customer_name, p.data_limit " +
                "FROM " + DBConnection.getUsageTable() + " u " +
                "JOIN Customers c ON u.customer_id = c.customer_id " +
                "JOIN Plans p ON c.plan_id = p.plan_id " +
                "WHERE u.customer_id LIKE ? OR c.name LIKE ? OR u.billing_month LIKE ? " +
                "ORDER BY u.billing_month DESC, u.customer_id";
        String pattern = "%" + keyword.trim() + "%";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUsage(rs));
                }
            }
        }
        return list;
    }

    private Usage mapResultSetToUsage(ResultSet rs) throws SQLException {
        Usage u = new Usage(
                rs.getInt("usage_id"),
                rs.getString("customer_id"),
                rs.getString("billing_month"),
                rs.getBigDecimal("data_used"),
                rs.getBigDecimal("extra_data"));
        u.setCustomerName(rs.getString("customer_name"));
        u.setDataLimit(rs.getBigDecimal("data_limit"));
        return u;
    }
}
