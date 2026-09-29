package com.library.ebook.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class JournalEntryRequest {

    @NotNull(message = "Journal ID is required")
    private Long journalId;

    private LocalDate entryDate;

    private String reference;

    private String description;

    @NotEmpty(message = "Journal entry must have at least one debit and credit line")
    private List<JournalLineRequest> lines;

    public JournalEntryRequest() {}

    public Long getJournalId() { return journalId; }
    public void setJournalId(Long journalId) { this.journalId = journalId; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<JournalLineRequest> getLines() { return lines; }
    public void setLines(List<JournalLineRequest> lines) { this.lines = lines; }
}
