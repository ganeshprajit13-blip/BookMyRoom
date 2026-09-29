package com.library.ebook.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UnifiedPaymentDto {
    private Long id;
    private String paymentNumber;
    private String partyName;
    private String partyType; // "PATRON" or "PUBLISHER"
    private String reference; // Bill / Invoice Number
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private String status;

    public UnifiedPaymentDto() {}

    public UnifiedPaymentDto(Long id, String paymentNumber, String partyName, String partyType,
                             String reference, BigDecimal amount, String paymentMethod,
                             LocalDate paymentDate, String status) {
        this.id = id;
        this.paymentNumber = paymentNumber;
        this.partyName = partyName;
        this.partyType = partyType;
        this.reference = reference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(String paymentNumber) { this.paymentNumber = paymentNumber; }

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public String getPartyType() { return partyType; }
    public void setPartyType(String partyType) { this.partyType = partyType; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
