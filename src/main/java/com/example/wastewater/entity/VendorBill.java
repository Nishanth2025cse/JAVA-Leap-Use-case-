package com.example.wastewater.entity;

import com.example.wastewater.enums.BillStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vendor_bills", indexes = {
    @Index(name = "idx_bill_supplier", columnList = "supplier_id"),
    @Index(name = "idx_bill_status", columnList = "status")
})
public class VendorBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private ChemicalSupplier supplier;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @Column(nullable = false, unique = true)
    private String billNumber;

    private LocalDate billDate;

    private LocalDate dueDate;

    private Double totalAmount = 0.0;

    private Double paidAmount = 0.0;

    private Double balanceAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillStatus status = BillStatus.UNPAID;

    @PrePersist
    protected void onCreate() {
        if (this.billDate == null) {
            this.billDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            this.dueDate = this.billDate.plusDays(30);
        }
    }

    public VendorBill() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChemicalSupplier getSupplier() { return supplier; }
    public void setSupplier(ChemicalSupplier supplier) { this.supplier = supplier; }

    public PurchaseOrder getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrder purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(Double balanceAmount) { this.balanceAmount = balanceAmount; }

    public BillStatus getStatus() { return status; }
    public void setStatus(BillStatus status) { this.status = status; }
}
