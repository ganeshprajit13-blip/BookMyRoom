package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "checkouts")
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Patron is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patron_id", nullable = false)
    private Contact patron;

    @NotNull(message = "E-Book is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ebook_id", nullable = false)
    private Ebook ebook;

    @NotNull(message = "License is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @Column(name = "access_key", nullable = false, length = 60)
    private String accessKey;

    @NotNull(message = "Checkout date is required")
    @Column(name = "checkout_date", nullable = false)
    private LocalDateTime checkoutDate;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "returned_date")
    private LocalDateTime returnedDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckoutStatus status = CheckoutStatus.ACTIVE;

    public Checkout() {}

    public Checkout(Contact patron, Ebook ebook, License license, String accessKey,
                    LocalDateTime checkoutDate, LocalDateTime expiryDate) {
        this.patron = patron;
        this.ebook = ebook;
        this.license = license;
        this.accessKey = accessKey;
        this.checkoutDate = checkoutDate;
        this.expiryDate = expiryDate;
        this.status = CheckoutStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.checkoutDate == null) {
            this.checkoutDate = LocalDateTime.now();
        }
        if (this.expiryDate == null) {
            this.expiryDate = this.checkoutDate.plusDays(14); // Standard 14-day loan
        }
        if (this.status == null) {
            this.status = CheckoutStatus.ACTIVE;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contact getPatron() { return patron; }
    public void setPatron(Contact patron) { this.patron = patron; }

    public Ebook getEbook() { return ebook; }
    public void setEbook(Ebook ebook) { this.ebook = ebook; }

    public License getLicense() { return license; }
    public void setLicense(License license) { this.license = license; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public LocalDateTime getCheckoutDate() { return checkoutDate; }
    public void setCheckoutDate(LocalDateTime checkoutDate) { this.checkoutDate = checkoutDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public LocalDateTime getReturnedDate() { return returnedDate; }
    public void setReturnedDate(LocalDateTime returnedDate) { this.returnedDate = returnedDate; }

    public CheckoutStatus getStatus() { return status; }
    public void setStatus(CheckoutStatus status) { this.status = status; }
}
