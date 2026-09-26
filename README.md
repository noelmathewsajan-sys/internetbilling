# Internet Billing Management System

A desktop-based Internet Billing Management System built using **Java Swing**, developed in **Apache NetBeans**, and backed by **MySQL** via **JDBC**. Designed for Internet Service Providers (ISPs) to manage subscribers, bandwidth plans, monthly data usage, automated invoicing, and simulated customer payments.

---

## 📌 Technologies

- **Java (JDK 11 / 17 / 21+)**
- **Java Swing** (Desktop GUI with clean modern layout)
- **Apache NetBeans** (Ant-based project management)
- **JDBC** (Java Database Connectivity via MySQL Connector/J)
- **MySQL (v8.0+)**
- **MySQL Workbench** (Database administration & script execution)

---

## 🚀 Features

- **Administrator Login:** Secure authentication for ISP management staff.
- **User / Customer Login:** Dedicated subscriber portal for customers to view plans, track data usage, and pay bills.
- **Customer Management:** Comprehensive CRUD operations (Add, Update, Search, Delete) with validation and plan assignment.
- **Internet Plan Management:** Flexible configuration of broadband tiers, data caps, tariffs, extra usage charges per GB, equipment rentals, and late fines.
- **Monthly Usage Tracking:** Monthly subscriber quota and consumption tracking with automated extra-data calculation.
- **Automatic Bill Calculation:** Real-time billing engine computing total dues:
  $$\text{Total Bill} = \text{Plan Tariff} + \text{Equipment Rental} + (\text{Extra Data} \times \text{Extra Charge/GB}) + \text{Late Fine}$$
- **Bill Management:** Search and filter invoices by status (`ALL`, `PAID`, `UNPAID`, `OVERDUE`) and print/preview official invoices.
- **Simulated Online Payment:** Payment gateway simulation supporting UPI, Debit Card, Credit Card, and Net Banking with instant receipt generation (`PAY-XXXXX`).
- **Payment History:** Complete subscriber payment and transaction history logs.
- **Dashboard Statistics:** Live KPI counters displaying Total Customers, Active Customers, Total Invoices, Paid/Unpaid/Overdue Counts, and Collected Revenue.
- **MySQL Database Integration:** Complete relational schema with foreign key constraints, cascade rules, and automated schema generation.

---

## 🗄️ Database: `InternetBillingDB`

The application uses a relational schema with 6 interrelated tables:
- **`Admin`**: Stores administrative credentials and roles.
- **`Plans`**: Internet broadband packages, bandwidth speed tiers, data limits, and pricing.
- **`Customers`**: Subscriber account profiles linked to their selected plan.
- **`Usage`**: Monthly data consumption records per customer.
- **`Bills`**: Computed monthly invoices with line-item breakdowns.
- **`Payments`**: Transaction records linked to invoices and customers.

---

## 📁 Project Structure

```text
InternetBillingManagementSystem/
├── build.xml                                # Apache NetBeans Ant build configuration
├── compile.bat                              # Command-line compilation script
├── run.bat                                  # Command-line application launcher
├── test.bat                                 # Command-line test runner
├── manifest.mf                              # Java application manifest
├── database.sql                             # MySQL Database DDL & sample data script
├── mysql_database.sql                       # MySQL schema & sample records
├── mssql_database.sql                       # Microsoft SQL Server alternative script
├── db.properties.example                    # Template configuration file for database credentials
├── .gitignore                               # Excludes build artifacts, secrets, and IDE caches
├── .gitattributes                           # Git line ending and binary normalization
├── VIVA_GUIDE.md                            # Comprehensive B.Tech project viva & concept guide
├── nbproject/                               # NetBeans project configuration
│   ├── build-impl.xml
│   ├── genfiles.properties
│   ├── project.properties
│   └── project.xml
├── lib/                                     # Bundled JDBC driver libraries
│   ├── mysql-connector-j-8.3.0.jar          # MySQL JDBC Driver
│   ├── mssql-jdbc-12.6.1.jre11.jar          # MSSQL JDBC Driver
│   └── h2-2.2.224.jar                       # Embedded Database Driver (offline demo mode)
└── src/                                     # Java source code
    └── internetbilling/
        ├── Main.java                        # Application entry point
        ├── dao/                             # Data Access Objects (SQL queries)
        │   ├── AdminDAO.java
        │   ├── BillDAO.java
        │   ├── CustomerDAO.java
        │   ├── PaymentDAO.java
        │   ├── PlanDAO.java
        │   └── UsageDAO.java
        ├── database/                        # Database connectivity manager
        │   └── DBConnection.java
        ├── gui/                             # Java Swing UI frames and dialogs
        │   ├── AdminDashboard.java
        │   ├── AdminLogin.java
        │   ├── BillManagement.java
        │   ├── BillPrintDialog.java
        │   ├── CustomerManagement.java
        │   ├── MyBills.java
        │   ├── MyProfile.java
        │   ├── MyUsage.java
        │   ├── PaymentForm.java
        │   ├── PaymentHistory.java
        │   ├── PlanManagement.java
        │   ├── UIUtils.java
        │   ├── UsageManagement.java
        │   ├── UserDashboard.java
        │   ├── UserLogin.java
        │   └── WelcomeFrame.java
        ├── model/                           # Domain entity POJOs
        │   ├── Admin.java
        │   ├── Bill.java
        │   ├── Customer.java
        │   ├── Payment.java
        │   ├── Plan.java
        │   └── Usage.java
        ├── service/                         # Business logic services
        │   ├── BillingService.java
        │   ├── LoginService.java
        │   └── PaymentService.java
        └── test/                            # Verification tests
            ├── TestAdminUserEntry.java
            └── TestUIRendering.java
```

---

## ⚙️ Prerequisites

1. **Java Development Kit (JDK 11, 17, or 21+)** installed and configured on your system `PATH`.
2. **MySQL Server (v8.0+)** running locally or on a reachable network host.
3. **MySQL Workbench** (or phpMyAdmin / MySQL CLI) for executing the SQL setup script.
4. **Apache NetBeans IDE (v12.0+)** to open, build, and run the project.

---

## 🛠️ Step-by-Step Setup Guide

### Step 1: Configure MySQL and Run the SQL Script

1. Open **MySQL Workbench** and connect to your local MySQL Server instance.
2. In the top menu, go to **File** > **Open SQL Script...** (or press `Ctrl + Shift + O`).
3. Select the file [`database.sql`](database.sql) located in the project root directory.
4. Execute the entire script by clicking the **Execute (⚡ Lightning bolt)** icon or pressing `Ctrl + Shift + Enter`.
5. Verify in the Schemas pane that **`InternetBillingDB`** has been created with all 6 tables populated with initial sample data.

*Alternatively, via the MySQL command line:*
```bash
mysql -u root -p < database.sql
```

---

### Step 2: Configure the Database Connection

The application reads database configuration from a local file named `db.properties`.

1. In the project root directory, locate [`db.properties.example`](db.properties.example).
2. Create a copy of `db.properties.example` and name it `db.properties`:
   ```bash
   cp db.properties.example db.properties
   ```
3. Open `db.properties` in any text editor and update the credentials to match your MySQL setup:
   ```properties
   db.engine=MYSQL
   db.host=localhost
   db.port=3306
   db.name=InternetBillingDB
   db.user=root
   db.password=YOUR_MYSQL_PASSWORD
   db.trustServerCertificate=true
   db.encrypt=false
   db.integratedSecurity=false
   ```
4. **Security Note:** `db.properties` is listed in `.gitignore` so your personal database password will never be committed to Git.

> **Interactive GUI Alternative:** You can also configure the database directly from the application's Welcome screen by clicking the **"DB Config"** button, entering your password, and clicking **Save**.

---

### Step 3: Open the Project in Apache NetBeans

1. Launch **Apache NetBeans IDE**.
2. Go to **File** > **Open Project...** (or press `Ctrl + Shift + O`).
3. Browse to the directory:
   ```text
   InternetBillingManagementSystem
   ```
4. NetBeans will identify the coffee cup project icon indicating an Ant-based Java Application. Click **Open Project**.
5. The project will appear in the **Projects** explorer tab with all source packages and libraries linked.

---

### Step 4: Build and Run the Application

#### Option A: Inside Apache NetBeans
1. In the **Projects** panel, right-click **InternetBillingManagementSystem** and select **Clean and Build** (`Shift + F11`).
2. Once the build completes successfully, right-click the project and select **Run** (`F6`).
3. The **Internet Billing Management System** Welcome screen will open.

#### Option B: From Command Prompt / PowerShell (Without IDE)
The project includes convenient Windows batch scripts:
```cmd
# Compile the sources into build/classes and bundle the JAR
compile.bat

# Launch the application
run.bat

# Run the test suite
test.bat
```

---

## 🔑 Default Login Credentials (Sample Data)

### 🛡️ Administrator Account
| Role | Username | Password |
| :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` |

### 👤 Sample Customer Accounts
| Customer ID | Subscriber Name | Username | Password | Assigned Plan |
| :--- | :--- | :--- | :--- | :--- |
| `CUST101` | John Doe | `johndoe` | `cust123` | Standard Home (50 Mbps) |
| `CUST102` | Sarah Connor | `sarahc` | `cust123` | Premium Streamer (100 Mbps) |
| `CUST103` | Michael Scott | `michaels` | `cust123` | Basic Starter (25 Mbps) |
| `CUST104` | Bruce Wayne | `brucew` | `cust123` | Ultra Enterprise (300 Mbps) |
| `CUST105` | Peter Parker | `peterp` | `cust123` | Standard Home (50 Mbps) |

---

## 🔒 Security Practices

- **Zero Credential Leakage:** Sensitive local credentials in `db.properties` and environment files are strictly excluded via `.gitignore`.
- **Prepared Statements:** All database queries in the DAO layer use parameterized `PreparedStatement` to prevent SQL injection vulnerabilities.
- **Secure Password Fields:** Password inputs in Swing GUI use `JPasswordField` with memory clearing.

---

## 📄 License
This project is developed for educational and academic evaluation purposes.
