package com.library.ebook.dto;

import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "patronId is required")
    private Long patronId;

    @NotNull(message = "licenseId is required")
    private Long licenseId;

    public CheckoutRequest() {}

    public CheckoutRequest(Long patronId, Long licenseId) {
        this.patronId = patronId;
        this.licenseId = licenseId;
    }

    public Long getPatronId() { return patronId; }
    public void setPatronId(Long patronId) { this.patronId = patronId; }

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }
}
