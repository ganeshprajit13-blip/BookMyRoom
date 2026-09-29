package com.library.ebook.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DashboardStatsDto {
    private long totalEbooks;
    private long activeLicenses;
    private long totalPatrons;
    private long totalPublishers;
    private long activeCheckouts;
    private int availableCapacity;
    private long activeHolds;
    private long expiringLicenses;
    private BigDecimal digitalAssetValue;
    private BigDecimal licensingExpense;
    private BigDecimal membershipRevenue;
    private BigDecimal outstandingPublisherPayables;
    private BigDecimal outstandingPatronReceivables;
    private double budgetUtilizationPct;
    private List<Map<String, Object>> recentTransactions;
    private List<Map<String, Object>> licenseUtilizationSummary;

    public DashboardStatsDto() {}

    // Getters and Setters
    public long getTotalEbooks() { return totalEbooks; }
    public void setTotalEbooks(long totalEbooks) { this.totalEbooks = totalEbooks; }

    public long getActiveLicenses() { return activeLicenses; }
    public void setActiveLicenses(long activeLicenses) { this.activeLicenses = activeLicenses; }

    public long getTotalPatrons() { return totalPatrons; }
    public void setTotalPatrons(long totalPatrons) { this.totalPatrons = totalPatrons; }

    public long getTotalPublishers() { return totalPublishers; }
    public void setTotalPublishers(long totalPublishers) { this.totalPublishers = totalPublishers; }

    public long getActiveCheckouts() { return activeCheckouts; }
    public void setActiveCheckouts(long activeCheckouts) { this.activeCheckouts = activeCheckouts; }

    public int getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(int availableCapacity) { this.availableCapacity = availableCapacity; }

    public long getActiveHolds() { return activeHolds; }
    public void setActiveHolds(long activeHolds) { this.activeHolds = activeHolds; }

    public long getExpiringLicenses() { return expiringLicenses; }
    public void setExpiringLicenses(long expiringLicenses) { this.expiringLicenses = expiringLicenses; }

    public BigDecimal getDigitalAssetValue() { return digitalAssetValue; }
    public void setDigitalAssetValue(BigDecimal digitalAssetValue) { this.digitalAssetValue = digitalAssetValue; }

    public BigDecimal getLicensingExpense() { return licensingExpense; }
    public void setLicensingExpense(BigDecimal licensingExpense) { this.licensingExpense = licensingExpense; }

    public BigDecimal getMembershipRevenue() { return membershipRevenue; }
    public void setMembershipRevenue(BigDecimal membershipRevenue) { this.membershipRevenue = membershipRevenue; }

    public BigDecimal getOutstandingPublisherPayables() { return outstandingPublisherPayables; }
    public void setOutstandingPublisherPayables(BigDecimal outstandingPublisherPayables) { this.outstandingPublisherPayables = outstandingPublisherPayables; }

    public BigDecimal getOutstandingPatronReceivables() { return outstandingPatronReceivables; }
    public void setOutstandingPatronReceivables(BigDecimal outstandingPatronReceivables) { this.outstandingPatronReceivables = outstandingPatronReceivables; }

    public double getBudgetUtilizationPct() { return budgetUtilizationPct; }
    public void setBudgetUtilizationPct(double budgetUtilizationPct) { this.budgetUtilizationPct = budgetUtilizationPct; }

    public double getBudgetUtilization() { return budgetUtilizationPct; }
    public void setBudgetUtilization(double budgetUtilization) { this.budgetUtilizationPct = budgetUtilization; }

    public List<Map<String, Object>> getRecentTransactions() { return recentTransactions; }
    public void setRecentTransactions(List<Map<String, Object>> recentTransactions) { this.recentTransactions = recentTransactions; }

    public List<Map<String, Object>> getLicenseUtilizationSummary() { return licenseUtilizationSummary; }
    public void setLicenseUtilizationSummary(List<Map<String, Object>> licenseUtilizationSummary) { this.licenseUtilizationSummary = licenseUtilizationSummary; }
}
