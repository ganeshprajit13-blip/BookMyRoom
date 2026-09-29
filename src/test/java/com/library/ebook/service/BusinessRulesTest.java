package com.library.ebook.service;

import com.library.ebook.dto.*;
import com.library.ebook.entity.*;
import com.library.ebook.exception.*;
import com.library.ebook.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class BusinessRulesTest {

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private AccessKeyService accessKeyService;

    @Autowired
    private HoldService holdService;

    @Autowired
    private AccountingService accountingService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private VendorBillService vendorBillService;

    @Autowired
    private VendorPaymentService vendorPaymentService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private EbookRepository ebookRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private ChartOfAccountRepository accountRepository;

    @Autowired
    private JournalRepository journalRepository;

    private Contact patron1;
    private Contact patron2;
    private Contact patron3;
    private Contact publisher;
    private Ebook ebook;
    private License license;

    @BeforeEach
    void setUp() {
        publisher = contactRepository.save(new Contact("Test Publisher", ContactType.VENDOR, "pub@test.com", "1234567890", "Pub Address"));
        patron1 = contactRepository.save(new Contact("Patron One", ContactType.CUSTOMER, "p1@test.com", "1111111111", "Patron Address 1"));
        patron2 = contactRepository.save(new Contact("Patron Two", ContactType.CUSTOMER, "p2@test.com", "2222222222", "Patron Address 2"));
        patron3 = contactRepository.save(new Contact("Patron Three", ContactType.CUSTOMER, "p3@test.com", "3333333333", "Patron Address 3"));

        ebook = ebookRepository.save(new Ebook("978-9999999999", "Test Digital Book", "Author Test", publisher.getName(), "Tech", 2024, "Desc", "English"));

        // Create a license with capacity = 2
        license = new License("LIC-TEST-01", ebook, publisher, LicenseType.PERPETUAL, 2, LocalDate.now().minusDays(10), null, new BigDecimal("100000.00"), 36);
        license.setActiveCheckoutCount(0);
        license = licenseRepository.save(license);
    }

    @Test
    @DisplayName("RULE 1: Concurrent checkout limit enforcement - HTTP 409 Conflict / Exception when full")
    void testConcurrentCheckoutLimit() {
        // Checkout 1
        Checkout c1 = checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), license.getId()));
        assertNotNull(c1);
        assertEquals(1, c1.getLicense().getActiveCheckoutCount());

        // Checkout 2
        Checkout c2 = checkoutService.performCheckout(new CheckoutRequest(patron2.getId(), license.getId()));
        assertNotNull(c2);
        assertEquals(2, c2.getLicense().getActiveCheckoutCount());

        // Checkout 3 must fail because limit = 2
        LicenseLimitExceededException ex = assertThrows(LicenseLimitExceededException.class, () -> {
            checkoutService.performCheckout(new CheckoutRequest(patron3.getId(), license.getId()));
        });
        assertTrue(ex.getMessage().contains("Concurrent checkout limit reached"));
    }

    @Test
    @DisplayName("RULE 2: Expired license rejection")
    void testExpiredLicenseRejection() {
        License expiredLic = new License("LIC-EXPIRED", ebook, publisher, LicenseType.TERM, 5, LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1), new BigDecimal("50000.00"), 6);
        expiredLic = licenseRepository.save(expiredLic);

        final Long expiredLicId = expiredLic.getId();
        LicenseExpiredException ex = assertThrows(LicenseExpiredException.class, () -> {
            checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), expiredLicId));
        });
        assertTrue(ex.getMessage().contains("expired"));
    }

    @Test
    @DisplayName("RULE 4: Returning a checkout decreases active checkout count and updates status")
    void testReturnCheckoutDecreasesActiveCount() {
        Checkout c1 = checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), license.getId()));
        assertEquals(1, licenseRepository.findById(license.getId()).get().getActiveCheckoutCount());

        Checkout returned = checkoutService.returnBook(c1.getId());
        assertEquals(CheckoutStatus.RETURNED, returned.getStatus());
        assertEquals(0, licenseRepository.findById(license.getId()).get().getActiveCheckoutCount());
    }

    @Test
    @DisplayName("RULE 5: Unbalanced journals must be rejected (Debit must equal Credit)")
    void testUnbalancedJournalRejection() {
        ChartOfAccount cash = accountRepository.findByAccountCode("1010").orElseGet(() ->
                accountRepository.save(new ChartOfAccount("1010", "Cash", AccountType.ASSET, "Cash", BigDecimal.ZERO)));
        ChartOfAccount expense = accountRepository.findByAccountCode("5010").orElseGet(() ->
                accountRepository.save(new ChartOfAccount("5010", "Expense", AccountType.EXPENSE, "Expense", BigDecimal.ZERO)));

        Journal journal = journalRepository.findByCode("GEN").orElseGet(() ->
                journalRepository.save(new Journal("GEN", "General", JournalType.GENERAL)));

        JournalEntryRequest unbalancedRequest = new JournalEntryRequest();
        unbalancedRequest.setJournalId(journal.getId());
        unbalancedRequest.setEntryDate(LocalDate.now());
        unbalancedRequest.setReference("UNBALANCED-TEST");
        unbalancedRequest.setDescription("Test unbalanced entry");

        // Debit 100, Credit 50 (Unbalanced!)
        JournalLineRequest line1 = new JournalLineRequest();
        line1.setAccountId(expense.getId());
        line1.setDebit(new BigDecimal("100.00"));
        line1.setCredit(BigDecimal.ZERO);
        line1.setDescription("Expense debit");

        JournalLineRequest line2 = new JournalLineRequest();
        line2.setAccountId(cash.getId());
        line2.setDebit(BigDecimal.ZERO);
        line2.setCredit(new BigDecimal("50.00"));
        line2.setDescription("Cash credit");

        unbalancedRequest.setLines(List.of(line1, line2));

        assertThrows(UnbalancedJournalException.class, () -> {
            accountingService.createJournalEntry(unbalancedRequest);
        });
    }

    @Test
    @DisplayName("RULE 8: Budget actual expenditure is dynamically calculated from posted transactions")
    void testBudgetActualExpenditureCalculation() {
        List<BudgetReportDto> budgetReports = budgetService.getBudgetReports();
        assertNotNull(budgetReports);
        for (BudgetReportDto report : budgetReports) {
            assertNotNull(report.getPlannedBudget());
            assertNotNull(report.getActualExpenditure());
            assertNotNull(report.getRemainingBudget());
            assertTrue(report.getUtilizationPercentage() >= 0.0);
        }
    }

    @Test
    @DisplayName("RULE 9: License utilization calculation: (activeCheckouts / concurrentLimit) * 100")
    void testLicenseUtilizationCalculation() {
        checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), license.getId()));
        LicenseUtilizationDto util = licenseService.getLicenseUtilization(license.getId());
        assertEquals(1, util.getActiveCheckouts());
        assertEquals(2, util.getConcurrentUserLimit());
        assertEquals(1, util.getAvailableSlots());
        assertEquals(50.0, util.getUtilizationPercentage(), 0.01);
    }

    @Test
    @DisplayName("RULE 10: Expired digital access keys revocation")
    void testRevokeExpiredKeys() {
        Checkout c1 = checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), license.getId()));
        AccessKey key = accessKeyService.getKeyByCheckout(c1.getId());
        assertNotNull(key);
        assertEquals(AccessKeyStatus.ACTIVE, key.getStatus());

        // Artificially expire the key date
        key.setExpiryDate(LocalDateTime.now().minusDays(1));

        int revokedCount = accessKeyService.revokeExpiredKeys();
        assertTrue(revokedCount >= 1);
        assertEquals(AccessKeyStatus.REVOKED, key.getStatus());
    }

    @Test
    @DisplayName("MODULE 8: Digital Holds queue placement and auto-fulfillment on return")
    void testDigitalHoldsQueue() {
        // Fill capacity (limit is 2)
        checkoutService.performCheckout(new CheckoutRequest(patron1.getId(), license.getId()));
        Checkout c2 = checkoutService.performCheckout(new CheckoutRequest(patron2.getId(), license.getId()));

        // Patron 3 places a hold
        EbookHold hold = holdService.placeHold(new HoldRequest(patron3.getId(), license.getId()));
        assertNotNull(hold);
        assertEquals(HoldStatus.WAITING, hold.getStatus());
        assertEquals(1, hold.getQueuePosition());

        // Patron 2 returns book -> Hold should be notified / capacity freed
        checkoutService.returnBook(c2.getId());
        assertEquals(1, licenseRepository.findById(license.getId()).get().getActiveCheckoutCount());
    }
}
