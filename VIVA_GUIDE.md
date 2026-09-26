# B.Tech Project Demonstration & Viva-Voce Guide
## INTERNET BILLING MANAGEMENT SYSTEM

This guide provides technical explanations, design rationale, and answers to common examiner and viva-voce questions for your B.Tech Computer Science project evaluation.

---

## 1. Project Overview & Architecture

### What is the architectural pattern used?
The project uses the **Layered MVC (Model - View - Controller) with DAO (Data Access Object)** architecture:

* **Presentation Layer (GUI):** Java Swing forms (`JFrame`, `JDialog`, `JPanel`, `JTable`) providing intuitive interfaces for ISP Staff and Subscribers.
* **Service Layer (Business Logic):** Encapsulates core business rules (`BillingService`, `PaymentService`, `LoginService`), ensuring that calculation and validation logic is decoupled from UI components.
* **Data Access Layer (DAO):** Encapsulates SQL interaction (`CustomerDAO`, `PlanDAO`, `UsageDAO`, `BillDAO`, `PaymentDAO`, `AdminDAO`), abstracting database specifics from the business logic.
* **Model Layer (Entities):** POJOs (Plain Old Java Objects) encapsulating database records (`Customer`, `Plan`, `Usage`, `Bill`, `Payment`, `Admin`).
* **Database Connection Layer:** Centralized `DBConnection` class managing Microsoft SQL Server JDBC connection pooling and properties configuration.

---

## 2. Key Object-Oriented Programming (OOP) Concepts Demonstrated

1. **Encapsulation:**
   * Model classes have `private` fields accessible only via typed getter and setter methods.
   * Internal database credentials and query structures are hidden within DAO classes.
2. **Abstraction:**
   * UI components interact with high-level service interfaces (e.g. `billingService.generateBillForCustomer(...)`) without needing to know table joins or SQL query mechanics.
3. **Modularity & Separation of Concerns:**
   * GUI forms only handle event handling and display rendering.
   * DAOs only handle SQL execution.
   * Service classes only handle business formulas and workflows.
4. **Inheritance & Polymorphism:**
   * Swing classes inherit from `JFrame` and `JDialog`.
   * Custom table cell renderers override standard `DefaultTableCellRenderer`.
   * Standard interfaces such as `ActionListener` implement polymorphic event dispatching.

---

## 3. Database Design & Integrity

### What constraints are implemented in Microsoft SQL Server?
* **Primary Keys:** Unique identity across all tables (`admin_id`, `plan_id`, `customer_id`, `usage_id`, `bill_id`, `payment_id`).
* **Foreign Keys with Referential Integrity:**
  * `Customers(plan_id) -> Plans(plan_id)` with `ON UPDATE CASCADE`
  * `Usage(customer_id) -> Customers(customer_id)` with `ON DELETE CASCADE`
  * `Bills(customer_id) -> Customers(customer_id)`
  * `Bills(usage_id) -> Usage(usage_id)`
  * `Payments(bill_id) -> Bills(bill_id)`
  * `Payments(customer_id) -> Customers(customer_id)`
* **Unique Constraints:**
  * Prevent duplicate usernames (`UQ_Customers_Username`).
  * Prevent duplicate usage for the same customer in the same month (`UQ_Usage_Customer_Month`).
  * Prevent duplicate bills for the same customer in the same month (`UQ_Bills_Customer_Month`).
* **CHECK Constraints:**
  * `monthly_tariff >= 0`
  * `data_limit >= 0`
  * `extra_data_charge >= 0`
  * `equipment_rental >= 0`
  * `late_payment_fine >= 0`
  * `data_used >= 0`
  * `total_amount >= 0`
  * `bill_status IN ('PAID', 'UNPAID', 'OVERDUE')`

---

## 4. Automatic Billing Algorithm

### How does the system compute the monthly bill?
$$\text{Extra Data} = \max(0, \text{Data Used} - \text{Data Limit})$$
$$\text{Extra Usage Charge} = \text{Extra Data} \times \text{Extra Data Charge per GB}$$
$$\text{Late Payment Fine} = \begin{cases} \text{Plan Late Fine}, & \text{if prior bills are overdue} \\ 0, & \text{otherwise} \end{cases}$$
$$\text{Total Amount} = \text{Plan Tariff} + \text{Equipment Rental} + \text{Extra Usage Charge} + \text{Late Payment Fine}$$

* `BigDecimal` with `RoundingMode.HALF_UP` is used exclusively for financial amounts to avoid floating-point binary rounding errors (e.g., $0.1 + 0.2 \neq 0.3$ in double).
* Unique Bill ID format: `BILL-YYYYMM-XXXX`.

---

## 5. Security & Best Practices

1. **SQL Injection Prevention:**
   * All user-supplied parameters are bound via parameterized `PreparedStatement`. String concatenation in SQL statements is strictly avoided.
2. **Password Masking:**
   * `JPasswordField` is used for all password entries (`char[]` buffer rather than plain text strings).
3. **Multi-Tenant Data Isolation:**
   * Customer portal queries filter strictly by the logged-in session's `customerId`, preventing any subscriber from viewing or modifying another customer's bills, usage, or personal records.

---

## 6. Common Viva-Voce Questions & Model Answers

### Q1: Why did you choose Java Swing over JavaFX or web technologies?
> **Answer:** Java Swing is built directly into the standard Java Development Kit (JDK) without requiring additional external runtime modules. It is lightweight, cross-platform, robust for desktop enterprise administration software, and provides rich UI components like `JTable` and `GridBagLayout` suitable for offline ISP office billing stations.

### Q2: Why did you use `BigDecimal` instead of `double` or `float` for billing calculations?
> **Answer:** `double` and `float` are binary floating-point representations conforming to IEEE 754, which cannot accurately represent decimal fractions like 0.1 or 0.05. In billing and financial applications, cumulative floating-point errors lead to discrepancies. `BigDecimal` provides exact arbitrary-precision arithmetic with customizable rounding modes.

### Q3: What is the purpose of the DAO pattern in your project?
> **Answer:** The Data Access Object (DAO) pattern abstracts and encapsulates all access to the data source. It separates low-level data access APIs (JDBC, SQL queries, ResultSets) from high-level business services. This allows changing database schemas or switching database vendors without touching GUI forms or business rules.

### Q4: How do you prevent duplicate bills in the database?
> **Answer:** We enforce uniqueness at two levels:
> 1. **Application Level:** `BillingService` queries `billDAO.isBillExists(customerId, billingMonth)` before generating an invoice.
> 2. **Database Level:** A `UNIQUE` composite constraint `UQ_Bills_Customer_Month UNIQUE (customer_id, billing_month)` ensures the database engine rejects duplicate records even under concurrent requests.

### Q5: How is payment simulated without a real payment gateway?
> **Answer:** The application simulates a transaction workflow: validating payment method (UPI, Debit Card, Credit Card, Net Banking), generating a cryptographic/random transaction identifier (`PAY-XXXXX`), recording transaction timestamp, inserting a record into `Payments`, and atomically updating the bill's status from `UNPAID` to `PAID`.
