package com.example.wastewater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "analytic_accounts")
public class AnalyticAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Facility name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    private String plantLocation;

    private boolean active = true;

    public AnalyticAccount() {}

    public AnalyticAccount(String name, String description, String plantLocation) {
        this.name = name;
        this.description = description;
        this.plantLocation = plantLocation;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPlantLocation() { return plantLocation; }
    public void setPlantLocation(String plantLocation) { this.plantLocation = plantLocation; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
