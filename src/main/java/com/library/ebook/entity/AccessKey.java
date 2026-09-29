package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_keys")
public class AccessKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Access key code is required")
    @Column(name = "access_key", nullable = false, unique = true, length = 60)
    private String accessKey;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "checkout_id")
    private Checkout checkout;

    @NotNull(message = "Issued date is required")
    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;

    @NotNull(message = "Expiry date is required")
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccessKeyStatus status = AccessKeyStatus.ACTIVE;

    public AccessKey() {}

    public AccessKey(String accessKey, Checkout checkout, LocalDateTime issuedDate, LocalDateTime expiryDate) {
        this.accessKey = accessKey;
        this.checkout = checkout;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
        this.status = AccessKeyStatus.ACTIVE;
    }

    @PrePersist
    protected void onCreate() {
        if (this.issuedDate == null) {
            this.issuedDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = AccessKeyStatus.ACTIVE;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public Checkout getCheckout() { return checkout; }
    public void setCheckout(Checkout checkout) { this.checkout = checkout; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public AccessKeyStatus getStatus() { return status; }
    public void setStatus(AccessKeyStatus status) { this.status = status; }
}
