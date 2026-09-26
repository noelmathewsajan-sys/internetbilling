package internetbilling.model;

import java.math.BigDecimal;

/**
 * Usage entity representing monthly internet data consumption.
 */
public class Usage {
    private int usageId;
    private String customerId;
    private String billingMonth;
    private BigDecimal dataUsed;
    private BigDecimal extraData;

    // Display helpers
    private String customerName;
    private BigDecimal dataLimit;

    public Usage() {
        this.dataUsed = BigDecimal.ZERO;
        this.extraData = BigDecimal.ZERO;
        this.dataLimit = BigDecimal.ZERO;
    }

    public Usage(int usageId, String customerId, String billingMonth, BigDecimal dataUsed, BigDecimal extraData) {
        this.usageId = usageId;
        this.customerId = customerId;
        this.billingMonth = billingMonth;
        this.dataUsed = dataUsed;
        this.extraData = extraData;
    }

    public int getUsageId() {
        return usageId;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(BigDecimal dataLimit) {
        this.dataLimit = dataLimit;
    }
}
