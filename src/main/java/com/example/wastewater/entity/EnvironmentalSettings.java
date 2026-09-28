package com.example.wastewater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "environmental_settings")
public class EnvironmentalSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Default COD limit is required")
    @PositiveOrZero
    private Double defaultCodLimit = 250.0;

    @NotNull(message = "Fine rate per excess COD unit is required")
    @PositiveOrZero
    private Double fineRate = 50.0;

    private String currency = "$";

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public EnvironmentalSettings() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getDefaultCodLimit() { return defaultCodLimit; }
    public void setDefaultCodLimit(Double defaultCodLimit) { this.defaultCodLimit = defaultCodLimit; }

    public Double getFineRate() { return fineRate; }
    public void setFineRate(Double fineRate) { this.fineRate = fineRate; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
