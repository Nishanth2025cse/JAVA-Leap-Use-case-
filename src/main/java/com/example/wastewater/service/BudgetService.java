package com.example.wastewater.service;

import com.example.wastewater.entity.AnalyticAccount;
import com.example.wastewater.entity.Budget;
import com.example.wastewater.exception.ResourceNotFoundException;
import com.example.wastewater.repository.AnalyticAccountRepository;
import com.example.wastewater.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AnalyticAccountRepository analyticAccountRepository;

    public BudgetService(BudgetRepository budgetRepository, AnalyticAccountRepository analyticAccountRepository) {
        this.budgetRepository = budgetRepository;
        this.analyticAccountRepository = analyticAccountRepository;
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Budget getBudgetById(Long id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget record not found with id: " + id));
    }

    @Transactional
    public Budget createBudget(Budget budget) {
        Long facilityId = budget.getAnalyticAccount().getId();
        AnalyticAccount facility = analyticAccountRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Analytic account facility not found with id: " + facilityId));

        budget.setAnalyticAccount(facility);
        if (budget.getActualAmount() == null) budget.setActualAmount(0.0);
        budget.recalculateBudget();
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget updateBudget(Long id, Budget details) {
        Budget existing = getBudgetById(id);
        existing.setFinancialYear(details.getFinancialYear());
        existing.setAllocatedAmount(details.getAllocatedAmount());
        if (details.getActualAmount() != null) {
            existing.setActualAmount(details.getActualAmount());
        }
        existing.recalculateBudget();
        return budgetRepository.save(existing);
    }

    @Transactional
    public void recordExpense(Long analyticAccountId, Double expenseAmount) {
        List<Budget> budgets = budgetRepository.findByAnalyticAccountId(analyticAccountId);
        if (!budgets.isEmpty()) {
            Budget budget = budgets.get(0);
            budget.setActualAmount(budget.getActualAmount() + expenseAmount);
            budget.recalculateBudget();
            budgetRepository.save(budget);
        }
    }
}
