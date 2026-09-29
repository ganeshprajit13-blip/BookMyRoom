package com.library.ebook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class BalanceSheetDto {
    private LocalDate asOfDate;
    private Map<String, BigDecimal> assets;
    private BigDecimal totalAssets;
    private Map<String, BigDecimal> liabilities;
    private BigDecimal totalLiabilities;
    private BigDecimal netWorthEquity;

    public BalanceSheetDto() {}

    public LocalDate getAsOfDate() { return asOfDate; }
    public void setAsOfDate(LocalDate asOfDate) { this.asOfDate = asOfDate; }

    public Map<String, BigDecimal> getAssets() { return assets; }
    public void setAssets(Map<String, BigDecimal> assets) { this.assets = assets; }

    public BigDecimal getTotalAssets() { return totalAssets; }
    public void setTotalAssets(BigDecimal totalAssets) { this.totalAssets = totalAssets; }

    public Map<String, BigDecimal> getLiabilities() { return liabilities; }
    public void setLiabilities(Map<String, BigDecimal> liabilities) { this.liabilities = liabilities; }

    public BigDecimal getTotalLiabilities() { return totalLiabilities; }
    public void setTotalLiabilities(BigDecimal totalLiabilities) { this.totalLiabilities = totalLiabilities; }

    public BigDecimal getNetWorthEquity() { return netWorthEquity; }
    public void setNetWorthEquity(BigDecimal netWorthEquity) { this.netWorthEquity = netWorthEquity; }
}
