package com.example.wastewater.entity;

import com.example.wastewater.enums.ComplianceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry", indexes = {
    @Index(name = "idx_telemetry_factory", columnList = "factory_id"),
    @Index(name = "idx_telemetry_time", columnList = "measurementDateTime"),
    @Index(name = "idx_telemetry_status", columnList = "status")
})
public class TelemetryReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "factory_id", nullable = false)
    private IndustrialFactory factory;

    @NotNull(message = "COD value is required")
    @PositiveOrZero(message = "COD value cannot be negative")
    private Double codValue;

    private Double permittedCodLimit;

    private LocalDateTime measurementDateTime;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplianceStatus status;

    private Double violationAmount = 0.0;

    private String remarks;

    @PrePersist
    protected void onCreate() {
        if (this.measurementDateTime == null) {
            this.measurementDateTime = LocalDateTime.now();
        }
    }

    public TelemetryReading() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public IndustrialFactory getFactory() { return factory; }
    public void setFactory(IndustrialFactory factory) { this.factory = factory; }

    public Double getCodValue() { return codValue; }
    public void setCodValue(Double codValue) { this.codValue = codValue; }

    public Double getPermittedCodLimit() { return permittedCodLimit; }
    public void setPermittedCodLimit(Double permittedCodLimit) { this.permittedCodLimit = permittedCodLimit; }

    public LocalDateTime getMeasurementDateTime() { return measurementDateTime; }
    public void setMeasurementDateTime(LocalDateTime measurementDateTime) { this.measurementDateTime = measurementDateTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public ComplianceStatus getStatus() { return status; }
    public void setStatus(ComplianceStatus status) { this.status = status; }

    public Double getViolationAmount() { return violationAmount; }
    public void setViolationAmount(Double violationAmount) { this.violationAmount = violationAmount; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
