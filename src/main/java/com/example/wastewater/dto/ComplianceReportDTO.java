package com.example.wastewater.dto;

import java.util.List;

public class ComplianceReportDTO {

    public static class FactoryViolationSummary {
        private Long factoryId;
        private String factoryName;
        private long violationCount;
        private double totalFineAmount;

        public FactoryViolationSummary() {}

        public FactoryViolationSummary(Long factoryId, String factoryName, long violationCount, double totalFineAmount) {
            this.factoryId = factoryId;
            this.factoryName = factoryName;
            this.violationCount = violationCount;
            this.totalFineAmount = totalFineAmount;
        }

        public Long getFactoryId() { return factoryId; }
        public void setFactoryId(Long factoryId) { this.factoryId = factoryId; }

        public String getFactoryName() { return factoryName; }
        public void setFactoryName(String factoryName) { this.factoryName = factoryName; }

        public long getViolationCount() { return violationCount; }
        public void setViolationCount(long violationCount) { this.violationCount = violationCount; }

        public double getTotalFineAmount() { return totalFineAmount; }
        public void setTotalFineAmount(double totalFineAmount) { this.totalFineAmount = totalFineAmount; }
    }

    private long totalTelemetryRecords;
    private long compliantRecords;
    private long nonCompliantRecords;
    private long totalViolations;
    private double totalFineAmount;
    private double complianceRatePercentage;
    private List<FactoryViolationSummary> factorySummaries;

    public ComplianceReportDTO() {}

    public long getTotalTelemetryRecords() { return totalTelemetryRecords; }
    public void setTotalTelemetryRecords(long totalTelemetryRecords) { this.totalTelemetryRecords = totalTelemetryRecords; }

    public long getCompliantRecords() { return compliantRecords; }
    public void setCompliantRecords(long compliantRecords) { this.compliantRecords = compliantRecords; }

    public long getNonCompliantRecords() { return nonCompliantRecords; }
    public void setNonCompliantRecords(long nonCompliantRecords) { this.nonCompliantRecords = nonCompliantRecords; }

    public long getTotalViolations() { return totalViolations; }
    public void setTotalViolations(long totalViolations) { this.totalViolations = totalViolations; }

    public double getTotalFineAmount() { return totalFineAmount; }
    public void setTotalFineAmount(double totalFineAmount) { this.totalFineAmount = totalFineAmount; }

    public double getComplianceRatePercentage() { return complianceRatePercentage; }
    public void setComplianceRatePercentage(double complianceRatePercentage) { this.complianceRatePercentage = complianceRatePercentage; }

    public List<FactoryViolationSummary> getFactorySummaries() { return factorySummaries; }
    public void setFactorySummaries(List<FactoryViolationSummary> factorySummaries) { this.factorySummaries = factorySummaries; }
}
