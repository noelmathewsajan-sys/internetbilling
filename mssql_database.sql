-- ============================================================================
-- INTERNET BILLING MANAGEMENT SYSTEM
-- Microsoft SQL Server Database Script (Alternative DBMS)
-- Database Name: InternetBillingDB
-- ============================================================================

-- Step 1: Create Database if not exists
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = N'InternetBillingDB')
BEGIN
    CREATE DATABASE InternetBillingDB;
    PRINT 'Database InternetBillingDB created successfully.';
END
ELSE
BEGIN
    PRINT 'Database InternetBillingDB already exists.';
END
GO

USE InternetBillingDB;
GO

-- Step 2: Drop Tables if they exist (in reverse foreign key order)
IF OBJECT_ID('Payments', 'U') IS NOT NULL DROP TABLE Payments;
IF OBJECT_ID('Bills', 'U') IS NOT NULL DROP TABLE Bills;
IF OBJECT_ID('Usage', 'U') IS NOT NULL DROP TABLE Usage;
IF OBJECT_ID('Customers', 'U') IS NOT NULL DROP TABLE Customers;
IF OBJECT_ID('Plans', 'U') IS NOT NULL DROP TABLE Plans;
IF OBJECT_ID('Admin', 'U') IS NOT NULL DROP TABLE Admin;
GO

-- ============================================================================
-- Step 3: Create Tables
-- ============================================================================

-- 1. Admin Table
CREATE TABLE Admin (
    admin_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);
GO

-- 2. Internet Plans Table
CREATE TABLE Plans (
    plan_id VARCHAR(30) PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    speed_tier VARCHAR(50) NOT NULL,
    monthly_tariff DECIMAL(10,2) NOT NULL CHECK (monthly_tariff >= 0),
    data_limit DECIMAL(10,2) NOT NULL CHECK (data_limit >= 0),
    extra_data_charge DECIMAL(10,2) NOT NULL CHECK (extra_data_charge >= 0),
    equipment_rental DECIMAL(10,2) NOT NULL CHECK (equipment_rental >= 0),
    late_payment_fine DECIMAL(10,2) NOT NULL CHECK (late_payment_fine >= 0)
);
GO

-- 3. Customers Table
CREATE TABLE Customers (
    customer_id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    plan_id VARCHAR(30) NOT NULL,
    connection_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT FK_Customers_Plans FOREIGN KEY (plan_id) REFERENCES Plans(plan_id) ON UPDATE CASCADE
);
GO

-- 4. Monthly Usage Table
CREATE TABLE Usage (
    usage_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id VARCHAR(30) NOT NULL,
    billing_month VARCHAR(20) NOT NULL,
    data_used DECIMAL(10,2) NOT NULL CHECK (data_used >= 0),
    extra_data DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (extra_data >= 0),
    CONSTRAINT FK_Usage_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT UQ_Usage_Customer_Month UNIQUE (customer_id, billing_month)
);
GO

-- 5. Bills Table
CREATE TABLE Bills (
    bill_id VARCHAR(30) PRIMARY KEY,
    customer_id VARCHAR(30) NOT NULL,
    usage_id INT NULL,
    billing_month VARCHAR(20) NOT NULL,
    plan_tariff DECIMAL(10,2) NOT NULL CHECK (plan_tariff >= 0),
    equipment_rental DECIMAL(10,2) NOT NULL CHECK (equipment_rental >= 0),
    extra_usage_charge DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (extra_usage_charge >= 0),
    late_fine DECIMAL(10,2) NOT NULL DEFAULT 0 CHECK (late_fine >= 0),
    total_amount DECIMAL(10,2) NOT NULL CHECK (total_amount >= 0),
    due_date DATE NOT NULL,
    bill_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' CHECK (bill_status IN ('PAID', 'UNPAID', 'OVERDUE')),
    CONSTRAINT FK_Bills_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id),
    CONSTRAINT FK_Bills_Usage FOREIGN KEY (usage_id) REFERENCES Usage(usage_id),
    CONSTRAINT UQ_Bills_Customer_Month UNIQUE (customer_id, billing_month)
);
GO

-- 6. Payments Table
CREATE TABLE Payments (
    payment_id VARCHAR(30) PRIMARY KEY,
    bill_id VARCHAR(30) NOT NULL,
    customer_id VARCHAR(30) NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT GETDATE(),
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payment_method VARCHAR(50) NOT NULL,
    payment_reference VARCHAR(100) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    CONSTRAINT FK_Payments_Bills FOREIGN KEY (bill_id) REFERENCES Bills(bill_id),
    CONSTRAINT FK_Payments_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);
GO

-- ============================================================================
-- Step 4: Insert Sample Data
-- ============================================================================

-- 1. Insert Administrator Account
INSERT INTO Admin (username, password) 
VALUES ('admin', 'admin123');
GO

-- 2. Insert Internet Plans
INSERT INTO Plans (plan_id, plan_name, speed_tier, monthly_tariff, data_limit, extra_data_charge, equipment_rental, late_payment_fine)
VALUES 
('PLAN01', 'Basic Starter', '25 Mbps', 399.00, 100.00, 10.00, 50.00, 50.00),
('PLAN02', 'Standard Home', '50 Mbps', 699.00, 250.00, 8.00, 75.00, 75.00),
('PLAN03', 'Premium Streamer', '100 Mbps', 999.00, 500.00, 5.00, 100.00, 100.00),
('PLAN04', 'Ultra Enterprise', '300 Mbps', 1499.00, 1000.00, 3.00, 150.00, 150.00);
GO

-- 3. Insert Customers (at least 5)
INSERT INTO Customers (customer_id, name, address, phone, email, username, password, plan_id, connection_date, status)
VALUES 
('CUST101', 'John Doe', '123 Elm Street, Cityville', '9876543210', 'john.doe@email.com', 'johndoe', 'cust123', 'PLAN02', '2026-01-15', 'ACTIVE'),
('CUST102', 'Sarah Connor', '456 Pine Avenue, Metropolis', '9876543211', 'sarah.c@email.com', 'sarahc', 'cust123', 'PLAN03', '2026-02-10', 'ACTIVE'),
('CUST103', 'Michael Scott', '1725 Slough Ave, Scranton', '9876543212', 'michael.s@email.com', 'michaels', 'cust123', 'PLAN01', '2026-03-05', 'ACTIVE'),
('CUST104', 'Bruce Wayne', '1007 Mountain Drive, Gotham', '9876543213', 'bruce.w@email.com', 'brucew', 'cust123', 'PLAN04', '2026-01-20', 'ACTIVE'),
('CUST105', 'Peter Parker', '20 Ingram Street, Queens', '9876543214', 'peter.p@email.com', 'peterp', 'cust123', 'PLAN02', '2026-04-12', 'INACTIVE');
GO

-- 4. Insert Sample Monthly Usage
INSERT INTO Usage (customer_id, billing_month, data_used, extra_data)
VALUES 
('CUST101', '2026-08', 220.00, 0.00),
('CUST101', '2026-09', 275.00, 25.00),
('CUST102', '2026-08', 480.00, 0.00),
('CUST102', '2026-09', 530.00, 30.00),
('CUST103', '2026-08', 95.00, 0.00),
('CUST103', '2026-09', 115.00, 15.00),
('CUST104', '2026-09', 650.00, 0.00);
GO

-- 5. Insert Sample Bills
INSERT INTO Bills (bill_id, customer_id, usage_id, billing_month, plan_tariff, equipment_rental, extra_usage_charge, late_fine, total_amount, due_date, bill_status)
VALUES 
('BILL-202608-101', 'CUST101', 1, '2026-08', 699.00, 75.00, 0.00, 0.00, 774.00, '2026-09-05', 'PAID'),
('BILL-202609-101', 'CUST101', 2, '2026-09', 699.00, 75.00, 200.00, 0.00, 974.00, '2026-10-05', 'UNPAID'),
('BILL-202608-102', 'CUST102', 3, '2026-08', 999.00, 100.00, 0.00, 0.00, 1099.00, '2026-09-05', 'PAID'),
('BILL-202609-102', 'CUST102', 4, '2026-09', 999.00, 100.00, 150.00, 0.00, 1249.00, '2026-10-05', 'UNPAID'),
('BILL-202608-103', 'CUST103', 5, '2026-08', 399.00, 50.00, 0.00, 50.00, 499.00, '2026-09-05', 'OVERDUE');
GO

-- 6. Insert Sample Payments
INSERT INTO Payments (payment_id, bill_id, customer_id, payment_date, amount, payment_method, payment_reference, payment_status)
VALUES 
('PAY-88001', 'BILL-202608-101', 'CUST101', '2026-09-02 11:30:00', 774.00, 'UPI', 'UPI-REF-99281729', 'SUCCESS'),
('PAY-88002', 'BILL-202608-102', 'CUST102', '2026-09-03 14:15:22', 1099.00, 'Credit Card', 'TXN-CC-48201948', 'SUCCESS');
GO

PRINT 'Sample data populated successfully!';
GO
