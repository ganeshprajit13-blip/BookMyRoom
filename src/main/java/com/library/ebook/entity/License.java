package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ebook_licenses")
public class License {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @NotNull(message = "E-Book is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ebook_id", nullable = false)
    private Ebook ebook;

    @NotNull(message = "Publisher is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Contact publisher;

    @NotNull(message = "License type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "license_type", nullable = false, length = 20)
    private LicenseType licenseType = LicenseType.PERPETUAL;

    @NotNull(message = "Concurrent user limit is required")
    @Min(value = 1, message = "Concurrent user limit must be at least 1")
    @Column(name = "concurrent_user_limit", nullable = false)
    private Integer concurrentUserLimit;

    @NotNull(message = "License start date is required")
    @Column(name = "license_start_date", nullable = false)
    private LocalDate licenseStartDate;

    @Column(name = "license_expiry_date")
    private LocalDate licenseExpiryDate;

    @NotNull(message = "Purchase cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase cost must be positive")
    @Column(name = "purchase_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal purchaseCost;

    @Column(length = 10)
    private String currency = "INR";

    @Column(name = "active_checkout_count", nullable = false)
    private Integer activeCheckoutCount = 0;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LicenseStatus status = LicenseStatus.ACTIVE;

    @Column(name = "amortization_period_months")
    private Integer amortizationPeriod = 60; // in months

    @Column(name = "remaining_asset_value", precision = 12, scale = 2)
    private BigDecimal remainingAssetValue;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public License() {}

    public License(String licenseNumber, Ebook ebook, Contact publisher, LicenseType licenseType,
                   Integer concurrentUserLimit, LocalDate licenseStartDate, LocalDate licenseExpiryDate,
                   BigDecimal purchaseCost, Integer amortizationPeriod) {
        this.licenseNumber = licenseNumber;
        this.ebook = ebook;
        this.publisher = publisher;
        this.licenseType = licenseType;
        this.concurrentUserLimit = concurrentUserLimit;
        this.licenseStartDate = licenseStartDate;
        this.licenseExpiryDate = licenseExpiryDate;
        this.purchaseCost = purchaseCost;
        this.remainingAssetValue = purchaseCost;
        this.amortizationPeriod = amortizationPeriod;
        this.activeCheckoutCount = 0;
        this.status = LicenseStatus.ACTIVE;
        this.currency = "INR";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.activeCheckoutCount == null) {
            this.activeCheckoutCount = 0;
        }
        if (this.status == null) {
            this.status = LicenseStatus.ACTIVE;
        }
        if (this.currency == null) {
            this.currency = "INR";
        }
        if (this.remainingAssetValue == null && this.purchaseCost != null) {
            this.remainingAssetValue = this.purchaseCost;
        }
    }

    // Helper methods for business logic
    public boolean hasAvailableCapacity() {
        return activeCheckoutCount < concurrentUserLimit;
    }

    public int getAvailableCapacity() {
        return Math.max(0, concurrentUserLimit - activeCheckoutCount);
    }

    public double getUtilizationPercentage() {
        if (concurrentUserLimit == null || concurrentUserLimit <= 0) return 0.0;
        return ((double) activeCheckoutCount / concurrentUserLimit) * 100.0;
    }

    public boolean isExpired() {
        if (licenseType == LicenseType.PERPETUAL) return false;
        return licenseExpiryDate != null && licenseExpiryDate.isBefore(LocalDate.now());
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Ebook getEbook() { return ebook; }
    public void setEbook(Ebook ebook) { this.ebook = ebook; }

    public Contact getPublisher() { return publisher; }
    public void setPublisher(Contact publisher) { this.publisher = publisher; }

    public LicenseType getLicenseType() { return licenseType; }
    public void setLicenseType(LicenseType licenseType) { this.licenseType = licenseType; }

    public Integer getConcurrentUserLimit() { return concurrentUserLimit; }
    public void setConcurrentUserLimit(Integer concurrentUserLimit) { this.concurrentUserLimit = concurrentUserLimit; }

    public LocalDate getLicenseStartDate() { return licenseStartDate; }
    public void setLicenseStartDate(LocalDate licenseStartDate) { this.licenseStartDate = licenseStartDate; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    public BigDecimal getPurchaseCost() { return purchaseCost; }
    public void setPurchaseCost(BigDecimal purchaseCost) { this.purchaseCost = purchaseCost; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Integer getActiveCheckoutCount() { return activeCheckoutCount; }
    public void setActiveCheckoutCount(Integer activeCheckoutCount) { this.activeCheckoutCount = activeCheckoutCount; }

    public LicenseStatus getStatus() { return status; }
    public void setStatus(LicenseStatus status) { this.status = status; }

    public Integer getAmortizationPeriod() { return amortizationPeriod; }
    public void setAmortizationPeriod(Integer amortizationPeriod) { this.amortizationPeriod = amortizationPeriod; }

    public BigDecimal getRemainingAssetValue() { return remainingAssetValue; }
    public void setRemainingAssetValue(BigDecimal remainingAssetValue) { this.remainingAssetValue = remainingAssetValue; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
