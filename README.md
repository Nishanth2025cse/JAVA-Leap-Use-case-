# Municipal Smart Waste Water Treatment & Effluent Monitoring Platform

An enterprise-grade, full-stack municipal environmental infrastructure management application built with **Java 21**, **Spring Boot 3**, **Spring Data JPA**, **MySQL**, and a modern **HTML5/CSS3/Vanilla JavaScript** dashboard UI.

---

## 🌟 Key Features

1. **IoT COD Telemetry Monitoring**:
   - Monitored real-time Chemical Oxygen Demand (COD) telemetry from industrial factories.
   - **Automated Compliance Verification**: Compares measured COD against factory-permitted thresholds.
   - **Automatic Fine Surcharge Calculation**: Calculates compliance fines for exceedances: `fine = (codValue - permittedCodLimit) * fineRate`.
   - Automatically records environmental violations and flags non-compliant factories.

2. **Automated Billing & Invoice Surcharges**:
   - Generates municipal customer invoices for industrial effluent processing.
   - **Automatic Violation Surcharge Inclusion**: Invoicing automatically fetches all unpaid/open environmental violation fines for a factory and incorporates them as a compliance surcharge into the customer invoice.

3. **Chemical Inventory & Stock Control**:
   - Manages chemical purification agent stock (e.g. Chlorine Agent).
   - **Automated Low-Stock Alerts**: Displays `LOW STOCK` alerts when inventory quantity drops below reorder levels.

4. **Procurement & Vendor Bills**:
   - Manages chemical suppliers and purchase orders.
   - **Automated Stock Increment**: Marking a purchase order as `RECEIVED` automatically updates chemical inventory stock and generates a corresponding Vendor Bill.
   - **Vendor Payments**: Updates bill balance, creates balanced double-entry accounting journals, and tracks actual treatment facility budget spending.

5. **Double-Entry General Ledger Accounting**:
   - Implements Chart of Accounts (Assets, Liabilities, Income, Expenses).
   - Maintains balanced journal entries ($Total\ Debit = Total\ Credit$) for all customer invoices, customer payments, vendor bills, and vendor payments.

6. **Facility Budget Allocations**:
   - Tracks allocated operational budgets vs actual spending for municipal treatment facilities.
   - Automated status warnings (`NORMAL`, `WARNING` at 80% utilization, `CRITICAL` when over budget).

7. **Financial & Environmental Reports**:
   - Balance Sheet
   - Profit and Loss Statement
   - Budget Utilization Performance Report
   - Environmental Compliance & Factory Violation Audit

---

## 🛠️ Technology Stack

- **Backend Framework**: Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Bean Validation)
- **Programming Language**: Java 21
- **Database**: MySQL 8.x (with optional H2 dev support)
- **Build Tool**: Maven 3.x
- **Frontend**: HTML5, Vanilla CSS3 (Custom Design System with Glassmorphism & Variables), Vanilla JavaScript (ES6+), Chart.js
- **API Architecture**: REST APIs with JSON DTOs

---

## 🔑 Demo Login Credentials

The application automatically seeds default demo users upon startup:

| Role | Username | Password | Access Capabilities |
| :--- | :--- | :--- | :--- |
| **Admin / Controller** | `admin` | `admin123` | Full access to Telemetry, Violations, Factories, Reports, Budgets & System Settings. |
| **Utility Accountant** | `accountant` | `acc123` | Access to Purchase Orders, Vendor Bills, Customer Invoices, Payments, Products & Suppliers. |

---

## 🚀 How to Run the Project in VS Code

### Prerequisites
1. **Java 21 SDK** installed (`java -version` should show 21.x)
2. **MySQL Server 8.x** running locally on port 3306

### Database Setup
1. Open MySQL Command Line or Workbench and create the database:
   ```sql
   CREATE DATABASE municipal_wastewater_db;
   ```
   *(Alternatively, execute the provided `database.sql` script)*.

2. Update database credentials in [`src/main/resources/application.properties`](file:///d:/SEM%203/JAVA/JAVA%20Project/use%20case%20project%20a/src/main/resources/application.properties):
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/municipal_wastewater_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Running the Application
Open a terminal in the project root directory in VS Code:

- **Windows PowerShell / Command Prompt**:
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```
  *or with system Maven:*
  ```powershell
  mvn spring-boot:run
  ```

- **Linux / macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```

Once started, open your browser and navigate to:
👉 **`http://localhost:8080`** or **`http://localhost:8080/login.html`**

---

## 📡 Key REST API Reference

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/auth/login` | `POST` | Authenticates user credentials |
| `/api/dashboard/summary` | `GET` | Fetches dashboard KPI summary metrics & tables |
| `/api/wastewater/plants` | `GET`, `POST` | Retrieve / register industrial factories |
| `/api/wastewater/plants/{id}` | `GET`, `PUT`, `DELETE` | Factory CRUD operations |
| `/api/wastewater/telemetry` | `GET`, `POST` | Submit discharge telemetry (triggers compliance check & fine calculation) |
| `/api/wastewater/violations` | `GET` | Fetch environmental violations |
| `/api/inventory/chemicals` | `GET` | Chemical stock inventory levels |
| `/api/inventory/chemicals/add` | `POST` | Increase chemical stock quantity |
| `/api/purchases/orders` | `GET`, `POST` | Procurement purchase orders |
| `/api/purchases/orders/{id}/status` | `PUT` | Update PO status (Receiving increases stock & generates Vendor Bill) |
| `/api/vendor-bills` | `GET`, `POST` | Chemical vendor bills & payables |
| `/api/customer-invoices` | `GET`, `POST` | Customer billing (auto-includes violation surcharge fines) |
| `/api/payments` | `GET`, `POST` | Record customer payment (updates invoice balance & journal) |
| `/api/vendor-payments` | `GET`, `POST` | Record vendor payment (updates bill balance & plant budget spending) |
| `/api/budgets` | `GET`, `POST` | Treatment facility budget allocations |
| `/api/reports/balance-sheet` | `GET` | Generates Balance Sheet report |
| `/api/reports/profit-loss` | `GET` | Generates Profit and Loss statement |
| `/api/reports/budget` | `GET` | Generates Budget Utilization report |
| `/api/reports/compliance` | `GET` | Generates Environmental Compliance report |

---

## 📁 Project Folder Structure

```
use case project a/
├── database.sql
├── pom.xml
├── README.md
├── application.properties -> src/main/resources/application.properties
└── src/
    └── main/
        ├── java/com/example/wastewater/
        │   ├── WasteWaterApplication.java
        │   ├── controller/         # REST Controllers
        │   ├── service/            # Core Business Logic & DataInitializer
        │   ├── repository/         # Spring Data JPA Repositories
        │   ├── entity/             # JPA Entities
        │   ├── dto/                # Data Transfer Objects
        │   ├── enums/              # Status & Type Enums
        │   └── exception/          # Exception Handlers
        └── resources/
            ├── application.properties
            └── static/             # Frontend Application
                ├── index.html
                ├── login.html
                ├── dashboard.html
                ├── factories.html
                ├── telemetry.html
                ├── violations.html
                ├── chemicals.html
                ├── purchase-orders.html
                ├── vendor-bills.html
                ├── service-orders.html
                ├── customer-invoices.html
                ├── payments.html
                ├── budgets.html
                ├── reports.html
                ├── settings.html
                ├── css/
                │   └── style.css
                └── js/
                    ├── main.js
                    ├── dashboard.js
                    ├── factories.js
                    ├── telemetry.js
                    ├── violations.js
                    ├── chemicals.js
                    ├── purchases.js
                    ├── invoices.js
                    ├── payments.js
                    ├── budgets.js
                    └── reports.js
```
