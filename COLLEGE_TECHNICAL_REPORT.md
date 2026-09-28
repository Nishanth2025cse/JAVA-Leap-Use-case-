# COLLEGE TECHNICAL REPORT

## 1. Title Page

**PROJECT TITLE:**  
Municipal Smart Waste Water Treatment & Effluent Monitoring Platform (AquaPulse)

**DOCUMENT TYPE:**  
Comprehensive Technical Report & System Documentation

**TECHNOLOGY STACK:**  
Java 21 LTS | Spring Boot 3.2.5 | Spring Data JPA | MySQL 8.x | HTML5 | CSS3 | Vanilla JavaScript

**DATE:**  
September 2026

---

## 2. Abstract

The **Municipal Smart Waste Water Treatment & Effluent Monitoring Platform (AquaPulse)** is an automated municipal operations application built to monitor industrial wastewater discharges, enforce environmental quality compliance, and manage municipal chemical inventory, billing, double-entry accounting, and facility budget workflows.

Industrial effluents often contain toxic concentrations of Chemical Oxygen Demand (COD). Manual monitoring of industrial discharges is inefficient and prone to reporting delays, allowing non-compliant factories to exceed permitted discharge limits without immediate penalty.

This platform connects IoT COD telemetry sensors directly to a Spring Boot backend. When measured COD exceeds a factory's permitted limit (e.g., > 250.0 mg/L), the system automatically classifies the discharge as `NON_COMPLIANT`, calculates a monetary fine based on a configured rate ($50/excess unit), creates an environmental violation, and flags the factory. Furthermore, when generating customer invoices for wastewater treatment, the system automatically fetches all open violation fines and appends them as a compliance surcharge. The platform also includes chemical stock inventory tracking with automated `LOW STOCK` alerts, purchase order receiving workflows that automatically increase inventory stock and generate vendor bills, double-entry accounting journal validation ($\text{Total Debits} = \text{Total Credits}$), facility budget monitoring, and real-time financial and compliance reporting.

---

## 3. Introduction and Problem Statement

### 3.1 Background
Municipal wastewater treatment authorities manage complex environmental infrastructure. They receive industrial effluents from chemical, textile, electroplating, and manufacturing plants. These effluents must be treated before discharge into natural water bodies. To prevent severe environmental damage, municipal authorities establish Chemical Oxygen Demand (COD) threshold limits for each industrial plant.

### 3.2 Problem Statement
Traditional municipal effluent management faces several critical challenges:
1. **Delayed Violation Detection**: COD exceedances are often detected long after discharge events occur, preventing real-time intervention.
2. **Disconnected Billing & Fines**: Environmental fines are recorded manually in separate systems, leading to uncollected surcharges and lost municipal revenue.
3. **Manual Inventory Tracking**: Chemical purification agents (such as Chlorine) are tracked manually, risking stockouts during critical purification cycles.
4. **Lack of Integrated Accounting & Budgeting**: Purchase orders, vendor bills, customer invoices, and facility operational budgets exist in siloed spreadsheets without double-entry general ledger integrity.

---

## 4. Project Objectives and Scope

### 4.1 Project Objectives
- **Automated Compliance Enforcement**: Automatically evaluate incoming COD telemetry against factory-specific limits and compute fine surcharges in real time.
- **Integrated Customer Billing**: Automatically append unbilled environmental violation fines as surcharges during customer invoice generation.
- **Automated Chemical Inventory Control**: Maintain chemical stock counts and trigger low-stock alerts when inventory drops below reorder thresholds.
- **Automated Procurement Workflow**: Increment chemical stock and generate vendor bills automatically when purchase orders are received.
- **Double-Entry General Ledger Integrity**: Ensure all invoices, payments, and bills generate balanced accounting journal entries ($\text{Debits} = \text{Credits}$).
- **Municipal Budget Auditing**: Monitor facility operational spending against allocated budgets and trigger warning thresholds.

### 4.2 Scope
- **In-Scope**: Web-based frontend UI, Spring Boot REST API, MySQL persistent storage, telemetry compliance calculation, invoice surcharge lookup, chemical stock restock/reduction, purchase order status transitions, double-entry journal posting, budget tracking, and 4 audit reports (Balance Sheet, Profit & Loss, Budget Utilization, Compliance Report).
- **Out-of-Scope**: Physical hardware sensor assembly, live hardware serial communication, and third-party bank payment gateway API integration.

---

## 5. Technology Stack

- **Programming Language**: Java 21 LTS (tested with JDK 21.0.10)
- **Backend Framework**: Spring Boot 3.2.5
- **Modules Used**: Spring Web (`spring-boot-starter-web`), Spring Data JPA (`spring-boot-starter-data-jpa`), Bean Validation (`spring-boot-starter-validation`)
- **Database Engine**: MySQL Server 8.x (Database name: `municipal_wastewater_db`)
- **Persistence Framework**: Hibernate ORM
- **Build & Dependency Tool**: Apache Maven 3.9.x
- **Frontend Technologies**: HTML5, Vanilla CSS3 (Custom Glassmorphism Design System), Vanilla JavaScript (ES6+ Fetch API), Chart.js
- **Default Application Port**: `8080`

---

## 6. System Architecture

The platform follows a layered, decoupled architecture ensuring clear separation of concerns:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        HTML5 / CSS3 / Vanilla JS                       │
│                           Browser User Interface                       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP / REST APIs (JSON)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Spring Boot REST Controllers                    │
│   (FactoryController, TelemetryController, CustomerInvoiceController)  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           Service Layer                                │
│ (TelemetryService, CustomerInvoiceService, PurchaseOrderService, etc.) │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       Spring Data JPA Repositories                     │
│         (IndustrialFactoryRepository, TelemetryReadingRepository)      │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                             Hibernate ORM                              │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ SQL Queries
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                          MySQL 8.x Database                            │
│                      (municipal_wastewater_db)                         │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Modules and Their Responsibilities

1. **Authentication & User Roles**: Simple role-based authentication supporting two primary roles: `ADMIN` (Environmental Controller) and `ACCOUNTANT` (Utility Accountant).
2. **Industrial Factories Master**: Manages factory registrations, industry types, locations, contact details, and permitted COD limits.
3. **IoT COD Telemetry**: Receives sensor readings (COD mg/L, location, measurement timestamp) and triggers compliance checks.
4. **Environmental Violations**: Records non-compliant exceedances, calculates monetary fines, and tracks violation billing states (`OPEN`, `INVOICED`, `PAID`, `RESOLVED`).
5. **Chemical Stock Inventory**: Tracks chemical purification agents (e.g., Chlorine Agent), quantities, unit costs, and reorder levels. Displays `LOW STOCK` alerts when quantity $\le$ reorder level.
6. **Chemical Suppliers / Vendors**: Manages chemical vendor contact info and tax identification numbers.
7. **Products & Services Master**: Catalog containing goods (Chlorine Chemical Agent) and services (Industrial Effluent Processing, Discharge Certificates).
8. **Purchase Orders**: Handles chemical procurement orders (`DRAFT`, `APPROVED`, `RECEIVED`, `CANCELLED`). Receiving an order automatically increases chemical stock and creates a Vendor Bill.
9. **Vendor Bills & Vendor Payments**: Manages accounts payable, processes vendor payments, updates bill balance, and records expenses against facility budgets.
10. **Service Orders & Customer Invoices**: Issues service orders and generates customer invoices with automated violation surcharge inclusion and 18% tax calculation.
11. **Customer Payments**: Processes customer invoice payments, updates invoice balance, and sets linked violation status to `PAID` upon full settlement.
12. **Chart of Accounts & Double-Entry Accounting**: Maintains accounts (Assets, Liabilities, Income, Expenses) and validates that total debits equal total credits for all journal entries.
13. **Analytic Accounts & Budgets**: Allocates operational budgets for municipal treatment facilities and tracks utilization status (`NORMAL`, `WARNING` at 80%, `CRITICAL` over budget).
14. **Dashboard**: Provides real-time top KPI cards, Chart.js trends, recent telemetry, violations, low stock items, and pending invoices.
15. **System Reports**: Generates Balance Sheet, Profit & Loss, Budget Performance, and Compliance Audit reports.

---

## 8. Database Entities and Relationships

- **`IndustrialFactory`** (1) ─── (N) **`TelemetryReading`**: A factory has many telemetry readings.
- **`IndustrialFactory`** (1) ─── (N) **`EnvironmentalViolation`**: A factory has many logged violations.
- **`TelemetryReading`** (1) ─── (0..1) **`EnvironmentalViolation`**: A non-compliant reading links to a violation.
- **`Product`** (1) ─── (1) **`ChemicalInventory`**: Each chemical product has an inventory record.
- **`ChemicalSupplier`** (1) ─── (N) **`PurchaseOrder`**: A supplier provides purchase orders.
- **`PurchaseOrder`** (1) ─── (N) **`PurchaseOrderItem`**: A PO contains order line items.
- **`PurchaseOrder`** (1) ─── (0..1) **`VendorBill`**: Receiving a PO generates a vendor bill.
- **`VendorBill`** (1) ─── (N) **`VendorPayment`**: A bill receives vendor payments.
- **`IndustrialFactory`** (1) ─── (N) **`CustomerInvoice`**: A factory receives customer invoices.
- **`CustomerInvoice`** (1) ─── (N) **`Payment`**: An invoice receives customer payments.
- **`AnalyticAccount`** (1) ─── (N) **`Budget`**: A treatment facility has budget allocations.
- **`Journal`** (1) ─── (N) **`JournalEntry`**: A journal entry contains balanced debit/credit lines linking to `Account`.

---

## 9. End-to-End Workflows

### Numbered End-to-End Demonstration Workflow

1. **Step 1 - Factory Registration**: Admin registers a new factory "GreenTech Manufacturing" with a permitted COD limit of **200.0 mg/L**.
2. **Step 2 - Telemetry Submission**: An IoT sensor submits a reading of **360.0 mg/L COD** for GreenTech Manufacturing.
3. **Step 3 - Automatic Violation & Fine Calculation**:
   - Measured COD (360.0) > Permitted Limit (200.0) $\rightarrow$ Marked `NON_COMPLIANT`.
   - Excess COD = $360.0 - 200.0 = 160.0\text{ mg/L}$.
   - Fine Amount = $160.0 \times \$50.00 = \$8,000.00$.
   - System automatically saves an `EnvironmentalViolation` with fine = **$8,000.00** and status = `OPEN`, and flags factory status as `FLAGGED`.
4. **Step 4 - Chemical Inventory Check**: Chemical inventory shows Chlorine Agent at 40 Kg (Reorder level 50 Kg). System displays **LOW STOCK** alert.
5. **Step 5 - Purchase Order & Automated Stocking**: Accountant creates PO for 100 Kg Chlorine Agent at $150/Kg ($15,000 total). Updating PO status to `RECEIVED`:
   - Stock automatically increases from 40 Kg to 140 Kg (Alert clears).
   - Vendor Bill `VB-2026-XXXX` for $15,000 is automatically generated.
6. **Step 6 - Customer Invoicing with Violation Surcharge**: Accountant generates an invoice for GreenTech Manufacturing for base treatment fee ($5,000.00):
   - System automatically fetches open violation fine ($8,000.00) as **Surcharge Amount**.
   - Subtotal = $\$5,000 + \$8,000 = \$13,000.00$.
   - Tax (18%) = $\$13,000 \times 0.18 = \$2,340.00$.
   - Total Amount = $\$15,340.00$.
   - Violation status changes from `OPEN` to `INVOICED`.
   - Sales journal entry is automatically posted ($Total\ Debit = Total\ Credit = \$15,340.00$).
7. **Step 7 - Customer Payment Processing**: Factory pays $15,340.00:
   - Invoice balance becomes $0.00 and status updates to `PAID`.
   - Linked violation updates to `PAID`.
   - Bank deposit journal entry is posted.
8. **Step 8 - Report Generation**: Admin views Balance Sheet, Profit & Loss, Budget Utilization, and Environmental Compliance Report, verifying live database totals.

---

## 10. Important COD Exceedance Business Rule

$$\text{COD Value} > \text{Permitted COD Limit} \implies \text{NON\_COMPLIANT, Auto Fine Levied}$$
$$\text{COD Value} \le \text{Permitted COD Limit} \implies \text{COMPLIANT, Fine} = \$0.00$$

- **Exceedance Rule**: A telemetry COD value strictly greater than the permitted limit triggers an automatic non-compliance status, fine calculation ($(\text{COD} - \text{Limit}) \times \text{Fine Rate}$), violation creation, and factory flagging.
- **Compliance Rule**: A COD value less than or equal to the permitted limit is marked compliant with $0 fine.
- **Configurability**: The system default limit (250 mg/L) and fine rate ($50/unit) can be configured dynamically via System Settings.

---

## 11. REST API Overview

| Endpoint Prefix | HTTP Methods | Description |
| :--- | :--- | :--- |
| `/api/auth/login` | `POST` | User authentication & role retrieval |
| `/api/dashboard/summary` | `GET` | Fetches dashboard KPI card totals & tables |
| `/api/wastewater/plants` | `GET`, `POST`, `PUT`, `DELETE` | Industrial factories CRUD & search |
| `/api/wastewater/telemetry` | `GET`, `POST` | Submit telemetry & evaluate compliance |
| `/api/wastewater/violations` | `GET`, `PUT` | View violations log & update status |
| `/api/inventory/chemicals` | `GET`, `POST`, `PUT` | Stock levels, restock & reduce stock |
| `/api/suppliers` | `GET`, `POST`, `PUT`, `DELETE` | Supplier vendor registry CRUD |
| `/api/products` | `GET`, `POST`, `PUT`, `DELETE` | Goods and services master catalog |
| `/api/purchases/orders` | `GET`, `POST`, `PUT` | Purchase orders & receiving workflow |
| `/api/vendor-bills` | `GET`, `POST` | Accounts payable vendor bills |
| `/api/vendor-payments` | `GET`, `POST` | Record vendor bill payments |
| `/api/service-orders` | `GET`, `POST`, `PUT` | Industrial service orders |
| `/api/customer-invoices` | `GET`, `POST` | Customer billing with fine surcharges |
| `/api/payments` | `GET`, `POST` | Record customer invoice payments |
| `/api/accounts` | `GET`, `POST`, `PUT` | Chart of accounts management |
| `/api/journals` | `GET`, `POST` | Accounting journal transactions |
| `/api/analytic-accounts` | `GET`, `POST`, `PUT` | Treatment plant facilities |
| `/api/budgets` | `GET`, `POST`, `PUT` | Plant budget allocations |
| `/api/reports/...` | `GET` | Financial reports (balance-sheet, profit-loss, budget, compliance) |
| `/api/wastewater/settings` | `GET`, `PUT` | Environmental threshold & fine rate settings |

---

## 12. Security, Validation, and Error Handling

- **Authentication**: Simple username/password authentication with session storage in browser `localStorage`. Role enforcement restricts admin-only vs accountant-only actions.
- **Bean Validation**: Inputs are validated using `@NotNull`, `@NotBlank`, and `@PositiveOrZero` annotations on JPA entity fields and REST DTOs.
- **Global Exception Handling**: `@RestControllerAdvice` handles `ResourceNotFoundException` (404), `BadRequestException` (400), validation errors, and uncaught exceptions, returning clean JSON error responses.

---

## 13. Installation and Configuration

### 13.1 Requirements
- JDK 21 installed (`java -version` returns 21)
- MySQL Server 8.x running on port 3306
- Apache Maven 3.x

### 13.2 Database Creation
```sql
CREATE DATABASE municipal_wastewater_db;
```

### 13.3 Configuration
In `src/main/resources/application.properties`:
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/municipal_wastewater_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 13.4 Running the Application
From the workspace directory in VS Code:
```powershell
.\mvnw.cmd spring-boot:run
```
Open browser at `http://localhost:8080/login.html`.

---

## 14. Test and Verification Results

- **Backend Compilation**: Compiled 103 Java source files with `BUILD SUCCESS` using JDK 21.
- **Maven Packaging**: Executed `mvn clean package -DskipTests` producing executable artifact `target/municipal-wastewater-platform-1.0.0.jar`.
- **Database Connection**: Successfully connected to local MySQL Server on port 3306; Hibernate automatically created all 20 database tables.
- **Automated Startup Seeding**: `DataInitializer` successfully seeded demo users (`admin`, `accountant`), chart of accounts, products, suppliers, factories, low-stock inventory, budgets, and sample telemetry.
- **Live Workflow Validation**:
  - Telemetry exceeding limit (360 COD vs 200 limit) successfully calculated fine ($8,000), created violation, and flagged factory.
  - Customer invoice creation automatically fetched the $8,000 fine and added it as surcharge, applying 18% tax and posting sales journal entries.
  - Recording customer payment successfully updated invoice balance to $0.00 and marked linked violations as `PAID`.
  - Updating PO to `RECEIVED` successfully increased chemical inventory stock and generated a vendor bill.

---

## 15. Limitations and Future Enhancements

### 15.1 Limitations
- **Demonstration / MVP Scope**: Built as an academic demonstration application.
- **Simplified Authentication**: Uses basic role session state in browser local storage rather than OAuth2 / JWT stateless token infrastructure.
- **Physical Sensor Hardware**: Telemetry is simulated via REST API calls rather than direct MQTT / Modbus hardware sensor serial interfaces.

### 15.2 Future Enhancements
- Integration of MQTT broker for direct IoT hardware sensor ingestion.
- Implementation of JWT token-based stateless authentication and Spring Security filters.
- Integration with SMS / Email gateway for immediate violation alert notifications to factory plant managers.

---

## 16. Conclusion

The **Municipal Smart Waste Water Treatment & Effluent Monitoring Platform (AquaPulse)** successfully demonstrates an automated, full-stack municipal environmental management system. By linking IoT COD telemetry with automated fine calculations, customer billing surcharges, chemical stock inventory management, double-entry accounting ledgers, and facility budget auditing, the system eliminates manual reporting delays and ensures municipal environmental compliance and financial integrity.
