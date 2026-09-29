package com.library.ebook.controller;

import com.library.ebook.entity.AnalyticAccount;
import com.library.ebook.entity.Budget;
import com.library.ebook.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Analytic Accounts
    @GetMapping("/api/analytic-accounts")
    public ResponseEntity<List<AnalyticAccount>> getAllAnalyticAccounts() {
        return ResponseEntity.ok(budgetService.getAllAnalyticAccounts());
    }

    @PostMapping("/api/analytic-accounts")
    public ResponseEntity<AnalyticAccount> createAnalyticAccount(@Valid @RequestBody AnalyticAccount account) {
        AnalyticAccount created = budgetService.createAnalyticAccount(account);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // Budgets
    @GetMapping("/api/budgets")
    public ResponseEntity<List<Budget>> getAllBudgets() {
        return ResponseEntity.ok(budgetService.getAllBudgets());
    }

    @GetMapping("/api/budgets/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.getBudgetById(id));
    }

    @PostMapping("/api/budgets")
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget) {
        Budget created = budgetService.createBudget(budget);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
