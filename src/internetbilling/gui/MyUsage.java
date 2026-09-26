package internetbilling.gui;

import internetbilling.dao.PlanDAO;
import internetbilling.dao.UsageDAO;
import internetbilling.model.Customer;
import internetbilling.model.Plan;
import internetbilling.model.Usage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Customer Monthly Internet Usage History Module.
 */
public class MyUsage extends JFrame {

    private final Customer customer;
    private final UsageDAO usageDAO;
    private final PlanDAO planDAO;

    private JTable tblUsage;
    private DefaultTableModel tableModel;
    private JLabel lblCurrentLimit;
    private JLabel lblTotalConsumed;

    public MyUsage(Customer customer) {
        this.customer = customer;
        this.usageDAO = new UsageDAO();
        this.planDAO = new PlanDAO();
        initComponents();
        loadUsageData();
    }

    private void initComponents() {
        setTitle("My Internet Usage - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(920, 580);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "MY INTERNET DATA USAGE",
            "Monthly high-speed data consumption breakdown and extra quota analysis"
        );
        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Top Summary Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 2, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 90));

        lblCurrentLimit = new JLabel("0.00 GB");
        lblTotalConsumed = new JLabel("0.00 GB");

        statsRow.add(UIUtils.createStatCard("Monthly Plan Quota", lblCurrentLimit.getText(), UIUtils.ACCENT_BLUE));
        statsRow.add(UIUtils.createStatCard("Lifetime Data Consumed", lblTotalConsumed.getText(), UIUtils.SUCCESS_GREEN));
        centerPanel.add(statsRow, BorderLayout.NORTH);

        // Table
        String[] columns = {"Billing Month", "Data Used (GB)", "Data Limit (GB)", "Extra Data Used (GB)", "Usage Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblUsage = new JTable(tableModel);
        UIUtils.formatTable(tblUsage);

        JScrollPane scrollPane = new JScrollPane(tblUsage);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Close
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(UIUtils.BG_LIGHT);
        JButton btnClose = new JButton("CLOSE");
        UIUtils.styleSecondaryButton(btnClose);
        btnClose.setIcon(UIUtils.getBackIcon(12, UIUtils.TEXT_DARK));
        btnClose.setIconTextGap(6);
        btnClose.addActionListener(e -> dispose());
        bottomBar.add(btnClose);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private void loadUsageData() {
        tableModel.setRowCount(0);
        if (customer == null) return;

        try {
            Plan plan = planDAO.getPlanById(customer.getPlanId());
            BigDecimal limit = (plan != null) ? plan.getDataLimit() : BigDecimal.ZERO;
            lblCurrentLimit.setText(limit + " GB");

            List<Usage> list = usageDAO.getUsageByCustomer(customer.getCustomerId());
            BigDecimal lifetime = BigDecimal.ZERO;

            for (Usage u : list) {
                lifetime = lifetime.add(u.getDataUsed());
                String status = (u.getExtraData().compareTo(BigDecimal.ZERO) > 0)
                    ? "EXCEEDED LIMIT (" + u.getExtraData() + " GB EXTRA)"
                    : "WITHIN QUOTA";

                tableModel.addRow(new Object[]{
                    u.getBillingMonth(),
                    u.getDataUsed() + " GB",
                    (u.getDataLimit() != null ? u.getDataLimit() : limit) + " GB",
                    u.getExtraData() + " GB",
                    status
                });
            }
            lblTotalConsumed.setText(lifetime + " GB");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load usage history: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
