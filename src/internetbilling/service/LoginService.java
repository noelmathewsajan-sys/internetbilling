package internetbilling.service;

import internetbilling.dao.AdminDAO;
import internetbilling.dao.CustomerDAO;
import internetbilling.model.Admin;
import internetbilling.model.Customer;
import java.sql.SQLException;

/**
 * Service handling authentication for both Administrator and Customer users.
 */
public class LoginService {

    private final AdminDAO adminDAO;
    private final CustomerDAO customerDAO;

    public LoginService() {
        this.adminDAO = new AdminDAO();
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Authenticates an administrator.
     * @param username admin username
     * @param password admin password
     * @return Admin object if authenticated, null otherwise
     * @throws SQLException on database error
     */
    public Admin authenticateAdmin(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return adminDAO.validateAdmin(username.trim(), password.trim());
    }

    /**
     * Authenticates a customer subscriber.
     * @param username customer username
     * @param password customer password
     * @return Customer object if authenticated, null otherwise
     * @throws SQLException on database error
     */
    public Customer authenticateCustomer(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        return customerDAO.validateCustomerLogin(username.trim(), password.trim());
    }
}
