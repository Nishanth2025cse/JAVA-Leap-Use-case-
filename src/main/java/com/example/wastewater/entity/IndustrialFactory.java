package com.example.wastewater.entity;

import com.example.wastewater.enums.FactoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "industrial_factories", indexes = {
    @Index(name = "idx_factory_reg", columnList = "registrationNumber"),
    @Index(name = "idx_factory_status", columnList = "status")
})
public class IndustrialFactory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Factory name is required")
    @Column(nullable = false)
    private String factoryName;

    @NotBlank(message = "Registration number is required")
    @Column(nullable = false, unique = true)
    private String registrationNumber;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String industryType;

    @NotNull(message = "Permitted COD limit is required")
    @PositiveOrZero(message = "Permitted COD limit cannot be negative")
    private Double permittedCodLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FactoryStatus status = FactoryStatus.ACTIVE;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public IndustrialFactory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFactoryName() { return factoryName; }
    public void setFactoryName(String factoryName) { this.factoryName = factoryName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getIndustryType() { return industryType; }
    public void setIndustryType(String industryType) { this.industryType = industryType; }

    public Double getPermittedCodLimit() { return permittedCodLimit; }
    public void setPermittedCodLimit(Double permittedCodLimit) { this.permittedCodLimit = permittedCodLimit; }

    public FactoryStatus getStatus() { return status; }
    public void setStatus(FactoryStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
