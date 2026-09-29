package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Budget name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull(message = "Analytic account is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "analytic_account_id", nullable = false)
    private AnalyticAccount analyticAccount;

    @Column(name = "fiscal_year", length = 30)
    private String fiscalYear = "2026-2027";

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Planned amount is required")
    @DecimalMin(value = "0.0", message = "Planned amount must be non-negative")
    @Column(name = "planned_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal plannedAmount;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Transient fields computed dynamically from posted transactions
    @Transient
    private BigDecimal actualExpenditure = BigDecimal.ZERO;

    @Transient
    private BigDecimal remainingBudget = BigDecimal.ZERO;

    @Transient
    private BigDecimal variance = BigDecimal.ZERO;

    @Transient
    private Double utilizationPercentage = 0.0;

    public Budget() {}

    public Budget(String name, AnalyticAccount analyticAccount, String fiscalYear, LocalDate startDate, LocalDate endDate, BigDecimal plannedAmount) {
        this.name = name;
        this.analyticAccount = analyticAccount;
        this.fiscalYear = fiscalYear != null ? fiscalYear : "2026-2027";
        this.startDate = startDate;
        this.endDate = endDate;
        this.plannedAmount = plannedAmount;
        this.status = "ACTIVE";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AnalyticAccount getAnalyticAccount() { return analyticAccount; }
    public void setAnalyticAccount(AnalyticAccount analyticAccount) { this.analyticAccount = analyticAccount; }

    public String getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(String fiscalYear) { this.fiscalYear = fiscalYear; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getPlannedAmount() { return plannedAmount; }
    public void setPlannedAmount(BigDecimal plannedAmount) { this.plannedAmount = plannedAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getActualExpenditure() { return actualExpenditure; }
    public void setActualExpenditure(BigDecimal actualExpenditure) { this.actualExpenditure = actualExpenditure; }

    public BigDecimal getRemainingBudget() { return remainingBudget; }
    public void setRemainingBudget(BigDecimal remainingBudget) { this.remainingBudget = remainingBudget; }

    public BigDecimal getVariance() { return variance; }
    public void setVariance(BigDecimal variance) { this.variance = variance; }

    public Double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(Double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }
}
