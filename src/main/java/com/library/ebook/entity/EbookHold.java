package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "ebook_holds")
public class EbookHold {

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "license_id")
    private License license;

    @NotNull(message = "Hold date is required")
    @Column(name = "hold_date", nullable = false)
    private LocalDateTime holdDate;

    @Column(name = "queue_position", nullable = false)
    private Integer queuePosition = 1;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HoldStatus status = HoldStatus.WAITING;

    public EbookHold() {}

    public EbookHold(Contact patron, Ebook ebook, License license, Integer queuePosition) {
        this.patron = patron;
        this.ebook = ebook;
        this.license = license;
        this.queuePosition = queuePosition != null ? queuePosition : 1;
        this.holdDate = LocalDateTime.now();
        this.status = HoldStatus.WAITING;
    }

    @PrePersist
    protected void onCreate() {
        if (this.holdDate == null) {
            this.holdDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = HoldStatus.WAITING;
        }
        if (this.queuePosition == null) {
            this.queuePosition = 1;
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

    public LocalDateTime getHoldDate() { return holdDate; }
    public void setHoldDate(LocalDateTime holdDate) { this.holdDate = holdDate; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public HoldStatus getStatus() { return status; }
    public void setStatus(HoldStatus status) { this.status = status; }
}
