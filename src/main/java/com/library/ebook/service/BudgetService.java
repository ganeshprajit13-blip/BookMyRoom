package com.library.ebook.service;

import com.library.ebook.dto.BudgetReportDto;
import com.library.ebook.entity.AnalyticAccount;
import com.library.ebook.entity.Budget;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.AnalyticAccountRepository;
import com.library.ebook.repository.BudgetRepository;
import com.library.ebook.repository.JournalItemRepository;
import com.library.ebook.repository.VendorBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final VendorBillRepository vendorBillRepository;
    private final JournalItemRepository journalItemRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         AnalyticAccountRepository analyticAccountRepository,
                         VendorBillRepository vendorBillRepository,
                         JournalItemRepository journalItemRepository) {
        this.budgetRepository = budgetRepository;
        this.analyticAccountRepository = analyticAccountRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.journalItemRepository = journalItemRepository;
    }

    // Analytic Accounts
    public List<AnalyticAccount> getAllAnalyticAccounts() {
        return analyticAccountRepository.findAll();
    }

    public AnalyticAccount getAnalyticAccountById(Long id) {
        return analyticAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Analytic Account not found with ID: " + id));
    }

    public AnalyticAccount createAnalyticAccount(AnalyticAccount account) {
        return analyticAccountRepository.save(account);
    }

    // Budgets
    public List<Budget> getAllBudgets() {
        List<Budget> budgets = budgetRepository.findAll();
        budgets.forEach(this::computeBudgetActuals);
        return budgets;
    }

    public Budget getBudgetById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with ID: " + id));
        computeBudgetActuals(budget);
        return budget;
    }

    public Budget createBudget(Budget budget) {
        if (budget.getAnalyticAccount() == null || budget.getAnalyticAccount().getId() == null) {
            throw new BusinessValidationException("Analytic account is required for a budget.");
        }
        AnalyticAccount account = analyticAccountRepository.findById(budget.getAnalyticAccount().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Analytic Account not found with ID: " + budget.getAnalyticAccount().getId()));

        if (budget.getEndDate() != null && budget.getStartDate() != null && budget.getEndDate().isBefore(budget.getStartDate())) {
            throw new BusinessValidationException("Budget end date cannot be before start date.");
        }

        budget.setAnalyticAccount(account);
        Budget saved = budgetRepository.save(budget);
        computeBudgetActuals(saved);
        return saved;
    }

    /**
     * Rule 8: Calculate actual licensing expenditure dynamically from posted transactions.
     * We aggregate posted vendor bills within budget dates or expense journal debits.
     */
    public void computeBudgetActuals(Budget budget) {
        BigDecimal actual = vendorBillRepository.sumAmountByDateRange(budget.getStartDate(), budget.getEndDate());
        if (actual == null) {
            // Alternatively fallback to expense account 5010 or total posted bills
            actual = vendorBillRepository.sumTotalPostedBills();
        }
        if (actual == null) {
            actual = BigDecimal.ZERO;
        }

        budget.setActualExpenditure(actual);
        BigDecimal remaining = budget.getPlannedAmount().subtract(actual);
        budget.setRemainingBudget(remaining);
        budget.setVariance(remaining); // Planned - Actual

        if (budget.getPlannedAmount().compareTo(BigDecimal.ZERO) > 0) {
            double util = actual.divide(budget.getPlannedAmount(), 4, RoundingMode.HALF_UP).doubleValue() * 100.0;
            budget.setUtilizationPercentage(Math.round(util * 10.0) / 10.0);
        } else {
            budget.setUtilizationPercentage(0.0);
        }
    }

    public List<BudgetReportDto> getBudgetReports() {
        return getAllBudgets().stream().map(budget -> {
            BudgetReportDto dto = new BudgetReportDto();
            dto.setBudgetId(budget.getId());
            dto.setBudgetName(budget.getName());
            dto.setDepartment(budget.getAnalyticAccount() != null ? budget.getAnalyticAccount().getName() : "N/A");
            dto.setFiscalYear(budget.getFiscalYear());
            dto.setPlannedBudget(budget.getPlannedAmount());
            dto.setActualLicensingExpenditure(budget.getActualExpenditure());
            dto.setRemainingBudget(budget.getRemainingBudget());
            dto.setVariance(budget.getVariance());
            dto.setUtilizationPercentage(budget.getUtilizationPercentage());
            dto.setStatus(budget.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }
}
