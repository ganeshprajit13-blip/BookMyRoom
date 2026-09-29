package com.library.ebook.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "journal_items")
public class JournalItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "journal_entry_id", nullable = false)
    @JsonBackReference
    private JournalEntry journalEntry;

    @NotNull(message = "Account is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private ChartOfAccount account;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    @Column(length = 255)
    private String description;

    public JournalItem() {}

    public JournalItem(ChartOfAccount account, BigDecimal debit, BigDecimal credit, String description) {
        this.account = account;
        this.debit = debit != null ? debit : BigDecimal.ZERO;
        this.credit = credit != null ? credit : BigDecimal.ZERO;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        if (this.debit == null) this.debit = BigDecimal.ZERO;
        if (this.credit == null) this.credit = BigDecimal.ZERO;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public JournalEntry getJournalEntry() { return journalEntry; }
    public void setJournalEntry(JournalEntry journalEntry) { this.journalEntry = journalEntry; }

    public ChartOfAccount getAccount() { return account; }
    public void setAccount(ChartOfAccount account) { this.account = account; }

    public BigDecimal getDebit() { return debit; }
    public void setDebit(BigDecimal debit) { this.debit = debit; }

    public BigDecimal getCredit() { return credit; }
    public void setCredit(BigDecimal credit) { this.credit = credit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
