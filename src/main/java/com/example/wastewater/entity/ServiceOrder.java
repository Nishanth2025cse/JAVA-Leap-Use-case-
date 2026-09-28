package com.example.wastewater.entity;

import com.example.wastewater.enums.ServiceOrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "service_orders", indexes = {
    @Index(name = "idx_so_factory", columnList = "factory_id"),
    @Index(name = "idx_so_status", columnList = "status")
})
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "factory_id", nullable = false)
    private IndustrialFactory factory;

    private LocalDate orderDate;

    private String servicePeriod;

    private String serviceType = "Industrial Effluent Processing";

    @NotNull
    @Positive
    private Double quantity = 1.0;

    @NotNull
    @Positive
    private Double unitPrice = 5000.0;

    private Double totalAmount = 5000.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceOrderStatus status = ServiceOrderStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
        if (this.totalAmount == null || this.totalAmount == 0.0) {
            this.totalAmount = (quantity != null && unitPrice != null) ? quantity * unitPrice : 0.0;
        }
    }

    public ServiceOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IndustrialFactory getFactory() { return factory; }
    public void setFactory(IndustrialFactory factory) { this.factory = factory; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getServicePeriod() { return servicePeriod; }
    public void setServicePeriod(String servicePeriod) { this.servicePeriod = servicePeriod; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public ServiceOrderStatus getStatus() { return status; }
    public void setStatus(ServiceOrderStatus status) { this.status = status; }
}
