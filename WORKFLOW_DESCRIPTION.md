# Comprehensive Project Workflow Description
## Municipal Smart Waste Water Treatment & Effluent Monitoring Platform

---

### 1. Executive Summary & Purpose
The **Municipal Smart Waste Water Treatment & Effluent Monitoring Platform** is an environmental infrastructure management system designed to automate wastewater discharge monitoring, compliance enforcement, chemical inventory control, customer billing with automated violation fines, double-entry accounting, and municipal financial reporting.

---

### 2. Detailed End-to-End System Workflow Description

#### Phase 1: Real-Time IoT COD Telemetry & Compliance Enforcement
1. **Discharge Measurement**: Sensors installed at industrial factory outfalls transmit Chemical Oxygen Demand (COD) telemetry measurements (in mg/L) to the system via REST APIs.
2. **Automated Threshold Evaluation**: The system compares the incoming COD reading against the factory's permitted threshold limit (or system default of 250 mg/L).
3. **Compliance Classification**:
   - **Compliant Discharge**: If the COD value is within the permitted threshold, the status is marked as `COMPLIANT`, and no fine is levied.
   - **Non-Compliant Discharge**: If the COD value exceeds the threshold, the status is marked as `NON_COMPLIANT`.
4. **Automated Fine Calculation & Violation Logging**:
   - The system calculates excess COD: `Excess COD = Measured COD - Permitted Limit`.
   - It calculates the fine amount: `Fine Amount = Excess COD * Configured Fine Rate ($50/unit)`.
   - It automatically logs an `EnvironmentalViolation` record with status `OPEN` and flags the factory's compliance status as `FLAGGED`.

---

#### Phase 2: Chemical Inventory Management & Procurement
1. **Stock Monitoring**: The system tracks chemical purification agent inventory levels (such as Chlorine Chemical Agent) and unit costs.
2. **Automated Low-Stock Alert**: Whenever chemical quantity falls below the reorder threshold, the system displays a prominent `LOW STOCK` badge alert on the dashboard.
3. **Procurement Purchase Orders**: The Utility Accountant creates a Purchase Order (PO) to procure chemical agents from registered suppliers.
4. **Automated Goods Receipt & Vendor Bill Generation**: When the shipment is delivered and the PO status is updated to `RECEIVED`:
   - Chemical inventory stock is automatically incremented.
   - A `VendorBill` for accounts payable is automatically generated with status `UNPAID`.

---

#### Phase 3: Service Orders & Customer Invoicing with Violation Surcharges
1. **Service Order Creation**: Service orders are created for industrial factories for monthly effluent processing services.
2. **Customer Invoice Generation**: When the accountant creates a Customer Invoice for a factory:
   - The system fetches the base treatment fee.
   - **Automated Fine Surcharge Lookup**: The system automatically queries the database for all open, unbilled `EnvironmentalViolation` records associated with that factory.
   - The total fine amounts are appended to the invoice as a **Compliance Surcharge**.
   - Tax (18%) is applied to the subtotal (Base Fee + Surcharge).
3. **Violation Status Transition**: All included violations automatically transition from status `OPEN` to `INVOICED`.
4. **Sales Journal Transaction**: The system automatically posts a balanced double-entry accounting journal transaction:
   - **Debit**: Accounts Receivable / Bank Account
   - **Credit**: Effluent Treatment Fees (Base + Tax)
   - **Credit**: Compliance Fines (Surcharge Amount)

---

#### Phase 4: Payment Processing & Reconciliation
1. **Customer Payments**:
   - When a factory submits a payment, the system updates `paidAmount` and recalculates `balanceAmount`.
   - When fully paid, the invoice status changes to `PAID`, and all included environmental violations update to `PAID`.
   - Posts a Bank Deposit Journal Entry (`Debit: Bank/Cash`, `Credit: Accounts Receivable`).
2. **Vendor Payments**:
   - When a vendor bill is paid, payable balances are updated and a purchase journal is posted.
   - The payment amount is automatically deducted from the relevant treatment facility's operational budget.

---

#### Phase 5: Facility Budgeting & System Reports
1. **Budget Performance Tracking**: The system tracks allocated operational budgets versus actual spending for each municipal treatment facility. It issues `WARNING` alerts at 80% budget utilization and `CRITICAL` alerts if spending exceeds allocation.
2. **Automated Audits & Reports**: The platform generates four real-time reports:
   - **Balance Sheet**: Assets, Liabilities, and Net Equity.
   - **Profit and Loss Statement**: Effluent Fees & Compliance Fines Income vs Chemical & Operational Expenses.
   - **Budget Performance Report**: Facility-wise budget allocations and spending percentages.
   - **Environmental Compliance Audit**: Total telemetry count, compliance rate, and factory-wise violation summaries.
