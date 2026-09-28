package com.example.wastewater.entity;

import com.example.wastewater.enums.ProductType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String productName;

    @NotBlank(message = "Product code is required")
    @Column(nullable = false, unique = true)
    private String productCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private String description;

    private String unit;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private Double price;

    @NotNull(message = "Tax percentage is required")
    @PositiveOrZero(message = "Tax percentage cannot be negative")
    private Double taxPercentage = 18.0;

    private boolean active = true;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getTaxPercentage() { return taxPercentage; }
    public void setTaxPercentage(Double taxPercentage) { this.taxPercentage = taxPercentage; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
