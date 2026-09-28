-- Municipal Smart Waste Water Treatment & Effluent Monitoring Platform Database Schema
-- Database Name: municipal_wastewater_db

CREATE DATABASE IF NOT EXISTS municipal_wastewater_db;
USE municipal_wastewater_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Industrial Factories Table
CREATE TABLE IF NOT EXISTS industrial_factories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factory_name VARCHAR(255) NOT NULL,
    registration_number VARCHAR(100) NOT NULL UNIQUE,
    contact_person VARCHAR(150),
    phone VARCHAR(50),
    email VARCHAR(100),
    address VARCHAR(255),
    city VARCHAR(100),
    industry_type VARCHAR(100),
    permitted_cod_limit DOUBLE NOT NULL DEFAULT 250.0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_factory_reg (registration_number),
    INDEX idx_factory_status (status)
);

-- 3. Chemical Suppliers Table
CREATE TABLE IF NOT EXISTS chemical_suppliers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(150),
    phone VARCHAR(50),
    email VARCHAR(100),
    address VARCHAR(255),
    tax_number VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    INDEX idx_supplier_status (status)
);

-- 4. Products & Services Master
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_code VARCHAR(100) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    unit VARCHAR(50),
    price DOUBLE NOT NULL DEFAULT 0.0,
    tax_percentage DOUBLE NOT NULL DEFAULT 18.0,
    active BOOLEAN DEFAULT TRUE
);

-- 5. Chart of Accounts
CREATE TABLE IF NOT EXISTS chart_of_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_code VARCHAR(50) NOT NULL UNIQUE,
    account_name VARCHAR(255) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    opening_balance DOUBLE DEFAULT 0.0,
    current_balance DOUBLE DEFAULT 0.0,
    active BOOLEAN DEFAULT TRUE
);

-- 6. Environmental Settings
CREATE TABLE IF NOT EXISTS environmental_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    default_cod_limit DOUBLE NOT NULL DEFAULT 250.0,
    fine_rate DOUBLE NOT NULL DEFAULT 50.0,
    currency VARCHAR(10) DEFAULT '$',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. Telemetry Table
CREATE TABLE IF NOT EXISTS telemetry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factory_id BIGINT NOT NULL,
    cod_value DOUBLE NOT NULL,
    permitted_cod_limit DOUBLE,
    measurement_date_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    location VARCHAR(150),
    status VARCHAR(50) NOT NULL,
    violation_amount DOUBLE DEFAULT 0.0,
    remarks VARCHAR(255),
    FOREIGN KEY (factory_id) REFERENCES industrial_factories(id) ON DELETE CASCADE,
    INDEX idx_telemetry_factory (factory_id),
    INDEX idx_telemetry_time (measurement_date_time),
    INDEX idx_telemetry_status (status)
);

-- 8. Environmental Violations Table
CREATE TABLE IF NOT EXISTS environmental_violations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factory_id BIGINT NOT NULL,
    telemetry_id BIGINT,
    cod_value DOUBLE,
    permitted_limit DOUBLE,
    excess_cod DOUBLE,
    fine_amount DOUBLE,
    violation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    remarks TEXT,
    FOREIGN KEY (factory_id) REFERENCES industrial_factories(id) ON DELETE CASCADE,
    FOREIGN KEY (telemetry_id) REFERENCES telemetry(id) ON DELETE SET NULL,
    INDEX idx_viol_factory (factory_id),
    INDEX idx_viol_status (status),
    INDEX idx_viol_date (violation_date)
);

-- 9. Chemical Inventory Table
CREATE TABLE IF NOT EXISTS chemical_inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity DOUBLE NOT NULL DEFAULT 0.0,
    reorder_level DOUBLE NOT NULL DEFAULT 50.0,
    unit_cost DOUBLE NOT NULL DEFAULT 0.0,
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 10. Purchase Orders & Items
CREATE TABLE IF NOT EXISTS purchase_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    order_date DATE,
    expected_delivery_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    total_amount DOUBLE DEFAULT 0.0,
    remarks VARCHAR(255),
    FOREIGN KEY (supplier_id) REFERENCES chemical_suppliers(id) ON DELETE CASCADE,
    INDEX idx_po_supplier (supplier_id),
    INDEX idx_po_status (status)
);

CREATE TABLE IF NOT EXISTS purchase_order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity DOUBLE NOT NULL,
    unit_price DOUBLE NOT NULL,
    total_price DOUBLE NOT NULL,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 11. Vendor Bills Table
CREATE TABLE IF NOT EXISTS vendor_bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    supplier_id BIGINT NOT NULL,
    purchase_order_id BIGINT,
    bill_number VARCHAR(100) NOT NULL UNIQUE,
    bill_date DATE,
    due_date DATE,
    total_amount DOUBLE DEFAULT 0.0,
    paid_amount DOUBLE DEFAULT 0.0,
    balance_amount DOUBLE DEFAULT 0.0,
    status VARCHAR(50) NOT NULL DEFAULT 'UNPAID',
    FOREIGN KEY (supplier_id) REFERENCES chemical_suppliers(id) ON DELETE CASCADE,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_orders(id) ON DELETE SET NULL,
    INDEX idx_bill_supplier (supplier_id),
    INDEX idx_bill_status (status)
);

-- 12. Service Orders Table
CREATE TABLE IF NOT EXISTS service_orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factory_id BIGINT NOT NULL,
    order_date DATE,
    service_period VARCHAR(100),
    service_type VARCHAR(150),
    quantity DOUBLE DEFAULT 1.0,
    unit_price DOUBLE DEFAULT 5000.0,
    total_amount DOUBLE DEFAULT 5000.0,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (factory_id) REFERENCES industrial_factories(id) ON DELETE CASCADE,
    INDEX idx_so_factory (factory_id),
    INDEX idx_so_status (status)
);

-- 13. Customer Invoices Table
CREATE TABLE IF NOT EXISTS customer_invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    factory_id BIGINT NOT NULL,
    service_order_id BIGINT,
    invoice_date DATE,
    due_date DATE,
    treatment_amount DOUBLE DEFAULT 0.0,
    surcharge_amount DOUBLE DEFAULT 0.0,
    tax_amount DOUBLE DEFAULT 0.0,
    total_amount DOUBLE DEFAULT 0.0,
    paid_amount DOUBLE DEFAULT 0.0,
    balance_amount DOUBLE DEFAULT 0.0,
    status VARCHAR(50) NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (factory_id) REFERENCES industrial_factories(id) ON DELETE CASCADE,
    FOREIGN KEY (service_order_id) REFERENCES service_orders(id) ON DELETE SET NULL,
    INDEX idx_inv_factory (factory_id),
    INDEX idx_inv_number (invoice_number),
    INDEX idx_inv_status (status)
);

-- 14. Customer Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_id BIGINT NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    remarks VARCHAR(255),
    FOREIGN KEY (invoice_id) REFERENCES customer_invoices(id) ON DELETE CASCADE
);

-- 15. Vendor Payments Table
CREATE TABLE IF NOT EXISTS vendor_payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vendor_bill_id BIGINT NOT NULL,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    amount DOUBLE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    remarks VARCHAR(255),
    FOREIGN KEY (vendor_bill_id) REFERENCES vendor_bills(id) ON DELETE CASCADE
);

-- 16. Analytic Accounts & Budgets
CREATE TABLE IF NOT EXISTS analytic_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    plant_location VARCHAR(150),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    analytic_account_id BIGINT NOT NULL,
    financial_year VARCHAR(50),
    allocated_amount DOUBLE DEFAULT 0.0,
    actual_amount DOUBLE DEFAULT 0.0,
    remaining_amount DOUBLE DEFAULT 0.0,
    status VARCHAR(50) DEFAULT 'NORMAL',
    FOREIGN KEY (analytic_account_id) REFERENCES analytic_accounts(id) ON DELETE CASCADE
);

-- 17. Accounting Journals & Entries
CREATE TABLE IF NOT EXISTS journals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journal_type VARCHAR(50) NOT NULL,
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    reference_number VARCHAR(100),
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS journal_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    journal_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    debit DOUBLE DEFAULT 0.0,
    credit DOUBLE DEFAULT 0.0,
    FOREIGN KEY (journal_id) REFERENCES journals(id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES chart_of_accounts(id) ON DELETE CASCADE
);
