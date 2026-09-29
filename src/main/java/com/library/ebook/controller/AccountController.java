package com.library.ebook.controller;

import com.library.ebook.entity.ChartOfAccount;
import com.library.ebook.service.AccountingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    private final AccountingService accountingService;

    public AccountController(AccountingService accountingService) {
        this.accountingService = accountingService;
    }

    @GetMapping
    public ResponseEntity<List<ChartOfAccount>> getAllAccounts() {
        return ResponseEntity.ok(accountingService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChartOfAccount> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountingService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<ChartOfAccount> createAccount(@Valid @RequestBody ChartOfAccount account) {
        ChartOfAccount created = accountingService.createAccount(account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
