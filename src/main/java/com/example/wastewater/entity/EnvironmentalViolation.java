package com.example.wastewater.entity;

import com.example.wastewater.enums.ViolationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "environmental_violations", indexes = {
    @Index(name = "idx_viol_factory", columnList = "factory_id"),
    @Index(name = "idx_viol_status", columnList = "status"),
    @Index(name = "idx_viol_date", columnList = "violationDate")
})
public class EnvironmentalViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "factory_id", nullable = false)
    private IndustrialFactory factory;

    @ManyToOne
    @JoinColumn(name = "telemetry_id")
    private TelemetryReading telemetry;

    private Double codValue;

    private Double permittedLimit;

    private Double excessCod;

    private Double fineAmount;

    private LocalDateTime violationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationStatus status = ViolationStatus.OPEN;

    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (this.violationDate == null) {
            this.violationDate = LocalDateTime.now();
        }
    }

    public EnvironmentalViolation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IndustrialFactory getFactory() { return factory; }
    public void setFactory(IndustrialFactory factory) { this.factory = factory; }

    public TelemetryReading getTelemetry() { return telemetry; }
    public void setTelemetry(TelemetryReading telemetry) { this.telemetry = telemetry; }

    public Double getCodValue() { return codValue; }
    public void setCodValue(Double codValue) { this.codValue = codValue; }

    public Double getPermittedLimit() { return permittedLimit; }
    public void setPermittedLimit(Double permittedLimit) { this.permittedLimit = permittedLimit; }

    public Double getExcessCod() { return excessCod; }
    public void setExcessCod(Double excessCod) { this.excessCod = excessCod; }

    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }

    public LocalDateTime getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDateTime violationDate) { this.violationDate = violationDate; }

    public ViolationStatus getStatus() { return status; }
    public void setStatus(ViolationStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
