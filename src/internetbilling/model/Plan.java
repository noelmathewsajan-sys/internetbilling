package internetbilling.model;

import java.math.BigDecimal;

/**
 * Plan entity representing Internet subscription packages.
 */
public class Plan {
    private String planId;
    private String planName;
    private String speedTier;
    private BigDecimal monthlyTariff;
    private BigDecimal dataLimit;
    private BigDecimal extraDataCharge;
    private BigDecimal equipmentRental;
    private BigDecimal latePaymentFine;

    public Plan() {
        this.monthlyTariff = BigDecimal.ZERO;
        this.dataLimit = BigDecimal.ZERO;
        this.extraDataCharge = BigDecimal.ZERO;
        this.equipmentRental = BigDecimal.ZERO;
        this.latePaymentFine = BigDecimal.ZERO;
    }

    public Plan(String planId, String planName, String speedTier, BigDecimal monthlyTariff,
                BigDecimal dataLimit, BigDecimal extraDataCharge, BigDecimal equipmentRental,
                BigDecimal latePaymentFine) {
        this.planId = planId;
        this.planName = planName;
        this.speedTier = speedTier;
        this.monthlyTariff = monthlyTariff;
        this.dataLimit = dataLimit;
        this.extraDataCharge = extraDataCharge;
        this.equipmentRental = equipmentRental;
        this.latePaymentFine = latePaymentFine;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
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

    public BigDecimal getMonthlyTariff() {
        return monthlyTariff;
    }

    public void setMonthlyTariff(BigDecimal monthlyTariff) {
        this.monthlyTariff = monthlyTariff;
    }

    public BigDecimal getDataLimit() {
        return dataLimit;
    }

    public void setDataLimit(BigDecimal dataLimit) {
        this.dataLimit = dataLimit;
    }

    public BigDecimal getExtraDataCharge() {
        return extraDataCharge;
    }

    public void setExtraDataCharge(BigDecimal extraDataCharge) {
        this.extraDataCharge = extraDataCharge;
    }

    public BigDecimal getEquipmentRental() {
        return equipmentRental;
    }

    public void setEquipmentRental(BigDecimal equipmentRental) {
        this.equipmentRental = equipmentRental;
    }

    public BigDecimal getLatePaymentFine() {
        return latePaymentFine;
    }

    public void setLatePaymentFine(BigDecimal latePaymentFine) {
        this.latePaymentFine = latePaymentFine;
    }

    @Override
    public String toString() {
        return planName + " (" + speedTier + ") - \u20B9" + monthlyTariff;
    }
}

