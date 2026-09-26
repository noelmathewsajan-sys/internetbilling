package internetbilling.model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Bill entity representing generated internet subscription invoices.
 */
public class Bill {
    private String billId;
    private String customerId;
    private Integer usageId;
    private String billingMonth;
    private BigDecimal planTariff;
    private BigDecimal equipmentRental;
    private BigDecimal extraUsageCharge;
    private BigDecimal lateFine;
    private BigDecimal totalAmount;
    private Date dueDate;
    private String billStatus; // 'PAID', 'UNPAID', 'OVERDUE'

    // Display helpers for receipts and UI tables
    private String customerName;
    private String customerAddress;
    private String planName;
    private String speedTier;
    private BigDecimal dataUsed;
    private BigDecimal extraData;

    public Bill() {
        this.planTariff = BigDecimal.ZERO;
        this.equipmentRental = BigDecimal.ZERO;
        this.extraUsageCharge = BigDecimal.ZERO;
        this.lateFine = BigDecimal.ZERO;
        this.totalAmount = BigDecimal.ZERO;
        this.billStatus = "UNPAID";
    }

    public Bill(String billId, String customerId, Integer usageId, String billingMonth,
                BigDecimal planTariff, BigDecimal equipmentRental, BigDecimal extraUsageCharge,
                BigDecimal lateFine, BigDecimal totalAmount, Date dueDate, String billStatus) {
        this.billId = billId;
        this.customerId = customerId;
        this.usageId = usageId;
        this.billingMonth = billingMonth;
        this.planTariff = planTariff;
        this.equipmentRental = equipmentRental;
        this.extraUsageCharge = extraUsageCharge;
        this.lateFine = lateFine;
        this.totalAmount = totalAmount;
        this.dueDate = dueDate;
        this.billStatus = billStatus;
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

    public Integer getUsageId() {
        return usageId;
    }

    public void setUsageId(Integer usageId) {
        this.usageId = usageId;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public BigDecimal getPlanTariff() {
        return planTariff;
    }

    public void setPlanTariff(BigDecimal planTariff) {
        this.planTariff = planTariff;
    }

    public BigDecimal getEquipmentRental() {
        return equipmentRental;
    }

    public void setEquipmentRental(BigDecimal equipmentRental) {
        this.equipmentRental = equipmentRental;
    }

    public BigDecimal getExtraUsageCharge() {
        return extraUsageCharge;
    }

    public void setExtraUsageCharge(BigDecimal extraUsageCharge) {
        this.extraUsageCharge = extraUsageCharge;
    }

    public BigDecimal getLateFine() {
        return lateFine;
    }

    public void setLateFine(BigDecimal lateFine) {
        this.lateFine = lateFine;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getSpeedTier() {
        return speedTier;
    }

    public void setSpeedTier(String speedTier) {
        this.speedTier = speedTier;
    }

    public BigDecimal getDataUsed() {
        return dataUsed;
    }

    public void setDataUsed(BigDecimal dataUsed) {
        this.dataUsed = dataUsed;
    }

    public BigDecimal getExtraData() {
        return extraData;
    }

    public void setExtraData(BigDecimal extraData) {
        this.extraData = extraData;
    }

    @Override
    public String toString() {
        return billId + " (" + billingMonth + ") - \u20B9" + totalAmount + " [" + billStatus + "]";
    }
}

