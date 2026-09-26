package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Payments table.
 */
public class PaymentDAO {

    private static final String BASE_SELECT =
        "SELECT p.payment_id, p.bill_id, p.customer_id, p.payment_date, p.amount, " +
        "p.payment_method, p.payment_reference, p.payment_status, " +
        "c.name AS customer_name, b.billing_month " +
        "FROM Payments p " +
        "JOIN Customers c ON p.customer_id = c.customer_id " +
        "JOIN Bills b ON p.bill_id = b.bill_id ";

    /**
     * Retrieves all payments ordered by date descending.
     */
    public List<Payment> getAllPayments() throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY p.payment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToPayment(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves payments for a specific customer.
     */
    public List<Payment> getPaymentsByCustomer(String customerId) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SELECT + "WHERE p.customer_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves payment by payment ID.
     */
    public Payment getPaymentById(String paymentId) throws SQLException {
        String sql = BASE_SELECT + "WHERE p.payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        return null;
    }

    /**
     * Records a new payment transaction.
     */
    public boolean addPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payments (payment_id, bill_id, customer_id, payment_date, amount, " +
                     "payment_method, payment_reference, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payment.getPaymentId().trim());
            ps.setString(2, payment.getBillId().trim());
            ps.setString(3, payment.getCustomerId().trim());
            ps.setTimestamp(4, payment.getPaymentDate() != null ? payment.getPaymentDate() : new Timestamp(System.currentTimeMillis()));
            ps.setBigDecimal(5, payment.getAmount());
            ps.setString(6, payment.getPaymentMethod().trim());
            ps.setString(7, payment.getPaymentReference().trim());
            ps.setString(8, payment.getPaymentStatus() != null ? payment.getPaymentStatus() : "SUCCESS");
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches payments by payment ID, bill ID, customer ID, customer name, method, or ref.
     */
    public List<Payment> searchPayments(String keyword) throws SQLException {
        List<Payment> list = new ArrayList<>();
        String sql = BASE_SELECT +
                     "WHERE p.payment_id LIKE ? OR p.bill_id LIKE ? OR p.customer_id LIKE ? " +
                     "OR c.name LIKE ? OR p.payment_method LIKE ? OR p.payment_reference LIKE ? " +
                     "ORDER BY p.payment_date DESC";
        String pattern = "%" + keyword.trim() + "%";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                ps.setString(i, pattern);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToPayment(rs));
                }
            }
        }
        return list;
    }

    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment(
            rs.getString("payment_id"),
            rs.getString("bill_id"),
            rs.getString("customer_id"),
            rs.getTimestamp("payment_date"),
            rs.getBigDecimal("amount"),
            rs.getString("payment_method"),
            rs.getString("payment_reference"),
            rs.getString("payment_status")
        );
        p.setCustomerName(rs.getString("customer_name"));
        p.setBillingMonth(rs.getString("billing_month"));
        return p;
    }
}
