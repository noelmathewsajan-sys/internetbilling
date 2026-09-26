package internetbilling.test;

import internetbilling.dao.AdminDAO;
import internetbilling.dao.BillDAO;
import internetbilling.dao.CustomerDAO;
import internetbilling.dao.PaymentDAO;
import internetbilling.dao.PlanDAO;
import internetbilling.dao.UsageDAO;
import internetbilling.database.DBConnection;
import internetbilling.model.Admin;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Payment;
import internetbilling.model.Plan;
import internetbilling.model.Usage;
import internetbilling.service.BillingService;
import internetbilling.service.LoginService;
import internetbilling.service.PaymentService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Test suite to verify:
 * 1. Database Connection
 * 2. Admin Authentication
 * 3. Customer (User) Creation / Registration by Admin
 * 4. Customer Authentication (User Login)
 * 5. Monthly Usage Recording
 * 6. Automated Bill Calculation & Generation
 * 7. Online Payment Processing
 * 8. Clean Teardown
 */
public class TestAdminUserEntry {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" INTERNET BILLING MANAGEMENT SYSTEM - TEST SUITE");
        System.out.println(" Test Case: Admin & User Entry, Authentication, Billing & Payment Flow");
        System.out.println("================================================================================");

        final String testCustId = "TEST_CUST_99";
        final String testUsername = "testuser99";
        final String testPassword = "testPassword123";
        final String testMonth = "2026-11";

        CustomerDAO customerDAO = new CustomerDAO();
        UsageDAO usageDAO = new UsageDAO();
        BillDAO billDAO = new BillDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        LoginService loginService = new LoginService();
        BillingService billingService = new BillingService();
        PaymentService paymentService = new PaymentService();

        // Ensure clean state before running
        cleanupTestData(testCustId);

        try {
            // TEST 1: Database Connectivity
            testDatabaseConnection();

            // TEST 2: Admin Login
            testAdminLogin(loginService);

            // TEST 3: Admin User Entry (Register new Customer)
            testCustomerEntry(customerDAO, testCustId, testUsername, testPassword);

            // TEST 4: Customer (User) Login with new credentials
            testCustomerLogin(loginService, testUsername, testPassword);

            // TEST 5: Monthly Usage Entry
            testUsageEntry(usageDAO, testCustId, testMonth);

            // TEST 6: Automatic Bill Generation
            Bill generatedBill = testBillGeneration(billingService, testCustId, testMonth);

            // TEST 7: Payment Simulation
            if (generatedBill != null) {
                testPaymentProcessing(paymentService, billDAO, generatedBill.getBillId(), testCustId, generatedBill.getTotalAmount());
            }

        } catch (Exception e) {
            System.err.println("[FAIL] Unexpected error occurred during test execution: " + e.getMessage());
            e.printStackTrace();
            testsFailed++;
        } finally {
            // Teardown
            cleanupTestData(testCustId);
            System.out.println("================================================================================");
            System.out.println(" TEST SUMMARY: " + testsPassed + " Passed, " + testsFailed + " Failed.");
            if (testsFailed == 0) {
                System.out.println(" RESULT: ALL TESTS PASSED SUCCESSFULLY! (100% OK)");
            } else {
                System.out.println(" RESULT: SOME TESTS FAILED. Please inspect logs above.");
            }
            System.out.println("================================================================================");
        }
    }

    private static void testDatabaseConnection() {
        System.out.print("[TEST 1] Testing Database Connection... ");
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("PASS (Connected via " + DBConnection.getDbEngine() + ")");
                testsPassed++;
            } else {
                System.out.println("FAIL (Connection is null or closed)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static void testAdminLogin(LoginService loginService) {
        System.out.print("[TEST 2] Testing Admin Login (admin / admin123)... ");
        try {
            Admin admin = loginService.authenticateAdmin("admin", "admin123");
            if (admin != null && "admin".equalsIgnoreCase(admin.getUsername())) {
                System.out.println("PASS (Authenticated: " + admin.getUsername() + ", ID: " + admin.getAdminId() + ")");
                testsPassed++;
            } else {
                System.out.println("FAIL (Invalid credentials or admin not found)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static void testCustomerEntry(CustomerDAO customerDAO, String customerId, String username, String password) {
        System.out.print("[TEST 3] Testing Admin User Entry (Register Customer: " + customerId + ")... ");
        try {
            Customer customer = new Customer(
                    customerId,
                    "Automated Test User",
                    "999 Silicon Valley Boulevard",
                    "9876500099",
                    "testuser99@example.com",
                    username,
                    password,
                    "PLAN01",
                    Date.valueOf(LocalDate.now()),
                    "ACTIVE"
            );
            boolean added = customerDAO.addCustomer(customer);
            if (added && customerDAO.isCustomerIdExists(customerId)) {
                System.out.println("PASS (Customer successfully created in DB)");
                testsPassed++;
            } else {
                System.out.println("FAIL (Failed to insert customer)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static void testCustomerLogin(LoginService loginService, String username, String password) {
        System.out.print("[TEST 4] Testing Customer User Login (" + username + ")... ");
        try {
            Customer cust = loginService.authenticateCustomer(username, password);
            if (cust != null && username.equalsIgnoreCase(cust.getUsername())) {
                System.out.println("PASS (Logged in as: " + cust.getName() + " [" + cust.getCustomerId() + "])");
                testsPassed++;
            } else {
                System.out.println("FAIL (Authentication returned null)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static void testUsageEntry(UsageDAO usageDAO, String customerId, String billingMonth) {
        System.out.print("[TEST 5] Testing Monthly Usage Entry (" + billingMonth + ")... ");
        try {
            Usage usage = new Usage(0, customerId, billingMonth, new BigDecimal("125.50"), new BigDecimal("25.50"));
            int usageId = usageDAO.addUsage(usage);
            if (usageId > 0 || usageDAO.isUsageExists(customerId, billingMonth)) {
                System.out.println("PASS (Usage recorded for month " + billingMonth + ")");
                testsPassed++;
            } else {
                System.out.println("FAIL (Could not record usage)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static Bill testBillGeneration(BillingService billingService, String customerId, String billingMonth) {
        System.out.print("[TEST 6] Testing Automatic Bill Calculation & Generation... ");
        try {
            Bill bill = billingService.generateBillForCustomer(customerId, billingMonth, new BigDecimal("125.50"));
            if (bill != null && bill.getTotalAmount() != null && bill.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("PASS (Bill ID: " + bill.getBillId() + ", Total: Rs." + bill.getTotalAmount() + ", Status: " + bill.getBillStatus() + ")");
                testsPassed++;
                return bill;
            } else {
                System.out.println("FAIL (Bill returned null or invalid amount)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
        return null;
    }

    private static void testPaymentProcessing(PaymentService paymentService, BillDAO billDAO, String billId, String customerId, BigDecimal amount) {
        System.out.print("[TEST 7] Testing Simulated Online Payment for Bill " + billId + "... ");
        try {
            Payment payment = paymentService.processPayment(billId, customerId, amount, "UPI", "TEST-UPI-REF-12345");
            Bill updatedBill = billDAO.getBillById(billId);
            if (payment != null && "SUCCESS".equalsIgnoreCase(payment.getPaymentStatus()) &&
                    updatedBill != null && "PAID".equalsIgnoreCase(updatedBill.getBillStatus())) {
                System.out.println("PASS (Payment: " + payment.getPaymentId() + ", Bill Status: " + updatedBill.getBillStatus() + ")");
                testsPassed++;
            } else {
                System.out.println("FAIL (Payment did not complete or bill status not updated)");
                testsFailed++;
            }
        } catch (Exception e) {
            System.out.println("FAIL (" + e.getMessage() + ")");
            testsFailed++;
        }
    }

    private static void cleanupTestData(String customerId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                try (java.sql.Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("DELETE FROM Payments WHERE customer_id = '" + customerId + "'");
                    stmt.executeUpdate("DELETE FROM Bills WHERE customer_id = '" + customerId + "'");
                    stmt.executeUpdate("DELETE FROM " + DBConnection.getUsageTable() + " WHERE customer_id = '" + customerId + "'");
                    stmt.executeUpdate("DELETE FROM Customers WHERE customer_id = '" + customerId + "'");
                }
            }
        } catch (Exception ignored) {
        }
    }
}
