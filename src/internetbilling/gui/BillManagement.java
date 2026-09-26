package internetbilling.gui;

import internetbilling.dao.BillDAO;
import internetbilling.dao.CustomerDAO;
import internetbilling.dao.UsageDAO;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import internetbilling.model.Usage;
import internetbilling.service.BillingService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Bill Management Module for Administrator.
 * Supports generating bills, searching, filtering by status, viewing details, and printing invoices.
 */
public class BillManagement extends JFrame {

    private final BillDAO billDAO;
    private final CustomerDAO customerDAO;
    private final BillingService billingService;
    private final AdminDashboard parentDashboard;

    // Controls
    private JComboBox<String> cmbFilterStatus;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JButton btnGenerateBill;
    private JButton btnViewPrint;
    private JButton btnMarkPaid;

    // Table
    private JTable tblBills;
    private DefaultTableModel tableModel;

    public BillManagement(AdminDashboard parent) {
        this.parentDashboard = parent;
        this.billDAO = new BillDAO();
        this.customerDAO = new CustomerDAO();
        this.billingService = new BillingService();
        initComponents();
        loadBillsTable("ALL");
    }

    private void initComponents() {
        setTitle("Bill Management - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "BILL MANAGEMENT & INVOICING",
            "Generate automatic subscriber bills, track collections, and print tax invoices"
        );
        add(header, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Top Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 10));
        toolbar.setBackground(UIUtils.CARD_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));

        // Left filter & search
        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leftBar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Status:");
        lblFilter.setFont(UIUtils.FONT_REGULAR_BOLD);

        cmbFilterStatus = new JComboBox<>(new String[]{"ALL", "UNPAID", "PAID", "OVERDUE"});
        cmbFilterStatus.setFont(UIUtils.FONT_REGULAR);
        cmbFilterStatus.addActionListener(e -> loadBillsTable((String) cmbFilterStatus.getSelectedItem()));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UIUtils.FONT_REGULAR_BOLD);
        txtSearch = new JTextField(15);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setPreferredSize(new Dimension(170, 32));

        btnSearch = new JButton("SEARCH");
        UIUtils.stylePrimaryButton(btnSearch);
        btnSearch.setIcon(UIUtils.getSearchIcon(13, Color.WHITE));
        btnSearch.setIconTextGap(6);
        btnSearch.addActionListener(e -> performSearch());

        btnRefresh = new JButton("REFRESH");
        UIUtils.styleSecondaryButton(btnRefresh);
        btnRefresh.setIcon(UIUtils.getRefreshIcon(13, UIUtils.TEXT_DARK));
        btnRefresh.setIconTextGap(6);
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cmbFilterStatus.setSelectedIndex(0);
            loadBillsTable("ALL");
        });

        leftBar.add(lblFilter);
        leftBar.add(cmbFilterStatus);
        leftBar.add(lblSearch);
        leftBar.add(txtSearch);
        leftBar.add(btnSearch);
        leftBar.add(btnRefresh);

        // Right actions
        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightBar.setOpaque(false);

        btnGenerateBill = new JButton("GENERATE BILL");
        UIUtils.styleSuccessButton(btnGenerateBill);
        btnGenerateBill.setIcon(UIUtils.getPlusIcon(13, Color.WHITE));
        btnGenerateBill.setIconTextGap(6);
        btnGenerateBill.addActionListener(e -> showGenerateBillDialog());

        btnViewPrint = new JButton("VIEW / PRINT BILL");
        UIUtils.stylePrimaryButton(btnViewPrint);
        btnViewPrint.setIcon(UIUtils.getBillsIcon(13, Color.WHITE));
        btnViewPrint.setIconTextGap(6);
        btnViewPrint.addActionListener(e -> viewPrintSelectedBill());

        btnMarkPaid = new JButton("MARK PAID");
        UIUtils.styleSecondaryButton(btnMarkPaid);
        btnMarkPaid.setIcon(UIUtils.getCheckIcon(13, UIUtils.SUCCESS_GREEN));
        btnMarkPaid.setIconTextGap(6);
        btnMarkPaid.addActionListener(e -> markSelectedBillPaid());

        rightBar.add(btnGenerateBill);
        rightBar.add(btnViewPrint);
        rightBar.add(btnMarkPaid);

        toolbar.add(leftBar, BorderLayout.WEST);
        toolbar.add(rightBar, BorderLayout.EAST);
        centerPanel.add(toolbar, BorderLayout.NORTH);

        // Bill Table
        String[] columns = {
            "Bill ID", "Customer ID", "Customer Name", "Billing Month", "Plan Tariff (" + UIUtils.CURRENCY_SYMBOL + ")",
            "Rental (" + UIUtils.CURRENCY_SYMBOL + ")", "Extra Usage (" + UIUtils.CURRENCY_SYMBOL + ")", "Late Fine (" + UIUtils.CURRENCY_SYMBOL + ")", "Total Amount (" + UIUtils.CURRENCY_SYMBOL + ")", "Due Date", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblBills = new JTable(tableModel);
        UIUtils.formatTable(tblBills);

        tblBills.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPrintSelectedBill();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBills);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadBillsTable(String filterStatus) {
        tableModel.setRowCount(0);
        try {
            List<Bill> list;
            if ("ALL".equalsIgnoreCase(filterStatus)) {
                list = billDAO.getAllBills();
            } else {
                list = billDAO.getBillsByStatus(filterStatus);
            }

            for (Bill b : list) {
                tableModel.addRow(new Object[]{
                    b.getBillId(),
                    b.getCustomerId(),
                    b.getCustomerName(),
                    b.getBillingMonth(),
                    b.getPlanTariff(),
                    b.getEquipmentRental(),
                    b.getExtraUsageCharge(),
                    b.getLateFine(),
                    b.getTotalAmount(),
                    b.getDueDate(),
                    b.getBillStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load bills: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (parentDashboard != null) parentDashboard.loadDashboardStats();
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadBillsTable((String) cmbFilterStatus.getSelectedItem());
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Bill> list = billDAO.searchBills(keyword);
            for (Bill b : list) {
                tableModel.addRow(new Object[]{
                    b.getBillId(),
                    b.getCustomerId(),
                    b.getCustomerName(),
                    b.getBillingMonth(),
                    b.getPlanTariff(),
                    b.getEquipmentRental(),
                    b.getExtraUsageCharge(),
                    b.getLateFine(),
                    b.getTotalAmount(),
                    b.getDueDate(),
                    b.getBillStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showGenerateBillDialog() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            if (customers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No customers found. Please add a customer first.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JComboBox<CustomerItem> cmbCust = new JComboBox<>();
            for (Customer c : customers) {
                cmbCust.addItem(new CustomerItem(c.getCustomerId(), c.getName(), c.getPlanId()));
            }

            LocalDate now = LocalDate.now();
            JTextField txtMonth = new JTextField(String.format("%04d-%02d", now.getYear(), now.getMonthValue()));
            JTextField txtUsage = new JTextField("0.00");

            JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
            form.add(new JLabel("Customer:"));
            form.add(cmbCust);
            form.add(new JLabel("Billing Month (YYYY-MM):"));
            form.add(txtMonth);
            form.add(new JLabel("Total Data Used (GB):"));
            form.add(txtUsage);

            int result = JOptionPane.showConfirmDialog(this, form, "Generate Automatic Customer Bill",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                CustomerItem item = (CustomerItem) cmbCust.getSelectedItem();
                String month = txtMonth.getText().trim();
                BigDecimal dataUsed = new BigDecimal(txtUsage.getText().trim());

                Bill bill = billingService.generateBillForCustomer(item.getId(), month, dataUsed);
                JOptionPane.showMessageDialog(this,
                    "Bill Generated Successfully!\n" +
                    "Bill ID: " + bill.getBillId() + "\n" +
                    "Customer: " + bill.getCustomerName() + "\n" +
                    "Total Amount: " + UIUtils.formatCurrency(bill.getTotalAmount()),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                loadBillsTable((String) cmbFilterStatus.getSelectedItem());
                new BillPrintDialog(this, bill).setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPrintSelectedBill() {
        int row = tblBills.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) tableModel.getValueAt(row, 0);
        try {
            Bill bill = billDAO.getBillById(billId);
            if (bill != null) {
                new BillPrintDialog(this, bill).setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading bill details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markSelectedBillPaid() {
        int row = tblBills.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a bill to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) tableModel.getValueAt(row, 0);
        String currentStatus = (String) tableModel.getValueAt(row, 10);
        if ("PAID".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This bill is already marked as PAID.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Mark Bill '" + billId + "' as PAID?", "Confirm Status Update", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                boolean success = billDAO.updateBillStatus(billId, "PAID");
                if (success) {
                    JOptionPane.showMessageDialog(this, "Bill status updated to PAID.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadBillsTable((String) cmbFilterStatus.getSelectedItem());
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating bill status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class CustomerItem {
        private final String id;
        private final String name;
        private final String planId;

        public CustomerItem(String id, String name, String planId) {
            this.id = id;
            this.name = name;
            this.planId = planId;
        }

        public String getId() { return id; }

        @Override
        public String toString() {
            return id + " - " + name;
        }
    }
}
