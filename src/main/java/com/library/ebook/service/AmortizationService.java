package com.library.ebook.service;

import com.library.ebook.dto.AmortizationResponse;
import com.library.ebook.entity.ChartOfAccount;
import com.library.ebook.entity.JournalEntry;
import com.library.ebook.entity.JournalType;
import com.library.ebook.entity.License;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AmortizationService {

    private final LicenseRepository licenseRepository;
    private final AccountingService accountingService;

    public AmortizationService(LicenseRepository licenseRepository, AccountingService accountingService) {
        this.licenseRepository = licenseRepository;
        this.accountingService = accountingService;
    }

    /**
     * Runs periodic amortization for a specific license.
     * Calculates monthly amortization = purchaseCost / amortizationPeriodMonths
     * Posts balanced journal entry:
     * Debit: 5010 - Digital Amortization & Licensing Expenses
     * Credit: 1020 - Digital Asset Inventory
     */
    public AmortizationResponse amortizeLicense(Long licenseId) {
        License license = licenseRepository.findById(licenseId)
                .orElseThrow(() -> new ResourceNotFoundException("License not found with ID: " + licenseId));

        if (license.getRemainingAssetValue() == null || license.getRemainingAssetValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("License " + license.getLicenseNumber() + " is already fully amortized (Remaining: 0).");
        }

        int months = license.getAmortizationPeriod() != null && license.getAmortizationPeriod() > 0 ? license.getAmortizationPeriod() : 60;
        BigDecimal monthlyAmount = license.getPurchaseCost().divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);

        // Do not amortize more than remaining value
        if (monthlyAmount.compareTo(license.getRemainingAssetValue()) > 0) {
            monthlyAmount = license.getRemainingAssetValue();
        }

        // Fetch Accounts
        ChartOfAccount expenseAccount = accountingService.getAccountByCode("5010"); // Digital Amortization & Licensing Expenses
        ChartOfAccount assetAccount = accountingService.getAccountByCode("1020");   // Digital Asset Inventory

        // Post Journal Entry
        String ref = "AMORT-" + license.getLicenseNumber();
        String desc = "Monthly digital asset amortization for license " + license.getLicenseNumber() + " (" + license.getEbook().getTitle() + ")";
        JournalEntry je = accountingService.postBalancedTransaction(
                JournalType.GENERAL, ref, desc, expenseAccount, assetAccount, monthlyAmount);

        // Update Remaining Asset Value on License
        BigDecimal newRemaining = license.getRemainingAssetValue().subtract(monthlyAmount);
        license.setRemainingAssetValue(newRemaining);
        licenseRepository.save(license);

        AmortizationResponse response = new AmortizationResponse();
        response.setLicenseId(license.getId());
        response.setLicenseNumber(license.getLicenseNumber());
        response.setEbookTitle(license.getEbook() != null ? license.getEbook().getTitle() : "Unknown");
        response.setOriginalAssetValue(license.getPurchaseCost());
        response.setAmortizationPeriodMonths(months);
        response.setMonthlyAmortizationAmount(monthlyAmount);
        response.setRemainingAssetValue(newRemaining);
        response.setJournalEntryNumber(je.getEntryNumber());
        response.setMessage("Amortization processed successfully.");
        response.setAccountingNote("Note: This is a simplified educational straight-line amortization model and not an official GASB/municipal standard substitute.");

        return response;
    }

    /**
     * Run monthly amortization across all active licenses that have remaining asset value.
     */
    public List<AmortizationResponse> amortizeAllActiveLicenses() {
        List<License> licenses = licenseRepository.findAll();
        List<AmortizationResponse> results = new ArrayList<>();

        for (License l : licenses) {
            if (l.getRemainingAssetValue() != null && l.getRemainingAssetValue().compareTo(BigDecimal.ZERO) > 0) {
                try {
                    results.add(amortizeLicense(l.getId()));
                } catch (Exception ignored) {}
            }
        }
        return results;
    }
}
