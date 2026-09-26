package internetbilling.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Payment entity representing simulated transaction records.
 */
public class Payment {
    private String paymentId;
    private String billId;
    private String customerId;
    private Timestamp paymentDate;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentReference;
    private String paymentStatus;

    // Display helpers
    private String customerName;
    private String billingMonth;

    public Payment() {
        this.amount = BigDecimal.ZERO;
        this.paymentStatus = "SUCCESS";
    }

    public Payment(String paymentId, String billId, String customerId, Timestamp paymentDate,
                   BigDecimal amount, String paymentMethod, String paymentReference, String paymentStatus) {
        this.paymentId = paymentId;
        this.billId = billId;
        this.customerId = customerId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentReference = paymentReference;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }
}
