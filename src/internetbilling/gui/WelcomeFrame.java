package internetbilling.gui;

import internetbilling.database.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Welcome Screen for Internet Billing Management System.
 */
public class WelcomeFrame extends JFrame {

    private JButton btnAdminLogin;
    private JButton btnUserLogin;
    private JButton btnExit;
    private JButton btnDBSettings;
    private JLabel lblTitle;
    private JLabel lblSubtitle;
    private JLabel lblVersion;

    public WelcomeFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Internet Billing Management System - ISP Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 540);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.PRIMARY_DARK); // Dark navy background #0F2747
        setLayout(new BorderLayout());

        // Center Content with Modern Card
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel cardPanel = new JPanel();
        cardPanel.setPreferredSize(new Dimension(540, 380));
        cardPanel.setBackground(UIUtils.CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIUtils.BORDER_COLOR, 1, true),
            new EmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));

        JLabel lblBrand = new JLabel("  FIBERLINK BROADBAND NETWORK", SwingConstants.CENTER);
        lblBrand.setIcon(UIUtils.getGlobeIcon(16, UIUtils.SECONDARY_BLUE));
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBrand.setForeground(UIUtils.SECONDARY_BLUE);
        lblBrand.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Internet Billing Management", SwingConstants.CENTER);
        lblTitle.setFont(UIUtils.FONT_TITLE);
        lblTitle.setForeground(UIUtils.TEXT_DARK);
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Select an authorized portal to sign in", SwingConstants.CENTER);
        lblSub.setFont(UIUtils.FONT_SMALL);
        lblSub.setForeground(UIUtils.TEXT_MUTED);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);

        btnAdminLogin = new JButton("ADMINISTRATOR PORTAL (ISP Office)");
        UIUtils.stylePrimaryButton(btnAdminLogin);
        btnAdminLogin.setIcon(UIUtils.getUserIcon(18, Color.WHITE));
        btnAdminLogin.setIconTextGap(10);
        btnAdminLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdminLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnAdminLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnAdminLogin.addActionListener(e -> openAdminLogin());

        btnUserLogin = new JButton("SUBSCRIBER PORTAL (Customer Login)");
        UIUtils.stylePrimaryButton(btnUserLogin);
        btnUserLogin.setIcon(UIUtils.getCustomersIcon(18, Color.WHITE));
        btnUserLogin.setIconTextGap(10);
        btnUserLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnUserLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnUserLogin.setAlignmentX(CENTER_ALIGNMENT);
        btnUserLogin.addActionListener(e -> openUserLogin());

        JPanel bottomBtnRow = new JPanel(new GridLayout(1, 2, 12, 0));
        bottomBtnRow.setOpaque(false);
        bottomBtnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        btnDBSettings = new JButton("Database Settings");
        UIUtils.styleSecondaryButton(btnDBSettings);
        btnDBSettings.setIcon(UIUtils.getDatabaseIcon(14, UIUtils.TEXT_DARK));
        btnDBSettings.setIconTextGap(8);
        btnDBSettings.addActionListener(e -> openDBSettingsDialog());

        btnExit = new JButton("Exit System");
        UIUtils.styleDangerButton(btnExit);
        btnExit.setIcon(UIUtils.getLogoutIcon(14, Color.WHITE));
        btnExit.setIconTextGap(8);
        btnExit.addActionListener(e -> System.exit(0));

        bottomBtnRow.add(btnDBSettings);
        bottomBtnRow.add(btnExit);

        JLabel lblFootNote = new JLabel("  Connected to: " + DBConnection.getDbEngine() + " Database", SwingConstants.CENTER);
        lblFootNote.setIcon(UIUtils.getCheckIcon(12, UIUtils.SUCCESS_GREEN));
        lblFootNote.setFont(UIUtils.FONT_SMALL);
        lblFootNote.setForeground(UIUtils.SUCCESS_GREEN);
        lblFootNote.setAlignmentX(CENTER_ALIGNMENT);

        cardPanel.add(lblBrand);
        cardPanel.add(Box.createVerticalStrut(6));
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(2));
        cardPanel.add(lblSub);
        cardPanel.add(Box.createVerticalStrut(24));
        cardPanel.add(btnAdminLogin);
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(btnUserLogin);
        cardPanel.add(Box.createVerticalStrut(18));
        cardPanel.add(bottomBtnRow);
        cardPanel.add(Box.createVerticalStrut(16));
        cardPanel.add(lblFootNote);

        centerPanel.add(cardPanel, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Subtle bottom tag
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(UIUtils.PRIMARY_DARK);
        lblVersion = new JLabel("Enterprise ISP Management System | Professional Edition");
        lblVersion.setFont(UIUtils.FONT_SMALL);
        lblVersion.setForeground(new Color(0x66, 0x70, 0x85));
        footerPanel.add(lblVersion);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void openAdminLogin() {
        this.dispose();
        new AdminLogin().setVisible(true);
    }

    private void openUserLogin() {
        this.dispose();
        new UserLogin().setVisible(true);
    }

    private void openDBSettingsDialog() {
        JComboBox<String> cmbEngine = new JComboBox<>(new String[]{
            "MySQL / MySQL Workbench (Port 3306)",
            "Microsoft SQL Server (Port 1433)",
            "Standalone Embedded Database (Zero Setup)"
        });

        int activeIdx = 0;
        if ("MSSQL".equalsIgnoreCase(DBConnection.getDbEngine())) activeIdx = 1;
        else if ("EMBEDDED".equalsIgnoreCase(DBConnection.getDbEngine())) activeIdx = 2;
        cmbEngine.setSelectedIndex(activeIdx);

        JTextField txtHost = new JTextField(DBConnection.getHost());
        JTextField txtPort = new JTextField(String.valueOf(DBConnection.getPort()));
        JTextField txtDb = new JTextField(DBConnection.getDatabase());
        JTextField txtUser = new JTextField(DBConnection.getUsername());
        JPasswordField txtPass = new JPasswordField(DBConnection.getPassword());

        cmbEngine.addActionListener(e -> {
            int sel = cmbEngine.getSelectedIndex();
            if (sel == 0) { // MySQL
                txtPort.setText("3306");
                txtUser.setText("root");
                txtPass.setText("");
            } else if (sel == 1) { // MSSQL
                txtPort.setText("1433");
                txtUser.setText("sa");
                txtPass.setText("YourPassword123!");
            }
        });

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.add(new JLabel("Database Engine:"));
        panel.add(cmbEngine);
        panel.add(new JLabel("Host / Server:"));
        panel.add(txtHost);
        panel.add(new JLabel("Port:"));
        panel.add(txtPort);
        panel.add(new JLabel("Database Name:"));
        panel.add(txtDb);
        panel.add(new JLabel("DB Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("DB Password:"));
        panel.add(txtPass);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Database Connection Settings",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            try {
                String chosenEngine = "MYSQL";
                if (cmbEngine.getSelectedIndex() == 1) chosenEngine = "MSSQL";
                else if (cmbEngine.getSelectedIndex() == 2) chosenEngine = "EMBEDDED";

                DBConnection.setDbEngine(chosenEngine);
                DBConnection.setHost(txtHost.getText().trim());
                DBConnection.setPort(Integer.parseInt(txtPort.getText().trim()));
                DBConnection.setDatabase(txtDb.getText().trim());
                DBConnection.setUsername(txtUser.getText().trim());
                DBConnection.setPassword(new String(txtPass.getPassword()));
                DBConnection.saveConfiguration();

                if (DBConnection.testConnection()) {
                    String msg = "Connected successfully to " + cmbEngine.getSelectedItem() + "!";
                    JOptionPane.showMessageDialog(this, msg, "Connection Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Saved settings, but connection test failed.\nPlease make sure the selected database is running and InternetBillingDB is created.",
                        "Connection Test Warning", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error in DB settings: " + ex.getMessage(),
                    "Configuration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new WelcomeFrame().setVisible(true));
    }
}
