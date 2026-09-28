package com.example.wastewater.dto;

import com.example.wastewater.entity.ChemicalInventory;
import com.example.wastewater.entity.CustomerInvoice;
import com.example.wastewater.entity.EnvironmentalViolation;
import com.example.wastewater.entity.TelemetryReading;

import java.util.List;

public class DashboardSummaryDTO {

    private long totalFactories;
    private long todayCodReadings;
    private long nonCompliantDischarges;
    private long pendingCustomerInvoices;
    private long chemicalLowStock;
    private double totalComplianceFines;
    private double currentBudgetUtilization; // Percentage e.g. 65.5%
    private double pendingVendorBillsAmount;

    private List<TelemetryReading> recentTelemetry;
    private List<EnvironmentalViolation> recentViolations;
    private List<ChemicalInventory> lowStockItems;
    private List<CustomerInvoice> pendingInvoices;

    public DashboardSummaryDTO() {}

    public long getTotalFactories() { return totalFactories; }
    public void setTotalFactories(long totalFactories) { this.totalFactories = totalFactories; }

    public long getTodayCodReadings() { return todayCodReadings; }
    public void setTodayCodReadings(long todayCodReadings) { this.todayCodReadings = todayCodReadings; }

    public long getNonCompliantDischarges() { return nonCompliantDischarges; }
    public void setNonCompliantDischarges(long nonCompliantDischarges) { this.nonCompliantDischarges = nonCompliantDischarges; }

    public long getPendingCustomerInvoices() { return pendingCustomerInvoices; }
    public void setPendingCustomerInvoices(long pendingCustomerInvoices) { this.pendingCustomerInvoices = pendingCustomerInvoices; }

    public long getChemicalLowStock() { return chemicalLowStock; }
    public void setChemicalLowStock(long chemicalLowStock) { this.chemicalLowStock = chemicalLowStock; }

    public double getTotalComplianceFines() { return totalComplianceFines; }
    public void setTotalComplianceFines(double totalComplianceFines) { this.totalComplianceFines = totalComplianceFines; }

    public double getCurrentBudgetUtilization() { return currentBudgetUtilization; }
    public void setCurrentBudgetUtilization(double currentBudgetUtilization) { this.currentBudgetUtilization = currentBudgetUtilization; }

    public double getPendingVendorBillsAmount() { return pendingVendorBillsAmount; }
    public void setPendingVendorBillsAmount(double pendingVendorBillsAmount) { this.pendingVendorBillsAmount = pendingVendorBillsAmount; }

    public List<TelemetryReading> getRecentTelemetry() { return recentTelemetry; }
    public void setRecentTelemetry(List<TelemetryReading> recentTelemetry) { this.recentTelemetry = recentTelemetry; }

    public List<EnvironmentalViolation> getRecentViolations() { return recentViolations; }
    public void setRecentViolations(List<EnvironmentalViolation> recentViolations) { this.recentViolations = recentViolations; }

    public List<ChemicalInventory> getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(List<ChemicalInventory> lowStockItems) { this.lowStockItems = lowStockItems; }

    public List<CustomerInvoice> getPendingInvoices() { return pendingInvoices; }
    public void setPendingInvoices(List<CustomerInvoice> pendingInvoices) { this.pendingInvoices = pendingInvoices; }
}
