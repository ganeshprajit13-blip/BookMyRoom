package com.library.ebook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ProfitAndLossDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private Map<String, BigDecimal> income;
    private BigDecimal totalIncome;
    private Map<String, BigDecimal> expenses;
    private BigDecimal totalExpenses;
    private BigDecimal netProfitLoss;

    public ProfitAndLossDto() {}

    public LocalDate getFromDate() { return fromDate; }
    public void setFromDate(LocalDate fromDate) { this.fromDate = fromDate; }

    public LocalDate getToDate() { return toDate; }
    public void setToDate(LocalDate toDate) { this.toDate = toDate; }

    public Map<String, BigDecimal> getIncome() { return income; }
    public void setIncome(Map<String, BigDecimal> income) { this.income = income; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public Map<String, BigDecimal> getExpenses() { return expenses; }
    public void setExpenses(Map<String, BigDecimal> expenses) { this.expenses = expenses; }

    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }

    public BigDecimal getNetProfitLoss() { return netProfitLoss; }
    public void setNetProfitLoss(BigDecimal netProfitLoss) { this.netProfitLoss = netProfitLoss; }

    public BigDecimal getNetProfitOrLoss() { return netProfitLoss; }
    public void setNetProfitOrLoss(BigDecimal netProfitOrLoss) { this.netProfitLoss = netProfitOrLoss; }
}
