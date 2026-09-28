# Comprehensive Project Documentation & Technical Report

## 📌 Project Title
**Municipal Smart Waste Water Treatment & Effluent Monitoring Platform**

---

## 📑 Executive Overview

The **Municipal Smart Waste Water Treatment & Effluent Monitoring Platform** is a full-stack municipal environmental infrastructure management application designed to track industrial chemical discharges, enforce environmental quality compliance, calculate violation fines automatically, manage chemical purification inventory, issue customer invoices with automated violation surcharges, record vendor bills and customer payments, maintain double-entry accounting ledgers, and provide financial/environmental audit reports.

---

## 🛠️ Technology Stack & System Architecture

### 1. Technology Architecture
- **Backend Framework**: Java 21 LTS with Spring Boot 3.2.5
- **Persistence & ORM**: Spring Data JPA / Hibernate ORM
- **Database Engine**: MySQL 8.x (`municipal_wastewater_db`)
- **API Protocol**: RESTful JSON APIs (`@RestController`, `@RequestMapping`)
- **Validation**: Spring Boot Starter Validation (`@NotNull`, `@NotBlank`, `@PositiveOrZero`)
- **Frontend Layer**: HTML5, Vanilla CSS3 (Custom Glassmorphism Design System), Vanilla JavaScript (ES6+), Chart.js
- **Build System**: Apache Maven (`pom.xml` & `mvnw.cmd`)

---

## 🔄 Complete Business Workflow

### Workflow 1: IoT COD Telemetry & Automated Compliance Enforcement
```
[Industrial Factory Outfall Sensor]
         │
         ▼
[POST /api/wastewater/telemetry] (COD Reading Submitted)
         │
         ▼
[TelemetryService] (Compares COD Value vs Permitted COD Limit)
         ├─────────────────────────────────────────┐
         │ (COD <= Limit)                          │ (COD > Limit)
         ▼                                         ▼
[Status: COMPLIANT]                    [Status: NON_COMPLIANT]
[Fine Surcharge: $0]                    [Calculate Fine: (COD - Limit) * FineRate]
                                        [Automatically Create Environmental Violation]
                                        [Flag Factory Status: FLAGGED]
```

#### Step-by-Step Logic:
1. An industrial factory transmits its Chemical Oxygen Demand (COD) measurement (in mg/L).
2. `TelemetryService` retrieves the factory's custom permitted COD limit (or defaults to the system default of 250 mg/L).
3. **If COD $\le$ Permitted Limit**:
   - Status is set to `COMPLIANT`.
   - Violation amount is set to `$0.00`.
4. **If COD $>$ Permitted Limit**:
   - Status is set to `NON_COMPLIANT`.
   - **Fine Calculation Formula**:
     $$\text{Excess COD} = \text{COD Measured} - \text{Permitted Limit}$$
     $$\text{Fine Amount} = \text{Excess COD} \times \text{Configured Fine Rate (\$50/unit)}$$
   - An `EnvironmentalViolation` record is automatically created with status `OPEN`.
   - Factory status is updated to `FLAGGED`.

---

### Workflow 2: Customer Invoicing & Automated Violation Fine Surcharge
```
[Select Industrial Factory for Billing]
         │
         ▼
[CustomerInvoiceService.createInvoice()]
         │
         ├──► Fetch Base Treatment Service Fee (e.g. $5,000)
         ├──► Query Database for OPEN Environmental Violations of this Factory
         ├──► Sum Violation Fine Amounts ──► Apply as Compliance Surcharge Amount
         ├──► Compute Subtotal = Base Fee + Surcharge
         ├──► Calculate 18% Tax = Subtotal * 0.18
         ├──► Total Invoice Amount = Subtotal + Tax
         │
         ▼
[Update Included Violations Status ──► INVOICED]
         │
         ▼
[Post Sales Journal Transaction]
   Debit:  Accounts Receivable / Bank ($Total)
   Credit: Effluent Treatment Fees ($Base + Tax)
   Credit: Compliance Fines ($Surcharge)
```

#### Step-by-Step Logic:
1. Accountant selects a factory to bill for wastewater purification services.
2. The system checks if there are any unbilled/unpaid `OPEN` environmental violations for that factory.
3. The sum of all open violation fines is automatically added to the invoice as `surchargeAmount`.
4. The system calculates subtotal and 18% tax:
   $$\text{Total Invoice Amount} = (\text{Base Treatment Amount} + \text{Surcharge Amount}) \times 1.18$$
5. All included violations have their status updated from `OPEN` to `INVOICED`.
6. A balanced double-entry Journal Transaction is automatically created.

---

### Workflow 3: Customer Payment Settlement
```
[Customer Payment Received (Bank/Cash)]
         │
         ▼
[POST /api/payments]
         │
         ▼
[PaymentService.recordPayment()]
         ├──► Update Invoice Paid Amount: PaidAmount = PaidAmount + PaymentAmount
         ├──► Recalculate Balance Due: BalanceAmount = TotalAmount - PaidAmount
         │
         ├─────────────────────────────────────────┐
         │ (Balance Due == 0)                      │ (Balance Due > 0)
         ▼                                         ▼
[Invoice Status: PAID]                  [Invoice Status: PARTIALLY_PAID]
[Update Violations Status ──► PAID]
         │
         ▼
[Post Bank Journal Transaction]
   Debit:  Bank / Cash Account ($PaymentAmount)
   Credit: Accounts Receivable ($PaymentAmount)
```

---

### Workflow 4: Chemical Procurement, Inventory & Vendor Bills
```
[Create Purchase Order for Chemical Agent]
         │
         ▼
[Status: DRAFT / APPROVED]
         │
         ▼ (Upon Goods Delivery)
[Update PO Status ──► RECEIVED]
         │
         ├──► Automatically Increase Chemical Inventory Stock Qty
         └──► Automatically Generate Vendor Bill (Status: UNPAID)
```

#### Step-by-Step Logic:
1. Utility Accountant creates a Purchase Order for Chlorine Chemical Agent from a supplier.
2. When the shipment arrives, the status is updated to `RECEIVED`.
3. `InventoryService.addStock()` automatically increases the chemical inventory quantity.
4. A `VendorBill` is automatically generated for accounts payable.
5. When the vendor bill is paid via `VendorPaymentService`:
   - Payable balance is reduced.
   - Plant operational budget actual spending is updated.
   - Purchase journal entry is posted.

---

### Workflow 5: Facility Budget Tracking & Warning Triggers
$$\text{Remaining Budget} = \text{Allocated Amount} - \text{Actual Spending}$$

- **`NORMAL`**: Actual Spending $< 80\%$ of Allocated Budget.
- **`WARNING`**: Actual Spending $\ge 80\%$ of Allocated Budget.
- **`CRITICAL`**: Actual Spending $>$ Allocated Budget.

---

## 👤 User Roles & Security Access Matrix

| Feature / Module | Admin / Controller | Utility Accountant |
| :--- | :---: | :---: |
| **Dashboard KPIs & Charts** | ✅ Full Access | ✅ Full Access |
| **Industrial Plants Management** | ✅ Manage / CRUD | 👁️ View Only |
| **IoT Telemetry Monitoring** | ✅ Transmit & View | 👁️ View Only |
| **Environmental Violations** | ✅ Audit & Manage | 👁️ View Only |
| **Chemical Stock Inventory** | ✅ View & Restock | ✅ View & Restock |
| **Suppliers & Procurement POs** | 👁️ View Only | ✅ Full CRUD & Receive |
| **Vendor Bills & Vendor Payments** | 👁️ View Only | ✅ Full Access |
| **Service Orders & Customer Invoices** | 👁️ View Only | ✅ Issue & Invoice |
| **Customer Payments Processing** | 👁️ View Only | ✅ Record Payments |
| **Chart of Accounts & Journals** | ✅ Full Audit | 👁️ View Only |
| **Treatment Facility Budgets** | ✅ Allocate & Monitor | 👁️ View Only |
| **Financial & Compliance Reports** | ✅ Full Audit | ✅ Full Audit |

---

## 🗄️ Database Entity Relationship Model (ERD)

```
[IndustrialFactory] 1 ──── N [TelemetryReading]
        │ 1                          │ 1
        │                            │
        ▼ N                          ▼ 1
[EnvironmentalViolation] ◄───────────┘
        │ N
        │ (Included in Billing)
        ▼
[CustomerInvoice] 1 ──── N [Payment]
        ▲ 1
        │
[ServiceOrder]

[ChemicalSupplier] 1 ──── N [PurchaseOrder] 1 ──── N [PurchaseOrderItem]
        │ 1                                                │ N
        ▼ N                                                ▼ 1
  [VendorBill] 1 ─── N [VendorPayment]                [Product]
                                                           │ 1
                                                           ▼ 1
                                                  [ChemicalInventory]

[AnalyticAccount] 1 ──── N [Budget]

[Journal] 1 ──── N [JournalEntry] N ──── 1 [Account]
```

---

## 📊 Summary of System APIs

- `POST /api/auth/login` – User authentication
- `GET /api/dashboard/summary` – Real-time KPI summary & charts
- `GET, POST, PUT, DELETE /api/wastewater/plants` – Industrial plants CRUD
- `GET, POST /api/wastewater/telemetry` – Submit telemetry & evaluate compliance
- `GET /api/wastewater/violations` – Fetch environmental violations log
- `GET, POST /api/inventory/chemicals` – Chemical inventory & restock
- `GET, POST, PUT /api/purchases/orders` – Chemical purchase orders & receipt
- `GET, POST /api/vendor-bills` – Vendor bills management
- `GET, POST /api/customer-invoices` – Customer invoices with fine surcharges
- `GET, POST /api/payments` – Customer payments settlement
- `GET, POST /api/vendor-payments` – Vendor bill payments
- `GET, POST /api/budgets` – Facility budget allocations
- `GET /api/reports/balance-sheet` – Balance Sheet statement
- `GET /api/reports/profit-loss` – Profit & Loss statement
- `GET /api/reports/budget` – Budget utilization report
- `GET /api/reports/compliance` – Environmental compliance audit report
