package internetbilling.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Centralized Database Connection Manager.
 * Primary: Microsoft SQL Server (localhost:1433 / InternetBillingDB).
 * Fallback: Standalone Embedded Database Mode (zero installation required for
 * instant testing & evaluation).
 */
public class DBConnection {

    private static final String CONFIG_FILE = "db.properties";
    private static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String MSSQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String H2_DRIVER = "org.h2.Driver";

    // Mode: "MYSQL", "MSSQL", or "EMBEDDED"
    private static String dbEngine = "MYSQL";

    // Configuration
    private static String host = "localhost";
    private static int port = 3306;
    private static String database = "InternetBillingDB";
    private static String username = "root";
    private static String password = "";
    private static boolean trustServerCertificate = true;
    private static boolean encrypt = false;
    private static boolean integratedSecurity = false;

    // Embedded DB initialized flag
    private static boolean embeddedInitialized = false;

    static {
        loadConfiguration();
        try {
            Class.forName(MSSQL_DRIVER);
        } catch (ClassNotFoundException ignored) {
        }
        try {
            Class.forName(H2_DRIVER);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static File getConfigFile() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            File sub = new File("InternetBillingManagementSystem", CONFIG_FILE);
            if (sub.exists()) {
                return sub;
            }
        }
        return file;
    }

    /**
     * Loads database configuration from db.properties if it exists.
     */
    public static void loadConfiguration() {
        File file = getConfigFile();
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                dbEngine = props.getProperty("db.engine", dbEngine);
                host = props.getProperty("db.host", host);
                port = Integer.parseInt(props.getProperty("db.port", String.valueOf(port)));
                database = props.getProperty("db.name", database);
                username = props.getProperty("db.user", username);
                password = props.getProperty("db.password", password);
                trustServerCertificate = Boolean.parseBoolean(props.getProperty("db.trustServerCertificate", "true"));
                encrypt = Boolean.parseBoolean(props.getProperty("db.encrypt", "false"));
                integratedSecurity = Boolean.parseBoolean(props.getProperty("db.integratedSecurity", "false"));
            } catch (Exception e) {
                System.err.println("Warning: Could not read db.properties, using defaults: " + e.getMessage());
            }
        } else {
            saveConfiguration();
        }
    }

    /**
     * Saves database configuration to db.properties.
     */
    public static void saveConfiguration() {
        Properties props = new Properties();
        props.setProperty("db.engine", dbEngine);
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", username);
        props.setProperty("db.password", password);
        props.setProperty("db.trustServerCertificate", String.valueOf(trustServerCertificate));
        props.setProperty("db.encrypt", String.valueOf(encrypt));
        props.setProperty("db.integratedSecurity", String.valueOf(integratedSecurity));

        File target = getConfigFile();
        try (FileOutputStream fos = new FileOutputStream(target)) {
            props.store(fos, "Database Connection Configuration for Internet Billing Management System");
        } catch (IOException e) {
            System.err.println("Warning: Could not save db.properties: " + e.getMessage());
        }
    }

    /**
     * Returns the appropriate table identifier for 'Usage' to prevent MySQL keyword conflict.
     */
    public static String getUsageTable() {
        if ("MSSQL".equalsIgnoreCase(dbEngine)) {
            return "[Usage]";
        }
        return "`Usage`";
    }

    /**
     * Constructs JDBC connection URL for MySQL.
     */
    public static String getMySQLConnectionString() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database +
               "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&connectTimeout=3000";
    }

    /**
     * Constructs JDBC connection URL for Microsoft SQL Server.
     */
    public static String getMSSQLConnectionString() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:sqlserver://").append(host).append(":").append(port).append(";");
        sb.append("databaseName=").append(database).append(";");
        sb.append("trustServerCertificate=").append(trustServerCertificate).append(";");
        sb.append("encrypt=").append(encrypt).append(";");
        sb.append("loginTimeout=3;");
        if (integratedSecurity) {
            sb.append("integratedSecurity=true;");
        }
        return sb.toString();
    }

    /**
     * Constructs JDBC connection URL for Embedded Standalone Database.
     */
    public static String getEmbeddedConnectionString() {
        return "jdbc:h2:./InternetBillingDB;MODE=MSSQLServer;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    }

    /**
     * Gets a database connection according to the configured engine, with automatic
     * fallback prompt.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if ("MYSQL".equalsIgnoreCase(dbEngine)) {
            try {
                Class.forName(MYSQL_DRIVER);
                return DriverManager.getConnection(getMySQLConnectionString(), username, password);
            } catch (Exception ex) {
                return handleConnectionFailure("MySQL Server (" + host + ":" + port + ")", ex);
            }
        } else if ("MSSQL".equalsIgnoreCase(dbEngine)) {
            try {
                Class.forName(MSSQL_DRIVER);
                if (integratedSecurity) {
                    return DriverManager.getConnection(getMSSQLConnectionString());
                } else {
                    return DriverManager.getConnection(getMSSQLConnectionString(), username, password);
                }
            } catch (Exception ex) {
                return handleConnectionFailure("Microsoft SQL Server (" + host + ":" + port + ")", ex);
            }
        } else {
            return getEmbeddedConnection();
        }
    }

    private static Connection handleConnectionFailure(String serverName, Exception ex) throws SQLException {
        int choice = JOptionPane.showConfirmDialog(
                null,
                "Could not connect to " + serverName + ".\n\n" +
                "Reason: " + ex.getMessage() + "\n\n" +
                "Would you like to switch to the Standalone Embedded Database Mode?\n" +
                "(It includes all tables and sample data preloaded with zero setup needed)",
                "Database Connection Notice",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            dbEngine = "EMBEDDED";
            saveConfiguration();
            return getEmbeddedConnection();
        } else {
            throw new SQLException(serverName + " connection failed: " + ex.getMessage(), ex);
        }
    }

    private static Connection getEmbeddedConnection() throws SQLException {
        try {
            Class.forName(H2_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Embedded Database Driver not found: " + H2_DRIVER, e);
        }

        Connection conn = DriverManager.getConnection(getEmbeddedConnectionString(), "sa", "");
        if (!embeddedInitialized) {
            initializeEmbeddedDatabase(conn);
            embeddedInitialized = true;
        }
        return conn;
    }

    /**
     * Populates embedded database tables and sample data.
     */
    public static void initializeEmbeddedDatabase(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // 1. Admin
            stmt.execute("CREATE TABLE IF NOT EXISTS Admin (" +
                    "admin_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(100) NOT NULL)");

            // 2. Plans
            stmt.execute("CREATE TABLE IF NOT EXISTS Plans (" +
                    "plan_id VARCHAR(30) PRIMARY KEY, " +
                    "plan_name VARCHAR(100) NOT NULL, " +
                    "speed_tier VARCHAR(50) NOT NULL, " +
                    "monthly_tariff DECIMAL(10,2) NOT NULL, " +
                    "data_limit DECIMAL(10,2) NOT NULL, " +
                    "extra_data_charge DECIMAL(10,2) NOT NULL, " +
                    "equipment_rental DECIMAL(10,2) NOT NULL, " +
                    "late_payment_fine DECIMAL(10,2) NOT NULL)");

            // 3. Customers
            stmt.execute("CREATE TABLE IF NOT EXISTS Customers (" +
                    "customer_id VARCHAR(30) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "address VARCHAR(255) NOT NULL, " +
                    "phone VARCHAR(20) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "plan_id VARCHAR(30) NOT NULL, " +
                    "connection_date DATE NOT NULL, " +
                    "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE')");

            // 4. Usage
            stmt.execute("CREATE TABLE IF NOT EXISTS Usage (" +
                    "usage_id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "customer_id VARCHAR(30) NOT NULL, " +
                    "billing_month VARCHAR(20) NOT NULL, " +
                    "data_used DECIMAL(10,2) NOT NULL, " +
                    "extra_data DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                    "CONSTRAINT UQ_Usage_Customer_Month UNIQUE (customer_id, billing_month))");

            // 5. Bills
            stmt.execute("CREATE TABLE IF NOT EXISTS Bills (" +
                    "bill_id VARCHAR(30) PRIMARY KEY, " +
                    "customer_id VARCHAR(30) NOT NULL, " +
                    "usage_id INT NULL, " +
                    "billing_month VARCHAR(20) NOT NULL, " +
                    "plan_tariff DECIMAL(10,2) NOT NULL, " +
                    "equipment_rental DECIMAL(10,2) NOT NULL, " +
                    "extra_usage_charge DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                    "late_fine DECIMAL(10,2) NOT NULL DEFAULT 0, " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "due_date DATE NOT NULL, " +
                    "bill_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', " +
                    "CONSTRAINT UQ_Bills_Customer_Month UNIQUE (customer_id, billing_month))");

            // 6. Payments
            stmt.execute("CREATE TABLE IF NOT EXISTS Payments (" +
                    "payment_id VARCHAR(30) PRIMARY KEY, " +
                    "bill_id VARCHAR(30) NOT NULL, " +
                    "customer_id VARCHAR(30) NOT NULL, " +
                    "payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "amount DECIMAL(10,2) NOT NULL, " +
                    "payment_method VARCHAR(50) NOT NULL, " +
                    "payment_reference VARCHAR(100) NOT NULL, " +
                    "payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS')");

            // Check if admin exists; if not, populate sample data
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Admin")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO Admin (username, password) VALUES ('admin', 'admin123')");

                    stmt.execute("INSERT INTO Plans VALUES " +
                            "('PLAN01', 'Basic Starter', '25 Mbps', 399.00, 100.00, 10.00, 50.00, 50.00), " +
                            "('PLAN02', 'Standard Home', '50 Mbps', 699.00, 250.00, 8.00, 75.00, 75.00), " +
                            "('PLAN03', 'Premium Streamer', '100 Mbps', 999.00, 500.00, 5.00, 100.00, 100.00), " +
                            "('PLAN04', 'Ultra Enterprise', '300 Mbps', 1499.00, 1000.00, 3.00, 150.00, 150.00)");

                    stmt.execute("INSERT INTO Customers VALUES " +
                            "('CUST101', 'John Doe', '123 Elm Street, Cityville', '9876543210', 'john.doe@email.com', 'johndoe', 'cust123', 'PLAN02', '2026-01-15', 'ACTIVE'), "
                            +
                            "('CUST102', 'Sarah Connor', '456 Pine Avenue, Metropolis', '9876543211', 'sarah.c@email.com', 'sarahc', 'cust123', 'PLAN03', '2026-02-10', 'ACTIVE'), "
                            +
                            "('CUST103', 'Michael Scott', '1725 Slough Ave, Scranton', '9876543212', 'michael.s@email.com', 'michaels', 'cust123', 'PLAN01', '2026-03-05', 'ACTIVE'), "
                            +
                            "('CUST104', 'Bruce Wayne', '1007 Mountain Drive, Gotham', '9876543213', 'bruce.w@email.com', 'brucew', 'cust123', 'PLAN04', '2026-01-20', 'ACTIVE'), "
                            +
                            "('CUST105', 'Peter Parker', '20 Ingram Street, Queens', '9876543214', 'peter.p@email.com', 'peterp', 'cust123', 'PLAN02', '2026-04-12', 'INACTIVE')");

                    stmt.execute("INSERT INTO Usage (customer_id, billing_month, data_used, extra_data) VALUES " +
                            "('CUST101', '2026-08', 220.00, 0.00), " +
                            "('CUST101', '2026-09', 275.00, 25.00), " +
                            "('CUST102', '2026-08', 480.00, 0.00), " +
                            "('CUST102', '2026-09', 530.00, 30.00), " +
                            "('CUST103', '2026-08', 95.00, 0.00), " +
                            "('CUST103', '2026-09', 115.00, 15.00), " +
                            "('CUST104', '2026-09', 650.00, 0.00)");

                    stmt.execute(
                            "INSERT INTO Bills (bill_id, customer_id, usage_id, billing_month, plan_tariff, equipment_rental, extra_usage_charge, late_fine, total_amount, due_date, bill_status) VALUES "
                                    +
                                    "('BILL-202608-101', 'CUST101', 1, '2026-08', 699.00, 75.00, 0.00, 0.00, 774.00, '2026-09-05', 'PAID'), "
                                    +
                                    "('BILL-202609-101', 'CUST101', 2, '2026-09', 699.00, 75.00, 200.00, 0.00, 974.00, '2026-10-05', 'UNPAID'), "
                                    +
                                    "('BILL-202608-102', 'CUST102', 3, '2026-08', 999.00, 100.00, 0.00, 0.00, 1099.00, '2026-09-05', 'PAID'), "
                                    +
                                    "('BILL-202609-102', 'CUST102', 4, '2026-09', 999.00, 100.00, 150.00, 0.00, 1249.00, '2026-10-05', 'UNPAID'), "
                                    +
                                    "('BILL-202608-103', 'CUST103', 5, '2026-08', 399.00, 50.00, 0.00, 50.00, 499.00, '2026-09-05', 'OVERDUE')");

                    stmt.execute(
                            "INSERT INTO Payments (payment_id, bill_id, customer_id, payment_date, amount, payment_method, payment_reference, payment_status) VALUES "
                                    +
                                    "('PAY-88001', 'BILL-202608-101', 'CUST101', CURRENT_TIMESTAMP, 774.00, 'UPI', 'UPI-REF-99281729', 'SUCCESS'), "
                                    +
                                    "('PAY-88002', 'BILL-202608-102', 'CUST102', CURRENT_TIMESTAMP, 1099.00, 'Credit Card', 'TXN-CC-48201948', 'SUCCESS')");
                }
            }
        } catch (SQLException e) {
            System.err.println("Embedded DB init notice: " + e.getMessage());
        }
    }

    /**
     * Tests the database connection.
     * 
     * @return true if successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Getters and Setters for configuration
    public static String getDbEngine() {
        return dbEngine;
    }

    public static void setDbEngine(String engine) {
        dbEngine = engine;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String h) {
        host = h;
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int p) {
        port = p;
    }

    public static String getDatabase() {
        return database;
    }

    public static void setDatabase(String db) {
        database = db;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String p) {
        password = p;
    }

    public static boolean isTrustServerCertificate() {
        return trustServerCertificate;
    }

    public static void setTrustServerCertificate(boolean t) {
        trustServerCertificate = t;
    }

    public static boolean isEncrypt() {
        return encrypt;
    }

    public static void setEncrypt(boolean e) {
        encrypt = e;
    }

    public static boolean isIntegratedSecurity() {
        return integratedSecurity;
    }

    public static void setIntegratedSecurity(boolean i) {
        integratedSecurity = i;
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("Internet Billing Management System - DB Test Utility");
        System.out.println("==================================================");
        loadConfiguration();
        System.out.println("Configured Engine: " + dbEngine);
        System.out.println("Target: " + host + ":" + port + "/" + database);
        System.out.println("User: " + username);
        System.out.println("Attempting connection...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println(">>> Connection SUCCESSFUL! <<<");
                System.out.println("Database Product: " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                try (Statement stmt = conn.createStatement()) {
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Customers")) {
                        if (rs.next()) {
                            System.out.println("Customer records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Plans")) {
                        if (rs.next()) {
                            System.out.println("Plan records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + getUsageTable())) {
                        if (rs.next()) {
                            System.out.println("Usage records found: " + rs.getInt(1));
                        }
                    }
                    try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Bills")) {
                        if (rs.next()) {
                            System.out.println("Bill records found: " + rs.getInt(1));
                        }
                    }
                }
            } else {
                System.err.println("Connection was null or closed.");
            }
        } catch (Exception e) {
            System.err.println("Connection FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("==================================================");
    }
}

