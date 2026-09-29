package com.library.ebook.dto;

import java.math.BigDecimal;

public class AmortizationResponse {
    private Long licenseId;
    private String licenseNumber;
    private String ebookTitle;
    private BigDecimal originalAssetValue;
    private Integer amortizationPeriodMonths;
    private BigDecimal monthlyAmortizationAmount;
    private BigDecimal remainingAssetValue;
    private String journalEntryNumber;
    private String message;
    private String accountingNote;

    public AmortizationResponse() {}

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getEbookTitle() { return ebookTitle; }
    public void setEbookTitle(String ebookTitle) { this.ebookTitle = ebookTitle; }

    public BigDecimal getOriginalAssetValue() { return originalAssetValue; }
    public void setOriginalAssetValue(BigDecimal originalAssetValue) { this.originalAssetValue = originalAssetValue; }

    public Integer getAmortizationPeriodMonths() { return amortizationPeriodMonths; }
    public void setAmortizationPeriodMonths(Integer amortizationPeriodMonths) { this.amortizationPeriodMonths = amortizationPeriodMonths; }

    public BigDecimal getMonthlyAmortizationAmount() { return monthlyAmortizationAmount; }
    public void setMonthlyAmortizationAmount(BigDecimal monthlyAmortizationAmount) { this.monthlyAmortizationAmount = monthlyAmortizationAmount; }

    public BigDecimal getRemainingAssetValue() { return remainingAssetValue; }
    public void setRemainingAssetValue(BigDecimal remainingAssetValue) { this.remainingAssetValue = remainingAssetValue; }

    public String getJournalEntryNumber() { return journalEntryNumber; }
    public void setJournalEntryNumber(String journalEntryNumber) { this.journalEntryNumber = journalEntryNumber; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccountingNote() { return accountingNote; }
    public void setAccountingNote(String accountingNote) { this.accountingNote = accountingNote; }
}
