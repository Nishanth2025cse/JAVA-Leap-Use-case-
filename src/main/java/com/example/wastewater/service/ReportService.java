package com.example.wastewater.service;

import com.example.wastewater.dto.*;
import com.example.wastewater.entity.Account;
import com.example.wastewater.entity.Budget;

import com.example.wastewater.enums.ComplianceStatus;
import com.example.wastewater.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {

    private final AccountRepository accountRepository;
    private final BudgetRepository budgetRepository;
    private final TelemetryReadingRepository telemetryRepository;
    private final EnvironmentalViolationRepository violationRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final VendorBillRepository vendorBillRepository;

    public ReportService(AccountRepository accountRepository,
                         BudgetRepository budgetRepository,
                         TelemetryReadingRepository telemetryRepository,
                         EnvironmentalViolationRepository violationRepository,
                         CustomerInvoiceRepository invoiceRepository,
                         VendorBillRepository vendorBillRepository) {
        this.accountRepository = accountRepository;
        this.budgetRepository = budgetRepository;
        this.telemetryRepository = telemetryRepository;
        this.violationRepository = violationRepository;
        this.invoiceRepository = invoiceRepository;
        this.vendorBillRepository = vendorBillRepository;
    }

    public BalanceSheetDTO getBalanceSheet() {
        BalanceSheetDTO dto = new BalanceSheetDTO();
        Map<String, Double> assets = new LinkedHashMap<>();
        Map<String, Double> liabilities = new LinkedHashMap<>();

        List<Account> allAccounts = accountRepository.findAll();
        double totalAssets = 0.0;
        double totalLiabilities = 0.0;

        for (Account acc : allAccounts) {
            if (acc.getAccountType() == com.example.wastewater.enums.AccountType.ASSET) {
                assets.put(acc.getAccountName(), acc.getCurrentBalance());
                totalAssets += acc.getCurrentBalance();
            } else if (acc.getAccountType() == com.example.wastewater.enums.AccountType.LIABILITY) {
                liabilities.put(acc.getAccountName(), acc.getCurrentBalance());
                totalLiabilities += acc.getCurrentBalance();
            }
        }

        Double pendingReceivables = invoiceRepository.getTotalPendingReceivables();
        if (pendingReceivables != null && pendingReceivables > 0) {
            assets.put("Accounts Receivable", pendingReceivables);
            totalAssets += pendingReceivables;
        }

        Double pendingPayables = vendorBillRepository.getTotalPendingPayables();
        if (pendingPayables != null && pendingPayables > 0) {
            liabilities.put("Chemical Payables", pendingPayables);
            totalLiabilities += pendingPayables;
        }

        dto.setAssets(assets);
        dto.setTotalAssets(totalAssets);
        dto.setLiabilities(liabilities);
        dto.setTotalLiabilities(totalLiabilities);
        dto.setEquity(totalAssets - totalLiabilities);
        return dto;
    }

    public ProfitLossDTO getProfitLoss() {
        ProfitLossDTO dto = new ProfitLossDTO();
        Map<String, Double> income = new LinkedHashMap<>();
        Map<String, Double> expenses = new LinkedHashMap<>();

        List<Account> allAccounts = accountRepository.findAll();
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        for (Account acc : allAccounts) {
            if (acc.getAccountType() == com.example.wastewater.enums.AccountType.INCOME) {
                income.put(acc.getAccountName(), acc.getCurrentBalance());
                totalIncome += acc.getCurrentBalance();
            } else if (acc.getAccountType() == com.example.wastewater.enums.AccountType.EXPENSE) {
                expenses.put(acc.getAccountName(), acc.getCurrentBalance());
                totalExpenses += acc.getCurrentBalance();
            }
        }

        dto.setIncome(income);
        dto.setTotalIncome(totalIncome);
        dto.setExpenses(expenses);
        dto.setTotalExpenses(totalExpenses);
        dto.setNetProfitLoss(totalIncome - totalExpenses);
        return dto;
    }

    public BudgetReportDTO getBudgetReport() {
        BudgetReportDTO dto = new BudgetReportDTO();
        List<Budget> budgets = budgetRepository.findAll();
        List<BudgetReportDTO.FacilityBudgetDetail> detailsList = new ArrayList<>();

        double totalAllocated = 0.0;
        double totalActual = 0.0;

        for (Budget b : budgets) {
            BudgetReportDTO.FacilityBudgetDetail detail = new BudgetReportDTO.FacilityBudgetDetail();
            detail.setFacilityName(b.getAnalyticAccount().getName());
            detail.setPlantLocation(b.getAnalyticAccount().getPlantLocation());
            detail.setFinancialYear(b.getFinancialYear());
            detail.setAllocatedAmount(b.getAllocatedAmount());
            detail.setActualAmount(b.getActualAmount());
            detail.setRemainingAmount(b.getRemainingAmount());
            double util = b.getAllocatedAmount() > 0 ? (b.getActualAmount() / b.getAllocatedAmount()) * 100.0 : 0.0;
            detail.setUtilizationPercentage(Math.round(util * 10.0) / 10.0);
            detail.setStatus(b.getStatus());

            detailsList.add(detail);
            totalAllocated += b.getAllocatedAmount();
            totalActual += b.getActualAmount();
        }

        dto.setTotalAllocated(totalAllocated);
        dto.setTotalActual(totalActual);
        dto.setTotalRemaining(totalAllocated - totalActual);
        double overallUtil = totalAllocated > 0 ? (totalActual / totalAllocated) * 100.0 : 0.0;
        dto.setOverallUtilizationPercentage(Math.round(overallUtil * 10.0) / 10.0);
        dto.setFacilityDetails(detailsList);
        return dto;
    }

    public ComplianceReportDTO getComplianceReport() {
        ComplianceReportDTO dto = new ComplianceReportDTO();
        long totalTelemetry = telemetryRepository.count();
        long compliant = telemetryRepository.findByStatus(ComplianceStatus.COMPLIANT).size();
        long nonCompliant = telemetryRepository.findByStatus(ComplianceStatus.NON_COMPLIANT).size();
        long totalViolations = violationRepository.count();
        Double totalFine = violationRepository.getTotalFineAmount();

        dto.setTotalTelemetryRecords(totalTelemetry);
        dto.setCompliantRecords(compliant);
        dto.setNonCompliantRecords(nonCompliant);
        dto.setTotalViolations(totalViolations);
        dto.setTotalFineAmount(totalFine != null ? totalFine : 0.0);

        double rate = totalTelemetry > 0 ? ((double) compliant / totalTelemetry) * 100.0 : 100.0;
        dto.setComplianceRatePercentage(Math.round(rate * 10.0) / 10.0);

        List<Object[]> factorySummariesRaw = violationRepository.findFactoryWiseViolationSummary();
        List<ComplianceReportDTO.FactoryViolationSummary> summaries = new ArrayList<>();
        for (Object[] row : factorySummariesRaw) {
            Long factoryId = (Long) row[0];
            String factoryName = (String) row[1];
            long count = ((Number) row[2]).longValue();
            double fine = row[3] != null ? ((Number) row[3]).doubleValue() : 0.0;
            summaries.add(new ComplianceReportDTO.FactoryViolationSummary(factoryId, factoryName, count, fine));
        }
        dto.setFactorySummaries(summaries);
        return dto;
    }
}
