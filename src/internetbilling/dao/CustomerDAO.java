package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Customer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Customers table.
 */
public class CustomerDAO {

    /**
     * Retrieves all customers with joined plan name.
     */
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.name, c.address, c.phone, c.email, c.username, " +
                     "c.password, c.plan_id, c.connection_date, c.status, p.plan_name " +
                     "FROM Customers c LEFT JOIN Plans p ON c.plan_id = p.plan_id " +
                     "ORDER BY c.customer_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    /**
     * Retrieves customer by customer ID.
     */
    public Customer getCustomerById(String customerId) throws SQLException {
        String sql = "SELECT c.customer_id, c.name, c.address, c.phone, c.email, c.username, " +
                     "c.password, c.plan_id, c.connection_date, c.status, p.plan_name " +
                     "FROM Customers c LEFT JOIN Plans p ON c.plan_id = p.plan_id " +
                     "WHERE c.customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }

    /**
     * Validates customer login using username and password.
     */
    public Customer validateCustomerLogin(String username, String password) throws SQLException {
        String sql = "SELECT c.customer_id, c.name, c.address, c.phone, c.email, c.username, " +
                     "c.password, c.plan_id, c.connection_date, c.status, p.plan_name " +
                     "FROM Customers c LEFT JOIN Plans p ON c.plan_id = p.plan_id " +
                     "WHERE c.username = ? AND c.password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds a new customer.
     */
    public boolean addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customers (customer_id, name, address, phone, email, username, " +
                     "password, plan_id, connection_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getCustomerId().trim());
            ps.setString(2, customer.getName().trim());
            ps.setString(3, customer.getAddress().trim());
            ps.setString(4, customer.getPhone().trim());
            ps.setString(5, customer.getEmail().trim());
            ps.setString(6, customer.getUsername().trim());
            ps.setString(7, customer.getPassword().trim());
            ps.setString(8, customer.getPlanId());
            ps.setDate(9, customer.getConnectionDate());
            ps.setString(10, customer.getStatus());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing customer.
     */
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE Customers SET name = ?, address = ?, phone = ?, email = ?, " +
                     "username = ?, password = ?, plan_id = ?, connection_date = ?, status = ? " +
                     "WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getName().trim());
            ps.setString(2, customer.getAddress().trim());
            ps.setString(3, customer.getPhone().trim());
            ps.setString(4, customer.getEmail().trim());
            ps.setString(5, customer.getUsername().trim());
            ps.setString(6, customer.getPassword().trim());
            ps.setString(7, customer.getPlanId());
            ps.setDate(8, customer.getConnectionDate());
            ps.setString(9, customer.getStatus());
            ps.setString(10, customer.getCustomerId().trim());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a customer.
     */
    public boolean deleteCustomer(String customerId) throws SQLException {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Searches customers by ID, name, phone, email, or username.
     */
    public List<Customer> searchCustomers(String keyword) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.customer_id, c.name, c.address, c.phone, c.email, c.username, " +
                     "c.password, c.plan_id, c.connection_date, c.status, p.plan_name " +
                     "FROM Customers c LEFT JOIN Plans p ON c.plan_id = p.plan_id " +
                     "WHERE c.customer_id LIKE ? OR c.name LIKE ? OR c.phone LIKE ? OR c.email LIKE ? OR c.username LIKE ? " +
                     "ORDER BY c.customer_id";
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
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        return customers;
    }

    /**
     * Checks if customer ID already exists.
     */
    public boolean isCustomerIdExists(String customerId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customers WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerId.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Checks if username is already taken by another customer.
     */
    public boolean isUsernameExists(String username, String excludeCustomerId) throws SQLException {
        boolean hasExclude = excludeCustomerId != null && !excludeCustomerId.trim().isEmpty();
        String sql = hasExclude
                ? "SELECT COUNT(*) FROM Customers WHERE username = ? AND customer_id <> ?"
                : "SELECT COUNT(*) FROM Customers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            if (hasExclude) {
                ps.setString(2, excludeCustomerId.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Gets total customer count.
     */
    public int getTotalCustomersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Gets active customer count.
     */
    public int getActiveCustomersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customers WHERE status = 'ACTIVE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer c = new Customer(
            rs.getString("customer_id"),
            rs.getString("name"),
            rs.getString("address"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("plan_id"),
            rs.getDate("connection_date"),
            rs.getString("status")
        );
        c.setPlanName(rs.getString("plan_name"));
        return c;
    }
}
