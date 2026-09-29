package com.library.ebook.dto;

import java.time.LocalDate;

public class LicenseUtilizationDto {
    private Long licenseId;
    private String licenseNumber;
    private String ebookTitle;
    private String isbn;
    private String publisherName;
    private String licenseType;
    private Integer concurrentLimit;
    private Integer activeCheckouts;
    private Integer availableSlots;
    private Double utilizationPercentage;
    private LocalDate expiryDate;
    private String status;

    public LicenseUtilizationDto() {}

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getEbookTitle() { return ebookTitle; }
    public void setEbookTitle(String ebookTitle) { this.ebookTitle = ebookTitle; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public String getLicenseType() { return licenseType; }
    public void setLicenseType(String licenseType) { this.licenseType = licenseType; }

    public Integer getConcurrentLimit() { return concurrentLimit; }
    public void setConcurrentLimit(Integer concurrentLimit) { this.concurrentLimit = concurrentLimit; }

    public Integer getConcurrentUserLimit() { return concurrentLimit; }
    public void setConcurrentUserLimit(Integer concurrentLimit) { this.concurrentLimit = concurrentLimit; }

    public Integer getActiveCheckouts() { return activeCheckouts; }
    public void setActiveCheckouts(Integer activeCheckouts) { this.activeCheckouts = activeCheckouts; }

    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }

    public Double getUtilizationPercentage() { return utilizationPercentage; }
    public void setUtilizationPercentage(Double utilizationPercentage) { this.utilizationPercentage = utilizationPercentage; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
