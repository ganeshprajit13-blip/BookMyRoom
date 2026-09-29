package com.library.ebook.dto;

public class HoldRequest {

    private Long patronId;
    private Long ebookId;
    private Long licenseId;

    public HoldRequest() {}

    public HoldRequest(Long patronId, Long ebookId) {
        this.patronId = patronId;
        this.ebookId = ebookId;
    }

    public HoldRequest(Long patronId, Long ebookId, Long licenseId) {
        this.patronId = patronId;
        this.ebookId = ebookId;
        this.licenseId = licenseId;
    }

    public Long getPatronId() { return patronId; }
    public void setPatronId(Long patronId) { this.patronId = patronId; }

    public Long getEbookId() { return ebookId; }
    public void setEbookId(Long ebookId) { this.ebookId = ebookId; }

    public Long getLicenseId() { return licenseId; }
    public void setLicenseId(Long licenseId) { this.licenseId = licenseId; }
}
