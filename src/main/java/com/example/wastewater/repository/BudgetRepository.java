package com.example.wastewater.repository;

import com.example.wastewater.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByAnalyticAccountId(Long analyticAccountId);
    Optional<Budget> findByAnalyticAccountIdAndFinancialYear(Long analyticAccountId, String financialYear);

    @Query("SELECT SUM(b.allocatedAmount) FROM Budget b")
    Double getTotalAllocatedBudget();

    @Query("SELECT SUM(b.actualAmount) FROM Budget b")
    Double getTotalActualSpending();
}
