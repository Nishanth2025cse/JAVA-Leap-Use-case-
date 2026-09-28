package com.example.wastewater.entity;

import com.example.wastewater.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "chart_of_accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Account code is required")
    @Column(nullable = false, unique = true)
    private String accountCode;

    @NotBlank(message = "Account name is required")
    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    private Double openingBalance = 0.0;

    private Double currentBalance = 0.0;

    private boolean active = true;

    public Account() {}

    public Account(String accountCode, String accountName, AccountType accountType, Double openingBalance) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
        this.openingBalance = openingBalance;
        this.currentBalance = openingBalance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountCode() { return accountCode; }
    public void setAccountCode(String accountCode) { this.accountCode = accountCode; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }

    public Double getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(Double openingBalance) { this.openingBalance = openingBalance; }

    public Double getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(Double currentBalance) { this.currentBalance = currentBalance; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
