package internetbilling.gui;

import internetbilling.dao.BillDAO;
import internetbilling.model.Bill;
import internetbilling.model.Customer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
 * Customer Billing Screen combining Current Bill card (Section 17) and Previous Bills History (Section 18).
 */
public class MyBills extends JFrame {

    private final Customer customer;
    private final BillDAO billDAO;

    // Current Bill Card components
    private JPanel currentBillCard;
    private JLabel lblCurrentBillId;
    private JLabel lblCurrentMonth;
    private JLabel lblCurrentTariff;
    private JLabel lblCurrentRental;
    private JLabel lblCurrentExtra;
    private JLabel lblCurrentFine;
    private JLabel lblCurrentTotal;
    private JLabel lblCurrentDueDate;
    private JLabel lblCurrentStatus;
    private JButton btnViewCurrentBill;
    private JButton btnPayCurrentBill;

    // Previous Bills Table
    private JTable tblBills;
    private DefaultTableModel tableModel;
    private JButton btnViewSelected;
    private JButton btnRefresh;

    private Bill currentUnpaidBill = null;

    public MyBills(Customer customer) {
        this.customer = customer;
        this.billDAO = new BillDAO();
        initComponents();
        loadBillingData();
    }

    private void initComponents() {
        setTitle("My Bills & Payments - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            "MY INVOICES & BILL PAYMENTS",
            "View outstanding dues, breakdown of charges, and transaction receipts"
        );
        add(header, BorderLayout.NORTH);

        // Center Split: Current Bill on Top, Table on Bottom
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // ==========================================
        // SECTION 17: CURRENT BILL CARD
        // ==========================================
        JLabel lblSectionCurrent = new JLabel("Current Outstanding Invoice");
        lblSectionCurrent.setFont(UIUtils.FONT_HEADER);
        lblSectionCurrent.setForeground(UIUtils.TEXT_DARK);
        centerPanel.add(lblSectionCurrent);
        centerPanel.add(Box.createVerticalStrut(8));

        currentBillCard = new JPanel(new BorderLayout(15, 10));
        currentBillCard.setBackground(UIUtils.CARD_BG);
        currentBillCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        currentBillCard.setPreferredSize(new Dimension(1040, 150));
        currentBillCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel billDetailsGrid = new JPanel(new GridLayout(2, 4, 15, 8));
        billDetailsGrid.setOpaque(false);

        lblCurrentBillId = new JLabel("None");
        lblCurrentMonth = new JLabel("-");
        lblCurrentTariff = new JLabel(UIUtils.formatCurrency(0.0));
        lblCurrentRental = new JLabel(UIUtils.formatCurrency(0.0));
        lblCurrentExtra = new JLabel(UIUtils.formatCurrency(0.0));
        lblCurrentFine = new JLabel(UIUtils.formatCurrency(0.0));
        lblCurrentTotal = new JLabel(UIUtils.formatCurrency(0.0));
        lblCurrentDueDate = new JLabel("-");
        lblCurrentStatus = new JLabel("ALL PAID");

        lblCurrentTotal.setFont(UIUtils.FONT_TITLE);
        lblCurrentTotal.setForeground(UIUtils.ACCENT_BLUE);

        billDetailsGrid.add(createMiniField("Bill ID:", lblCurrentBillId));
        billDetailsGrid.add(createMiniField("Billing Month:", lblCurrentMonth));
        billDetailsGrid.add(createMiniField("Plan Tariff:", lblCurrentTariff));
        billDetailsGrid.add(createMiniField("Equipment Rental:", lblCurrentRental));
        billDetailsGrid.add(createMiniField("Extra Usage Charge:", lblCurrentExtra));
        billDetailsGrid.add(createMiniField("Late Fine:", lblCurrentFine));
        billDetailsGrid.add(createMiniField("TOTAL DUE:", lblCurrentTotal));
        billDetailsGrid.add(createMiniField("Due Date:", lblCurrentDueDate));

        currentBillCard.add(billDetailsGrid, BorderLayout.CENTER);

        // Action Buttons on Right of Card
        JPanel currentActions = new JPanel(new GridLayout(2, 1, 0, 10));
        currentActions.setOpaque(false);
        currentActions.setPreferredSize(new Dimension(160, 0));

        btnViewCurrentBill = new JButton("VIEW BILL");
        UIUtils.stylePrimaryButton(btnViewCurrentBill);
        btnViewCurrentBill.setIcon(UIUtils.getBillsIcon(13, Color.WHITE));
        btnViewCurrentBill.setIconTextGap(6);
        btnViewCurrentBill.addActionListener(e -> {
            if (currentUnpaidBill != null) {
                new BillPrintDialog(this, currentUnpaidBill).setVisible(true);
            }
        });

        btnPayCurrentBill = new JButton("PAY BILL NOW");
        UIUtils.styleSuccessButton(btnPayCurrentBill);
        btnPayCurrentBill.setIcon(UIUtils.getPaymentsIcon(13, Color.WHITE));
        btnPayCurrentBill.setIconTextGap(6);
        btnPayCurrentBill.addActionListener(e -> {
            if (currentUnpaidBill != null) {
                new PaymentForm(customer, currentUnpaidBill, this::loadBillingData).setVisible(true);
            }
        });

        currentActions.add(btnViewCurrentBill);
        currentActions.add(btnPayCurrentBill);
        currentBillCard.add(currentActions, BorderLayout.EAST);

        centerPanel.add(currentBillCard);
        centerPanel.add(Box.createVerticalStrut(20));

        // ==========================================
        // SECTION 18: PREVIOUS BILLS TABLE
        // ==========================================
        JPanel tableHeaderRow = new JPanel(new BorderLayout());
        tableHeaderRow.setOpaque(false);

        JLabel lblSectionHistory = new JLabel("All Invoices & Billing History");
        lblSectionHistory.setFont(UIUtils.FONT_HEADER);
        lblSectionHistory.setForeground(UIUtils.TEXT_DARK);

        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tableActions.setOpaque(false);

        btnViewSelected = new JButton("VIEW SELECTED BILL");
        UIUtils.stylePrimaryButton(btnViewSelected);
        btnViewSelected.setIcon(UIUtils.getBillsIcon(13, Color.WHITE));
        btnViewSelected.setIconTextGap(6);
        btnViewSelected.addActionListener(e -> viewSelectedBill());

        btnRefresh = new JButton("REFRESH");
        UIUtils.styleSecondaryButton(btnRefresh);
        btnRefresh.setIcon(UIUtils.getRefreshIcon(13, UIUtils.TEXT_DARK));
        btnRefresh.setIconTextGap(6);
        btnRefresh.addActionListener(e -> loadBillingData());

        tableActions.add(btnViewSelected);
        tableActions.add(btnRefresh);

        tableHeaderRow.add(lblSectionHistory, BorderLayout.WEST);
        tableHeaderRow.add(tableActions, BorderLayout.EAST);
        centerPanel.add(tableHeaderRow);
        centerPanel.add(Box.createVerticalStrut(8));

        String[] columns = {"Bill ID", "Billing Month", "Total Amount (" + UIUtils.CURRENCY_SYMBOL + ")", "Due Date", "Payment Status"};
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
                    viewSelectedBill();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBills);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createMiniField(String label, JLabel valLabel) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setFont(UIUtils.FONT_SMALL);
        l.setForeground(UIUtils.TEXT_MUTED);

        valLabel.setFont(UIUtils.FONT_REGULAR_BOLD);
        p.add(l, BorderLayout.NORTH);
        p.add(valLabel, BorderLayout.CENTER);
        return p;
    }

    public void loadBillingData() {
        if (customer == null) return;

        // 1. Load Current Unpaid Bill
        try {
            currentUnpaidBill = billDAO.getCurrentUnpaidBill(customer.getCustomerId());
            if (currentUnpaidBill != null) {
                lblCurrentBillId.setText(currentUnpaidBill.getBillId());
                lblCurrentMonth.setText(currentUnpaidBill.getBillingMonth());
                lblCurrentTariff.setText(UIUtils.formatCurrency(currentUnpaidBill.getPlanTariff()));
                lblCurrentRental.setText(UIUtils.formatCurrency(currentUnpaidBill.getEquipmentRental()));
                lblCurrentExtra.setText(UIUtils.formatCurrency(currentUnpaidBill.getExtraUsageCharge()));
                lblCurrentFine.setText(UIUtils.formatCurrency(currentUnpaidBill.getLateFine()));
                lblCurrentTotal.setText(UIUtils.formatCurrency(currentUnpaidBill.getTotalAmount()));
                lblCurrentDueDate.setText(currentUnpaidBill.getDueDate().toString());
                btnViewCurrentBill.setEnabled(true);
                btnPayCurrentBill.setEnabled(true);
            } else {
                lblCurrentBillId.setText("None");
                lblCurrentMonth.setText("No Outstanding Bills");
                lblCurrentTariff.setText(UIUtils.formatCurrency(0.0));
                lblCurrentRental.setText(UIUtils.formatCurrency(0.0));
                lblCurrentExtra.setText(UIUtils.formatCurrency(0.0));
                lblCurrentFine.setText(UIUtils.formatCurrency(0.0));
                lblCurrentTotal.setText(UIUtils.formatCurrency(0.0));
                lblCurrentDueDate.setText("Account in good standing");
                btnViewCurrentBill.setEnabled(false);
                btnPayCurrentBill.setEnabled(false);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading current bill: " + ex.getMessage());
        }

        // 2. Load History Table
        tableModel.setRowCount(0);
        try {
            List<Bill> list = billDAO.getBillsByCustomer(customer.getCustomerId());
            for (Bill b : list) {
                tableModel.addRow(new Object[]{
                    b.getBillId(),
                    b.getBillingMonth(),
                    b.getTotalAmount(),
                    b.getDueDate(),
                    b.getBillStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load bills history: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedBill() {
        int row = tblBills.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an invoice from the history table.",
                "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) tableModel.getValueAt(row, 0);
        try {
            Bill bill = billDAO.getBillById(billId);
            if (bill != null) {
                new BillPrintDialog(this, bill).setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
