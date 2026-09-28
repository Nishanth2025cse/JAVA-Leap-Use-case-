package com.example.wastewater.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "chemical_suppliers", indexes = {
    @Index(name = "idx_supplier_status", columnList = "status")
})
public class ChemicalSupplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Supplier name is required")
    @Column(nullable = false)
    private String supplierName;

    private String contactPerson;

    private String phone;

    private String email;

    private String address;

    private String taxNumber;

    private String status = "ACTIVE";

    public ChemicalSupplier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
