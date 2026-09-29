package com.library.ebook.dto;

import java.math.BigDecimal;

public class BudgetReportDto {
    private Long budgetId;
    private String budgetName;
    private String department;
    private String fiscalYear;
    private BigDecimal plannedBudget;
    private BigDecimal actualLicensingExpenditure;
    private BigDecimal remainingBudget;
    private BigDecimal variance;
    private Double utilizationPercentage;
    private String status;

    public BudgetReportDto() {}

    public Long getBudgetId() { return budgetId; }
    public void setBudgetId(Long budgetId) { this.budgetId = budgetId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }

    public BigDecimal getPlannedBudget() { return plannedBudget; }
    public void setPlannedBudget(BigDecimal plannedBudget) { this.plannedBudget = plannedBudget; }

    public BigDecimal getActualLicensingExpenditure() { return actualLicensingExpenditure; }
    public void setActualLicensingExpenditure(BigDecimal actualLicensingExpenditure) { this.actualLicensingExpenditure = actualLicensingExpenditure; }

    public BigDecimal getActualExpenditure() { return actualLicensingExpenditure; }
    public void setActualExpenditure(BigDecimal actualExpenditure) { this.actualLicensingExpenditure = actualExpenditure; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }

    public BigDecimal getVariance() { return variance; }
    public void setVariance(BigDecimal variance) { this.variance = variance; }

    public Double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(Double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
