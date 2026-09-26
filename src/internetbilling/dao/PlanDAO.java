package internetbilling.dao;

import internetbilling.database.DBConnection;
import internetbilling.model.Plan;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Plans table.
 */
public class PlanDAO {

    /**
     * Retrieves all plans.
     */
    public List<Plan> getAllPlans() throws SQLException {
        List<Plan> plans = new ArrayList<>();
        String sql = "SELECT plan_id, plan_name, speed_tier, monthly_tariff, data_limit, " +
                     "extra_data_charge, equipment_rental, late_payment_fine FROM Plans ORDER BY plan_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                plans.add(mapResultSetToPlan(rs));
            }
        }
        return plans;
    }

    /**
     * Retrieves plan by plan ID.
     */
    public Plan getPlanById(String planId) throws SQLException {
        String sql = "SELECT plan_id, plan_name, speed_tier, monthly_tariff, data_limit, " +
                     "extra_data_charge, equipment_rental, late_payment_fine FROM Plans WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPlan(rs);
                }
            }
        }
        return null;
    }

    /**
     * Adds a new internet plan.
     */
    public boolean addPlan(Plan plan) throws SQLException {
        String sql = "INSERT INTO Plans (plan_id, plan_name, speed_tier, monthly_tariff, " +
                     "data_limit, extra_data_charge, equipment_rental, late_payment_fine) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plan.getPlanId());
            ps.setString(2, plan.getPlanName());
            ps.setString(3, plan.getSpeedTier());
            ps.setBigDecimal(4, plan.getMonthlyTariff());
            ps.setBigDecimal(5, plan.getDataLimit());
            ps.setBigDecimal(6, plan.getExtraDataCharge());
            ps.setBigDecimal(7, plan.getEquipmentRental());
            ps.setBigDecimal(8, plan.getLatePaymentFine());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Updates an existing internet plan.
     */
    public boolean updatePlan(Plan plan) throws SQLException {
        String sql = "UPDATE Plans SET plan_name = ?, speed_tier = ?, monthly_tariff = ?, " +
                     "data_limit = ?, extra_data_charge = ?, equipment_rental = ?, late_payment_fine = ? " +
                     "WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, plan.getPlanName());
            ps.setString(2, plan.getSpeedTier());
            ps.setBigDecimal(3, plan.getMonthlyTariff());
            ps.setBigDecimal(4, plan.getDataLimit());
            ps.setBigDecimal(5, plan.getExtraDataCharge());
            ps.setBigDecimal(6, plan.getEquipmentRental());
            ps.setBigDecimal(7, plan.getLatePaymentFine());
            ps.setString(8, plan.getPlanId());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a plan by plan ID.
     */
    public boolean deletePlan(String planId) throws SQLException {
        String sql = "DELETE FROM Plans WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planId);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Checks if plan is in use by customers.
     */
    public boolean isPlanInUse(String planId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Customers WHERE plan_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, planId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Plan mapResultSetToPlan(ResultSet rs) throws SQLException {
        return new Plan(
            rs.getString("plan_id"),
            rs.getString("plan_name"),
            rs.getString("speed_tier"),
            rs.getBigDecimal("monthly_tariff"),
            rs.getBigDecimal("data_limit"),
            rs.getBigDecimal("extra_data_charge"),
            rs.getBigDecimal("equipment_rental"),
            rs.getBigDecimal("late_payment_fine")
        );
    }
}
