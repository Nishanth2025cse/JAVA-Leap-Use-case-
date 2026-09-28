package com.example.wastewater.dto;

import com.example.wastewater.enums.BudgetStatus;
import java.util.List;

public class BudgetReportDTO {

    public static class FacilityBudgetDetail {
        private String facilityName;
        private String plantLocation;
        private String financialYear;
        private double allocatedAmount;
        private double actualAmount;
        private double remainingAmount;
        private double utilizationPercentage;
        private BudgetStatus status;

        public FacilityBudgetDetail() {}

        public String getFacilityName() { return facilityName; }
        public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

        public String getPlantLocation() { return plantLocation; }
        public void setPlantLocation(String plantLocation) { this.plantLocation = plantLocation; }

        public String getFinancialYear() { return financialYear; }
        public void setFinancialYear(String financialYear) { this.financialYear = financialYear; }

        public double getAllocatedAmount() { return allocatedAmount; }
        public void setAllocatedAmount(double allocatedAmount) { this.allocatedAmount = allocatedAmount; }

        public double getActualAmount() { return actualAmount; }
        public void setActualAmount(double actualAmount) { this.actualAmount = actualAmount; }

        public double getRemainingAmount() { return remainingAmount; }
        public void setRemainingAmount(double remainingAmount) { this.remainingAmount = remainingAmount; }

        public double getUtilizationPercentage() { return utilizationPercentage; }
        public void setUtilizationPercentage(double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

        public BudgetStatus getStatus() { return status; }
        public void setStatus(BudgetStatus status) { this.status = status; }
    }

    private double totalAllocated;
    private double totalActual;
    private double totalRemaining;
    private double overallUtilizationPercentage;
    private List<FacilityBudgetDetail> facilityDetails;

    public BudgetReportDTO() {}

    public double getTotalAllocated() { return totalAllocated; }
    public void setTotalAllocated(double totalAllocated) { this.totalAllocated = totalAllocated; }

    public double getTotalActual() { return totalActual; }
    public void setTotalActual(double totalActual) { this.totalActual = totalActual; }

    public double getTotalRemaining() { return totalRemaining; }
    public void setTotalRemaining(double totalRemaining) { this.totalRemaining = totalRemaining; }

    public double getOverallUtilizationPercentage() { return overallUtilizationPercentage; }
    public void setOverallUtilizationPercentage(double overallUtilizationPercentage) { this.overallUtilizationPercentage = overallUtilizationPercentage; }

    public List<FacilityBudgetDetail> getFacilityDetails() { return facilityDetails; }
    public void setFacilityDetails(List<FacilityBudgetDetail> facilityDetails) { this.facilityDetails = facilityDetails; }
}
