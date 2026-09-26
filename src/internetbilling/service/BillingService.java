package internetbilling.service;

import internetbilling.dao.BillDAO;
import internetbilling.dao.CustomerDAO;
import internetbilling.dao.PlanDAO;
import internetbilling.dao.UsageDAO;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Plan;
import internetbilling.model.Usage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

/**
 * Service managing billing calculations and bill generation.
 */
public class BillingService {

    private final BillDAO billDAO;
    private final CustomerDAO customerDAO;
    private final PlanDAO planDAO;
    private final UsageDAO usageDAO;

    public BillingService() {
        this.billDAO = new BillDAO();
        this.customerDAO = new CustomerDAO();
        this.planDAO = new PlanDAO();
        this.usageDAO = new UsageDAO();
    }

    /**
     * Calculates and generates a new bill automatically based on customer's plan and monthly usage.
     *
     * Calculation:
     * Extra Data = Data Used - Data Limit
     * Extra Usage Charge = Extra Data * Extra Data Charge per GB (0 if Data Used <= Data Limit)
     * Late Fine = Plan's Late Payment Fine if customer has overdue previous bills, else 0
     * Total = Plan Tariff + Equipment Rental + Extra Usage Charge + Late Fine
     *
     * @param customerId Customer ID
     * @param billingMonth Month string (e.g., "2026-10")
     * @param dataUsed Data used in GB
     * @return generated Bill object
     * @throws IllegalArgumentException on duplicate bill or invalid customer/plan
     * @throws SQLException on database error
     */
    public Bill generateBillForCustomer(String customerId, String billingMonth, BigDecimal dataUsed)
            throws SQLException, IllegalArgumentException {

        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (billingMonth == null || billingMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("Billing month cannot be empty.");
        }
        if (dataUsed == null || dataUsed.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Data usage cannot be negative.");
        }

        // Prevent duplicate bill for the same customer + month
        if (billDAO.isBillExists(customerId, billingMonth)) {
            throw new IllegalArgumentException("A bill already exists for customer " + customerId + " and month " + billingMonth);
        }

        Customer customer = customerDAO.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }

        Plan plan = planDAO.getPlanById(customer.getPlanId());
        if (plan == null) {
            throw new IllegalArgumentException("Plan not found for customer: " + customer.getPlanId());
        }

        // Calculate Extra Data
        BigDecimal dataLimit = plan.getDataLimit();
        BigDecimal extraData = BigDecimal.ZERO;
        if (dataUsed.compareTo(dataLimit) > 0) {
            extraData = dataUsed.subtract(dataLimit);
        }

        // Record or fetch usage record
        int usageId = -1;
        Usage existingUsage = usageDAO.getUsageByCustomerAndMonth(customerId, billingMonth);
        if (existingUsage != null) {
            existingUsage.setDataUsed(dataUsed);
            existingUsage.setExtraData(extraData);
            usageDAO.updateUsage(existingUsage);
            usageId = existingUsage.getUsageId();
        } else {
            Usage newUsage = new Usage(0, customerId, billingMonth, dataUsed, extraData);
            usageId = usageDAO.addUsage(newUsage);
        }

        // Calculate Extra Usage Charge
        BigDecimal extraUsageCharge = BigDecimal.ZERO;
        if (extraData.compareTo(BigDecimal.ZERO) > 0) {
            extraUsageCharge = extraData.multiply(plan.getExtraDataCharge()).setScale(2, RoundingMode.HALF_UP);
        }

        // Check for previous overdue bill to apply late payment fine
        BigDecimal lateFine = BigDecimal.ZERO;
        if (billDAO.hasOverdueBill(customerId, billingMonth)) {
            lateFine = plan.getLatePaymentFine().setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal planTariff = plan.getMonthlyTariff().setScale(2, RoundingMode.HALF_UP);
        BigDecimal equipmentRental = plan.getEquipmentRental().setScale(2, RoundingMode.HALF_UP);

        // Total Bill Amount
        BigDecimal totalAmount = planTariff
                .add(equipmentRental)
                .add(extraUsageCharge)
                .add(lateFine)
                .setScale(2, RoundingMode.HALF_UP);

        // Generate unique Bill ID
        String billId = generateUniqueBillId(customerId, billingMonth);

        // Due date: 15 days from today
        LocalDate due = LocalDate.now().plusDays(15);
        Date dueDate = Date.valueOf(due);

        Bill bill = new Bill(
                billId,
                customerId,
                usageId > 0 ? usageId : null,
                billingMonth,
                planTariff,
                equipmentRental,
                extraUsageCharge,
                lateFine,
                totalAmount,
                dueDate,
                "UNPAID"
        );

        bill.setCustomerName(customer.getName());
        bill.setCustomerAddress(customer.getAddress());
        bill.setPlanName(plan.getPlanName());
        bill.setSpeedTier(plan.getSpeedTier());
        bill.setDataUsed(dataUsed);
        bill.setExtraData(extraData);

        boolean added = billDAO.addBill(bill);
        if (!added) {
            throw new SQLException("Failed to save bill to database.");
        }

        return bill;
    }

    /**
     * Generates a unique Bill ID.
     * Format: BILL-YYYYMM-XXXX (clean and distinct)
     */
    public String generateUniqueBillId(String customerId, String billingMonth) {
        String cleanMonth = billingMonth.replaceAll("[^0-9]", "");
        if (cleanMonth.length() > 6) {
            cleanMonth = cleanMonth.substring(0, 6);
        } else if (cleanMonth.isEmpty()) {
            cleanMonth = String.valueOf(LocalDate.now().getYear()) + String.format("%02d", LocalDate.now().getMonthValue());
        }
        int rand = 1000 + new Random().nextInt(9000);
        return "BILL-" + cleanMonth + "-" + rand;
    }
}
