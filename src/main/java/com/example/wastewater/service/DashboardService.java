package com.example.wastewater.service;

import com.example.wastewater.dto.DashboardSummaryDTO;
import com.example.wastewater.enums.ComplianceStatus;
import com.example.wastewater.enums.InvoiceStatus;
import com.example.wastewater.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final IndustrialFactoryRepository factoryRepository;
    private final TelemetryReadingRepository telemetryRepository;
    private final EnvironmentalViolationRepository violationRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final ChemicalInventoryRepository inventoryRepository;
    private final VendorBillRepository vendorBillRepository;
    private final BudgetRepository budgetRepository;

    public DashboardService(IndustrialFactoryRepository factoryRepository,
                            TelemetryReadingRepository telemetryRepository,
                            EnvironmentalViolationRepository violationRepository,
                            CustomerInvoiceRepository invoiceRepository,
                            ChemicalInventoryRepository inventoryRepository,
                            VendorBillRepository vendorBillRepository,
                            BudgetRepository budgetRepository) {
        this.factoryRepository = factoryRepository;
        this.telemetryRepository = telemetryRepository;
        this.violationRepository = violationRepository;
        this.invoiceRepository = invoiceRepository;
        this.inventoryRepository = inventoryRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.budgetRepository = budgetRepository;
    }

    public DashboardSummaryDTO getDashboardSummary() {
        DashboardSummaryDTO dto = new DashboardSummaryDTO();

        dto.setTotalFactories(factoryRepository.count());

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        dto.setTodayCodReadings(telemetryRepository.countTodayReadings(startOfDay));
        dto.setNonCompliantDischarges(telemetryRepository.findByStatus(ComplianceStatus.NON_COMPLIANT).size());

        dto.setPendingCustomerInvoices(invoiceRepository.countPendingInvoices());
        dto.setChemicalLowStock(inventoryRepository.findLowStockItems().size());

        Double totalFines = violationRepository.getTotalFineAmount();
        dto.setTotalComplianceFines(totalFines != null ? totalFines : 0.0);

        Double allocated = budgetRepository.getTotalAllocatedBudget();
        Double actual = budgetRepository.getTotalActualSpending();
        double util = (allocated != null && allocated > 0 && actual != null) ? (actual / allocated) * 100.0 : 0.0;
        dto.setCurrentBudgetUtilization(Math.round(util * 10.0) / 10.0);

        Double pendingBills = vendorBillRepository.getTotalPendingPayables();
        dto.setPendingVendorBillsAmount(pendingBills != null ? pendingBills : 0.0);

        dto.setRecentTelemetry(telemetryRepository.findTop10ByOrderByMeasurementDateTimeDesc());
        dto.setRecentViolations(violationRepository.findTop10ByOrderByViolationDateDesc());
        dto.setLowStockItems(inventoryRepository.findLowStockItems());
        dto.setPendingInvoices(invoiceRepository.findByStatus(InvoiceStatus.ISSUED));

        return dto;
    }
}
