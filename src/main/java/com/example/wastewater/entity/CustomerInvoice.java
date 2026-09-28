package com.example.wastewater.entity;

import com.example.wastewater.enums.InvoiceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customer_invoices", indexes = {
    @Index(name = "idx_inv_factory", columnList = "factory_id"),
    @Index(name = "idx_inv_number", columnList = "invoiceNumber"),
    @Index(name = "idx_inv_status", columnList = "status")
})
public class CustomerInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "factory_id", nullable = false)
    private IndustrialFactory factory;

    @ManyToOne
    @JoinColumn(name = "service_order_id")
    private ServiceOrder serviceOrder;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private Double treatmentAmount = 0.0;

    private Double surchargeAmount = 0.0;

    private Double taxAmount = 0.0;

    private Double totalAmount = 0.0;

    private Double paidAmount = 0.0;

    private Double balanceAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status = InvoiceStatus.ISSUED;

    @PrePersist
    protected void onCreate() {
        if (this.invoiceDate == null) {
            this.invoiceDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            this.dueDate = this.invoiceDate.plusDays(30);
        }
    }

    public CustomerInvoice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public IndustrialFactory getFactory() { return factory; }
    public void setFactory(IndustrialFactory factory) { this.factory = factory; }

    public ServiceOrder getServiceOrder() { return serviceOrder; }
    public void setServiceOrder(ServiceOrder serviceOrder) { this.serviceOrder = serviceOrder; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getTreatmentAmount() { return treatmentAmount; }
    public void setTreatmentAmount(Double treatmentAmount) { this.treatmentAmount = treatmentAmount; }

    public Double getSurchargeAmount() { return surchargeAmount; }
    public void setSurchargeAmount(Double surchargeAmount) { this.surchargeAmount = surchargeAmount; }

    public Double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(Double taxAmount) { this.taxAmount = taxAmount; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public Double getBalanceAmount() { return balanceAmount; }
    public void setBalanceAmount(Double balanceAmount) { this.balanceAmount = balanceAmount; }

    public InvoiceStatus getStatus() { return status; }
    public void setStatus(InvoiceStatus status) { this.status = status; }
}
