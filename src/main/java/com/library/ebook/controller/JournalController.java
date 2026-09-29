package com.library.ebook.controller;

import com.library.ebook.dto.JournalEntryRequest;
import com.library.ebook.entity.Journal;
import com.library.ebook.entity.JournalEntry;
import com.library.ebook.service.AccountingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journals")
@CrossOrigin(origins = "*")
public class JournalController {

    private final AccountingService accountingService;

    public JournalController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping
    public ResponseEntity<List<Journal>> getAllJournals() {
        return ResponseEntity.ok(accountingService.getAllJournals());
    }

    @GetMapping("/entries")
    public ResponseEntity<List<JournalEntry>> getAllEntries() {
        return ResponseEntity.ok(accountingService.getAllEntries());
    }

    @GetMapping("/entries/{id}")
    public ResponseEntity<JournalEntry> getEntryById(@PathVariable Long id) {
        return ResponseEntity.ok(accountingService.getEntryById(id));
    }

    @PostMapping("/entries")
    public ResponseEntity<JournalEntry> createManualEntry(@Valid @RequestBody JournalEntryRequest request) {
        JournalEntry created = accountingService.postManualJournalEntry(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
