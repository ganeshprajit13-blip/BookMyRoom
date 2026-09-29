package com.library.ebook.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class JournalLineRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Debit is required")
    @DecimalMin(value = "0.0", message = "Debit must be non-negative")
    private BigDecimal debit = BigDecimal.ZERO;

    @NotNull(message = "Credit is required")
    @DecimalMin(value = "0.0", message = "Credit must be non-negative")
    private BigDecimal credit = BigDecimal.ZERO;

    private String description;

    public JournalLineRequest() {}

    public JournalLineRequest(Long accountId, BigDecimal debit, BigDecimal credit, String description) {
        this.accountId = accountId;
        this.debit = debit != null ? debit : BigDecimal.ZERO;
        this.credit = credit != null ? credit : BigDecimal.ZERO;
        this.description = description;
    }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }

    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
