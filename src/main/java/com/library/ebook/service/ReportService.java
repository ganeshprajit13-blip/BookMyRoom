package com.library.ebook.service;

import com.library.ebook.dto.*;
import com.library.ebook.entity.*;
import com.library.ebook.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final EbookRepository ebookRepository;
    private final LicenseRepository licenseRepository;
    private final ContactRepository contactRepository;
    private final CheckoutRepository checkoutRepository;
    private final EbookHoldRepository holdRepository;
    private final VendorBillRepository vendorBillRepository;
    private final CustomerInvoiceRepository customerInvoiceRepository;
    private final ChartOfAccountRepository accountRepository;
    private final JournalEntryRepository entryRepository;
    private final BudgetService budgetService;
    private final LicenseService licenseService;

    public ReportService(EbookRepository ebookRepository,
                         LicenseRepository licenseRepository,
                         ContactRepository contactRepository,
                         CheckoutRepository checkoutRepository,
                         EbookHoldRepository holdRepository,
                         VendorBillRepository vendorBillRepository,
                         CustomerInvoiceRepository customerInvoiceRepository,
                         ChartOfAccountRepository accountRepository,
                         JournalEntryRepository entryRepository,
                         BudgetService budgetService,
                         LicenseService licenseService) {
        this.ebookRepository = ebookRepository;
        this.licenseRepository = licenseRepository;
        this.contactRepository = contactRepository;
        this.checkoutRepository = checkoutRepository;
        this.holdRepository = holdRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.customerInvoiceRepository = customerInvoiceRepository;
        this.accountRepository = accountRepository;
        this.entryRepository = entryRepository;
        this.budgetService = budgetService;
        this.licenseService = licenseService;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalEbooks(ebookRepository.count());
        stats.setActiveLicenses(licenseRepository.countByStatus(LicenseStatus.ACTIVE));
        stats.setTotalPatrons(contactRepository.countByContactType(ContactType.CUSTOMER));
        stats.setTotalPublishers(contactRepository.countByContactType(ContactType.VENDOR));
        stats.setActiveCheckouts(checkoutRepository.countByStatus(CheckoutStatus.ACTIVE));
        stats.setActiveHolds(holdRepository.countByStatus(HoldStatus.WAITING));

        // Available capacity across active licenses
        List<License> activeLicenses = licenseRepository.findByStatus(LicenseStatus.ACTIVE);
        int totalAvailableCapacity = 0;
        for (License l : activeLicenses) {
            totalAvailableCapacity += l.getAvailableCapacity();
        }
        stats.setAvailableCapacity(totalAvailableCapacity);

        // Expiring licenses (within 30 days)
        stats.setExpiringLicenses(licenseRepository.findExpiringLicenses(LocalDate.now().plusDays(30)).size());

        // Digital Asset Value
        BigDecimal assetVal = licenseRepository.sumRemainingAssetValue();
        stats.setDigitalAssetValue(assetVal != null ? assetVal : BigDecimal.ZERO);

        // Licensing Expenditure (total posted bills)
        BigDecimal exp = vendorBillRepository.sumTotalPostedBills();
        stats.setLicensingExpense(exp != null ? exp : BigDecimal.ZERO);

        // Membership Revenue (total posted customer invoices)
        BigDecimal rev = customerInvoiceRepository.sumTotalPostedInvoices();
        stats.setMembershipRevenue(rev != null ? rev : BigDecimal.ZERO);

        // Outstanding Publisher Payables
        BigDecimal payables = vendorBillRepository.sumOutstandingPayables();
        stats.setOutstandingPublisherPayables(payables != null ? payables : BigDecimal.ZERO);

        // Outstanding Patron Receivables
        BigDecimal receivables = customerInvoiceRepository.sumOutstandingReceivables();
        stats.setOutstandingPatronReceivables(receivables != null ? receivables : BigDecimal.ZERO);

        // Budget utilization average
        List<Budget> budgets = budgetService.getAllBudgets();
        if (!budgets.isEmpty()) {
            double avg = budgets.stream().mapToDouble(Budget::getUtilizationPercentage).average().orElse(0.0);
            stats.setBudgetUtilizationPct(Math.round(avg * 10.0) / 10.0);
        } else {
            stats.setBudgetUtilizationPct(0.0);
        }

        // Recent 5 Transactions / Journal Entries
        List<JournalEntry> recentEntries = entryRepository.findTop20ByOrderByEntryDateDescIdDesc();
        List<Map<String, Object>> recentTxns = new ArrayList<>();
        int count = 0;
        for (JournalEntry je : recentEntries) {
            if (count++ >= 8) break;
            Map<String, Object> map = new HashMap<>();
            map.put("entryNumber", je.getEntryNumber());
            map.put("date", je.getEntryDate());
            map.put("reference", je.getReference());
            map.put("description", je.getDescription());
            map.put("amount", je.getTotalDebit());
            map.put("journal", je.getJournal().getName());
            recentTxns.add(map);
        }
        stats.setRecentTransactions(recentTxns);

        // License utilization summary
        List<Map<String, Object>> licSummary = new ArrayList<>();
        for (License l : activeLicenses) {
            Map<String, Object> item = new HashMap<>();
            item.put("licenseNumber", l.getLicenseNumber());
            item.put("ebookTitle", l.getEbook() != null ? l.getEbook().getTitle() : "N/A");
            item.put("activeCheckouts", l.getActiveCheckoutCount());
            item.put("capacity", l.getConcurrentUserLimit());
            item.put("utilization", Math.round(l.getUtilizationPercentage() * 10.0) / 10.0);
            licSummary.add(item);
        }
        stats.setLicenseUtilizationSummary(licSummary);

        return stats;
    }

    public BalanceSheetDto getBalanceSheet() {
        BalanceSheetDto dto = new BalanceSheetDto();
        dto.setAsOfDate(LocalDate.now());

        Map<String, BigDecimal> assets = new LinkedHashMap<>();
        Map<String, BigDecimal> liabilities = new LinkedHashMap<>();

        BigDecimal totalAssets = BigDecimal.ZERO;
        BigDecimal totalLiab = BigDecimal.ZERO;

        List<ChartOfAccount> accounts = accountRepository.findAll();
        for (ChartOfAccount acc : accounts) {
            BigDecimal bal = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
            if (acc.getType() == AccountType.ASSET) {
                assets.put(acc.getName() + " (" + acc.getCode() + ")", bal);
                totalAssets = totalAssets.add(bal);
            } else if (acc.getType() == AccountType.LIABILITY) {
                liabilities.put(acc.getName() + " (" + acc.getCode() + ")", bal);
                totalLiab = totalLiab.add(bal);
            }
        }

        dto.setAssets(assets);
        dto.setTotalAssets(totalAssets);
        dto.setLiabilities(liabilities);
        dto.setTotalLiabilities(totalLiab);
        dto.setNetWorthEquity(totalAssets.subtract(totalLiab));

        return dto;
    }

    public ProfitAndLossDto getProfitAndLoss() {
        ProfitAndLossDto dto = new ProfitAndLossDto();
        dto.setFromDate(LocalDate.now().withDayOfYear(1));
        dto.setToDate(LocalDate.now());

        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expenses = new LinkedHashMap<>();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        List<ChartOfAccount> accounts = accountRepository.findAll();
        for (ChartOfAccount acc : accounts) {
            BigDecimal bal = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
            if (acc.getType() == AccountType.INCOME) {
                income.put(acc.getName() + " (" + acc.getCode() + ")", bal);
                totalIncome = totalIncome.add(bal);
            } else if (acc.getType() == AccountType.EXPENSE) {
                expenses.put(acc.getName() + " (" + acc.getCode() + ")", bal);
                totalExpenses = totalExpenses.add(bal);
            }
        }

        dto.setIncome(income);
        dto.setTotalIncome(totalIncome);
        dto.setExpenses(expenses);
        dto.setTotalExpenses(totalExpenses);
        dto.setNetProfitLoss(totalIncome.subtract(totalExpenses));

        return dto;
    }

    public List<PatronActivityDto> getPatronActivityReport() {
        List<Contact> patrons = contactRepository.findByContactType(ContactType.CUSTOMER);
        return patrons.stream().map(patron -> {
            PatronActivityDto dto = new PatronActivityDto();
            dto.setPatronId(patron.getId());
            dto.setPatronName(patron.getName());
            dto.setEmail(patron.getEmail());
            dto.setPhone(patron.getPhone());
            dto.setActiveCheckouts(checkoutRepository.countByPatronIdAndStatus(patron.getId(), CheckoutStatus.ACTIVE));
            dto.setReturnedBooks(checkoutRepository.countByPatronIdAndStatus(patron.getId(), CheckoutStatus.RETURNED));
            dto.setExpiredAccess(checkoutRepository.countByPatronIdAndStatus(patron.getId(), CheckoutStatus.EXPIRED));
            dto.setActiveHolds(holdRepository.countByPatronIdAndStatus(patron.getId(), HoldStatus.WAITING));
            dto.setMembershipStatus(patron.getStatus());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<LicenseUtilizationDto> getLicenseUtilizationReport() {
        return licenseService.getLicenseUtilizationList();
    }

    public List<BudgetReportDto> getBudgetReport() {
        return budgetService.getBudgetReports();
    }
}
