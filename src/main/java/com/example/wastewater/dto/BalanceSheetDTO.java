package com.example.wastewater.dto;

import java.util.Map;

public class BalanceSheetDTO {
    private Map<String, Double> assets;
    private double totalAssets;
    private Map<String, Double> liabilities;
    private double totalLiabilities;
    private double equity;

    public BalanceSheetDTO() {}

    public Map<String, Double> getAssets() { return assets; }
    public void setAssets(Map<String, Double> assets) { this.assets = assets; }

    public double getTotalAssets() { return totalAssets; }
    public void setTotalAssets(double totalAssets) { this.totalAssets = totalAssets; }

    public Map<String, Double> getLiabilities() { return liabilities; }
    public void setLiabilities(Map<String, Double> liabilities) { this.liabilities = liabilities; }

    public double getTotalLiabilities() { return totalLiabilities; }
    public void setTotalLiabilities(double totalLiabilities) { this.totalLiabilities = totalLiabilities; }

    public double getEquity() { return equity; }
    public void setEquity(double equity) { this.equity = equity; }
}
