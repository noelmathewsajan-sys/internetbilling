package internetbilling.service;

import internetbilling.dao.BillDAO;
import internetbilling.dao.PaymentDAO;
import internetbilling.model.Bill;
import internetbilling.model.Payment;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Service managing simulated online payment operations.
 */
public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final BillDAO billDAO;

    public PaymentService() {
        this.paymentDAO = new PaymentDAO();
        this.billDAO = new BillDAO();
    }

    /**
     * Processes simulated online payment for a customer's bill.
     *
     * @param billId Bill ID being paid
     * @param customerId Customer making payment
     * @param amount Payment amount (must equal or exceed bill total)
     * @param method Payment method (UPI, Debit Card, Credit Card, Net Banking)
     * @param reference Payment reference identifier
     * @return Payment transaction record
     * @throws IllegalArgumentException on validation error
     * @throws SQLException on database error
     */
    public Payment processPayment(String billId, String customerId, BigDecimal amount,
                                  String method, String reference) throws SQLException, IllegalArgumentException {

        if (billId == null || billId.trim().isEmpty()) {
            throw new IllegalArgumentException("Bill ID cannot be empty.");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        if (method == null || method.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a valid payment method.");
        }
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment reference / details cannot be empty.");
        }

        Bill bill = billDAO.getBillById(billId);
        if (bill == null) {
            throw new IllegalArgumentException("Bill not found: " + billId);
        }
        if ("PAID".equalsIgnoreCase(bill.getBillStatus())) {
            throw new IllegalArgumentException("Bill " + billId + " is already marked as PAID.");
        }

        // 1. Generate unique Payment ID: e.g., PAY-XXXXX
        String paymentId = "PAY-" + (10000 + new Random().nextInt(90000));

        // 2. Prepare payment record
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Payment payment = new Payment(
                paymentId,
                billId.trim(),
                customerId.trim(),
                now,
                amount,
                method.trim(),
                reference.trim(),
                "SUCCESS"
        );

        // 3. Save payment to database
        boolean paymentSaved = paymentDAO.addPayment(payment);
        if (!paymentSaved) {
            throw new SQLException("Failed to record payment in database.");
        }

        // 4. Update bill status to PAID
        boolean billUpdated = billDAO.updateBillStatus(billId, "PAID");
        if (!billUpdated) {
            throw new SQLException("Payment recorded but failed to update bill status to PAID.");
        }

        return payment;
    }
}
