package com.example.wastewater.dto;

import java.util.Map;

public class ProfitLossDTO {
    private Map<String, Double> income;
    private double totalIncome;
    private Map<String, Double> expenses;
    private double totalExpenses;
    private double netProfitLoss;

    public ProfitLossDTO() {}

    public Map<String, Double> getIncome() { return income; }
    public void setIncome(Map<String, Double> income) { this.income = income; }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public Map<String, Double> getExpenses() { return expenses; }
    public void setExpenses(Map<String, Double> expenses) { this.expenses = expenses; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public double getNetProfitLoss() { return netProfitLoss; }
    public void setNetProfitLoss(double netProfitLoss) { this.netProfitLoss = netProfitLoss; }
}
