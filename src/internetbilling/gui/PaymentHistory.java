package internetbilling.gui;

import internetbilling.dao.PaymentDAO;
import internetbilling.model.Customer;
import internetbilling.model.Payment;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Payment History Module for Administrator and Customer Portal.
 */
public class PaymentHistory extends JFrame {

    private final Customer currentCustomer; // null if admin
    private final PaymentDAO paymentDAO;

    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;
    private JTable tblPayments;
    private DefaultTableModel tableModel;

    public PaymentHistory(Customer customer) {
        this.currentCustomer = customer;
        this.paymentDAO = new PaymentDAO();
        initComponents();
        loadPayments();
    }

    private void initComponents() {
        String title = (currentCustomer == null) ? "Payment Transactions Audit" : "My Payment History";
        setTitle(title + " - Internet Billing Management System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1020, 620);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIUtils.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header
        JPanel header = UIUtils.createHeaderPanel(
            title.toUpperCase(),
            (currentCustomer == null) ? "Track and audit all subscriber billing payment receipts"
                                      : "View your verified transaction receipts and simulated payment records"
        );
        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Search Bar (Enabled for admin, or simple refresh for user)
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchBar.setBackground(UIUtils.CARD_BG);
        searchBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblSearch = new JLabel("Search Transactions:");
        lblSearch.setFont(UIUtils.FONT_REGULAR_BOLD);
        txtSearch = new JTextField(20);
        UIUtils.styleTextField(txtSearch);
        txtSearch.setPreferredSize(new Dimension(220, 32));

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
            loadPayments();
        });

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnRefresh);
        centerPanel.add(searchBar, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "Payment ID", "Bill ID", "Customer ID", "Customer Name",
            "Payment Date", "Amount (" + UIUtils.CURRENCY_SYMBOL + ")", "Method", "Reference", "Status"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblPayments = new JTable(tableModel);
        UIUtils.formatTable(tblPayments);

        JScrollPane scrollPane = new JScrollPane(tblPayments);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void loadPayments() {
        tableModel.setRowCount(0);
        try {
            List<Payment> list;
            if (currentCustomer == null) {
                list = paymentDAO.getAllPayments();
            } else {
                list = paymentDAO.getPaymentsByCustomer(currentCustomer.getCustomerId());
            }

            for (Payment p : list) {
                tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getBillId(),
                    p.getCustomerId(),
                    p.getCustomerName() != null ? p.getCustomerName() : (currentCustomer != null ? currentCustomer.getName() : "N/A"),
                    p.getPaymentDate(),
                    p.getAmount(),
                    p.getPaymentMethod(),
                    p.getPaymentReference(),
                    p.getPaymentStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payment transactions: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadPayments();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<Payment> list = paymentDAO.searchPayments(keyword);
            for (Payment p : list) {
                // If user view, ensure only their payments are shown
                if (currentCustomer != null && !p.getCustomerId().equalsIgnoreCase(currentCustomer.getCustomerId())) {
                    continue;
                }
                tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getBillId(),
                    p.getCustomerId(),
                    p.getCustomerName() != null ? p.getCustomerName() : "N/A",
                    p.getPaymentDate(),
                    p.getAmount(),
                    p.getPaymentMethod(),
                    p.getPaymentReference(),
                    p.getPaymentStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
