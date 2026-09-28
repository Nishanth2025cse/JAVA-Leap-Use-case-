package com.example.wastewater.entity;

import com.example.wastewater.enums.BudgetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "analytic_account_id", nullable = false)
    private AnalyticAccount analyticAccount;

    private String financialYear;

    @NotNull(message = "Allocated amount is required")
    @PositiveOrZero(message = "Allocated amount cannot be negative")
    private Double allocatedAmount = 0.0;

    private Double actualAmount = 0.0;

    private Double remainingAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetStatus status = BudgetStatus.NORMAL;

    public Budget() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AnalyticAccount getAnalyticAccount() { return analyticAccount; }
    public void setAnalyticAccount(AnalyticAccount analyticAccount) { this.analyticAccount = analyticAccount; }

    public String getFinancialYear() { return financialYear; }
    public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }

    public Double getAllocatedAmount() { return allocatedAmount; }
    public void setAllocatedAmount(Double allocatedAmount) { this.allocatedAmount = allocatedAmount; }

    public Double getActualAmount() { return actualAmount; }
    public void setActualAmount(Double actualAmount) { this.actualAmount = actualAmount; }

    public Double getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(Double remainingAmount) { this.remainingAmount = remainingAmount; }

    public BudgetStatus getStatus() { return status; }
    public void setStatus(BudgetStatus status) { this.status = status; }

    public void recalculateBudget() {
        if (allocatedAmount == null) allocatedAmount = 0.0;
        if (actualAmount == null) actualAmount = 0.0;
        this.remainingAmount = allocatedAmount - actualAmount;
        if (actualAmount > allocatedAmount) {
            this.status = BudgetStatus.CRITICAL;
        } else if (actualAmount >= 0.8 * allocatedAmount) {
            this.status = BudgetStatus.WARNING;
        } else {
            this.status = BudgetStatus.NORMAL;
        }
    }
}
