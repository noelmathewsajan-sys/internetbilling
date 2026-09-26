package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Bill;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Bills table.
 */
public class BillDAO {

    private static final String BASE_SELECT =
        "SELECT b.bill_id, b.customer_id, b.usage_id, b.billing_month, b.plan_tariff, " +
        "b.equipment_rental, b.extra_usage_charge, b.late_fine, b.total_amount, b.due_date, b.bill_status, " +
        "c.name AS customer_name, c.address AS customer_address, p.plan_name, p.speed_tier, " +
        "u.data_used, u.extra_data " +
        "FROM Bills b " +
        "JOIN Customers c ON b.customer_id = c.customer_id " +
        "JOIN Plans p ON c.plan_id = p.plan_id " +
        "LEFT JOIN " + DBConnection.getUsageTable() + " u ON b.usage_id = u.usage_id ";

    /**
     * Retrieves all bills ordered by billing month descending.
     */
    public List<Bill> getAllBills() throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY b.billing_month DESC, b.bill_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves bill by bill ID.
     */
    public Bill getBillById(String billId) throws SQLException {
        String sql = BASE_SELECT + "WHERE b.bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, billId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves bills for a specific customer.
     */
    public List<Bill> getBillsByCustomer(String customerId) throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.customer_id = ? ORDER BY b.billing_month DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBill(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves bills filtered by status (PAID, UNPAID, OVERDUE).
     */
    public List<Bill> getBillsByStatus(String status) throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE b.bill_status = ? ORDER BY b.billing_month DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBill(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves current/latest unpaid or overdue bill for customer.
     */
    public Bill getCurrentUnpaidBill(String customerId) throws SQLException {
        String sql = BASE_SELECT + "WHERE b.customer_id = ? AND b.bill_status IN ('UNPAID', 'OVERDUE') " +
                     "ORDER BY b.due_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        }
        return null;
    }

    /**
     * Checks if bill already exists for customer + month.
     */
    public boolean isBillExists(String customerId, String billingMonth) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills WHERE customer_id = ? AND billing_month = ?";
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
     * Checks if a customer has any unpaid bill prior to the current month that is overdue.
     */
    public boolean hasOverdueBill(String customerId, String currentMonth) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills WHERE customer_id = ? AND billing_month < ? AND bill_status IN ('UNPAID', 'OVERDUE')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            ps.setString(2, currentMonth.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Adds a new bill.
     */
    public boolean addBill(Bill bill) throws SQLException {
        String sql = "INSERT INTO Bills (bill_id, customer_id, usage_id, billing_month, plan_tariff, " +
                     "equipment_rental, extra_usage_charge, late_fine, total_amount, due_date, bill_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bill.getBillId().trim());
            ps.setString(2, bill.getCustomerId().trim());
            if (bill.getUsageId() != null && bill.getUsageId() > 0) {
                ps.setInt(3, bill.getUsageId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, bill.getBillingMonth().trim());
            ps.setBigDecimal(5, bill.getPlanTariff());
            ps.setBigDecimal(6, bill.getEquipmentRental());
            ps.setBigDecimal(7, bill.getExtraUsageCharge());
            ps.setBigDecimal(8, bill.getLateFine());
            ps.setBigDecimal(9, bill.getTotalAmount());
            ps.setDate(10, bill.getDueDate());
            ps.setString(11, bill.getBillStatus());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates bill payment status.
     */
    public boolean updateBillStatus(String billId, String status) throws SQLException {
        String sql = "UPDATE Bills SET bill_status = ? WHERE bill_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.trim());
            ps.setString(2, billId.trim());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches bills by bill ID, customer ID, customer name, or month.
     */
    public List<Bill> searchBills(String keyword) throws SQLException {
        List<Bill> list = new ArrayList<>();
        String sql = BASE_SELECT +
                     "WHERE b.bill_id LIKE ? OR b.customer_id LIKE ? OR c.name LIKE ? OR b.billing_month LIKE ? OR b.bill_status LIKE ? " +
                     "ORDER BY b.billing_month DESC, b.bill_id DESC";
        String pattern = "%" + keyword.trim() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBill(rs));
                }
            }
        }
        return list;
    }

    public int getTotalBillsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getPaidBillsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills WHERE bill_status = 'PAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getUnpaidBillsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills WHERE bill_status = 'UNPAID'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getOverdueBillsCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Bills WHERE bill_status = 'OVERDUE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public BigDecimal getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM Payments WHERE payment_status = 'SUCCESS'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal(1);
        }
        return BigDecimal.ZERO;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill b = new Bill(
            rs.getString("bill_id"),
            rs.getString("customer_id"),
            rs.getInt("usage_id"),
            rs.getString("billing_month"),
            rs.getBigDecimal("plan_tariff"),
            rs.getBigDecimal("equipment_rental"),
            rs.getBigDecimal("extra_usage_charge"),
            rs.getBigDecimal("late_fine"),
            rs.getBigDecimal("total_amount"),
            rs.getDate("due_date"),
            rs.getString("bill_status")
        );
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerAddress(rs.getString("customer_address"));
        b.setPlanName(rs.getString("plan_name"));
        b.setSpeedTier(rs.getString("speed_tier"));
        b.setDataUsed(rs.getBigDecimal("data_used"));
        b.setExtraData(rs.getBigDecimal("extra_data"));
        return b;
    }
}
