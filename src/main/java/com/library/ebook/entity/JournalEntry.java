package com.library.ebook.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Entry number is required")
    @Column(name = "entry_number", nullable = false, unique = true, length = 50)
    private String entryNumber;

    @NotNull(message = "Journal is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "journal_id", nullable = false)
    private Journal journal;

    @NotNull(message = "Entry date is required")
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(length = 100)
    private String reference;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_debit", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalDebit = BigDecimal.ZERO;

    @Column(name = "total_credit", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCredit = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean posted = true;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<JournalItem> lines = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public JournalEntry() {}

    public JournalEntry(String entryNumber, Journal journal, LocalDate entryDate, String reference, String description) {
        this.entryNumber = entryNumber;
        this.journal = journal;
        this.entryDate = entryDate;
        this.reference = reference;
        this.description = description;
        this.posted = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.totalDebit == null) {
            this.totalDebit = BigDecimal.ZERO;
        }
        if (this.totalCredit == null) {
            this.totalCredit = BigDecimal.ZERO;
        }
        if (this.posted == null) {
            this.posted = true;
        }
    }

    public void addLine(JournalItem item) {
        lines.add(item);
        item.setJournalEntry(this);
    }

    public boolean isBalanced() {
        if (totalDebit == null || totalCredit == null) return false;
        return totalDebit.compareTo(totalCredit) == 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntryNumber() { return entryNumber; }
    public void setEntryNumber(String entryNumber) { this.entryNumber = entryNumber; }

    public Journal getJournal() { return journal; }
    public void setJournal(Journal journal) { this.journal = journal; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }

    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }

    public Boolean getPosted() { return posted; }
    public void setPosted(Boolean posted) { this.posted = posted; }

    public List<JournalItem> getLines() { return lines; }
    public void setLines(List<JournalItem> lines) { this.lines = lines; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
