
package internetbilling.model;

import java.sql.Date;

/**
 * Customer entity representing internet subscriber accounts.
 */
public class Customer {
    private String customerId;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String planId;
    private Date connectionDate;
    private String status; // 'ACTIVE', 'INACTIVE', 'SUSPENDED'

    // Additional display field from Plan join
    private String planName;

    public Customer() {
        this.status = "ACTIVE";
    }

    public Customer(String customerId, String name, String address, String phone, String email,
                    String username, String password, String planId, Date connectionDate, String status) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.planId = planId;
        this.connectionDate = connectionDate;
        this.status = status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Date getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(Date connectionDate) {
        this.connectionDate = connectionDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    @Override
    public String toString() {
        return customerId + " - " + name;
    }
}
