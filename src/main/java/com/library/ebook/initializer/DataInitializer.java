package com.library.ebook.initializer;

import com.library.ebook.entity.*;
import com.library.ebook.repository.*;
import com.library.ebook.service.AccountingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ContactRepository contactRepository;
    private final EbookRepository ebookRepository;
    private final LicenseRepository licenseRepository;
    private final CheckoutRepository checkoutRepository;
    private final AccessKeyRepository accessKeyRepository;
    private final EbookHoldRepository holdRepository;
    private final ProductRepository productRepository;
    private final ChartOfAccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final PurchaseOrderRepository poRepository;
    private final VendorBillRepository vendorBillRepository;
    private final VendorPaymentRepository vendorPaymentRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final CustomerPaymentRepository customerPaymentRepository;
    private final AnalyticAccountRepository analyticAccountRepository;
    private final BudgetRepository budgetRepository;
    private final AccountingService accountingService;

    public DataInitializer(ContactRepository contactRepository,
                           EbookRepository ebookRepository,
                           LicenseRepository licenseRepository,
                           CheckoutRepository checkoutRepository,
                           AccessKeyRepository accessKeyRepository,
                           EbookHoldRepository holdRepository,
                           ProductRepository productRepository,
                           ChartOfAccountRepository accountRepository,
                           JournalRepository journalRepository,
                           PurchaseOrderRepository poRepository,
                           VendorBillRepository vendorBillRepository,
                           VendorPaymentRepository vendorPaymentRepository,
                           SalesOrderRepository salesOrderRepository,
                           CustomerInvoiceRepository invoiceRepository,
                           CustomerPaymentRepository customerPaymentRepository,
                           AnalyticAccountRepository analyticAccountRepository,
                           BudgetRepository budgetRepository,
                           AccountingService accountingService) {
        this.contactRepository = contactRepository;
        this.ebookRepository = ebookRepository;
        this.licenseRepository = licenseRepository;
        this.checkoutRepository = checkoutRepository;
        this.accessKeyRepository = accessKeyRepository;
        this.holdRepository = holdRepository;
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.poRepository = poRepository;
        this.vendorBillRepository = vendorBillRepository;
        this.vendorPaymentRepository = vendorPaymentRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.invoiceRepository = invoiceRepository;
        this.customerPaymentRepository = customerPaymentRepository;
        this.analyticAccountRepository = analyticAccountRepository;
        this.budgetRepository = budgetRepository;
        this.accountingService = accountingService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Safe check: do not duplicate data on restarts
        if (contactRepository.count() > 0) {
            log.info("Demo data already loaded. Skipping initialization.");
            return;
        }

        log.info("Initializing Automated Municipal Library E-Book Licensing Demo Data...");

        // 1. Chart of Accounts
        ChartOfAccount cash = accountRepository.save(new ChartOfAccount("1010", "Cash", AccountType.ASSET, "Municipal Operating Cash Account", new BigDecimal("1500000.00")));
        ChartOfAccount digitalAssets = accountRepository.save(new ChartOfAccount("1020", "Digital Asset Inventory", AccountType.ASSET, "Capitalized E-Book Licenses & Content Assets", BigDecimal.ZERO));
        ChartOfAccount receivables = accountRepository.save(new ChartOfAccount("1030", "Patron Receivable", AccountType.ASSET, "Outstanding Patron Membership Fees", BigDecimal.ZERO));
        ChartOfAccount payables = accountRepository.save(new ChartOfAccount("2010", "Publisher Royalty Payables", AccountType.LIABILITY, "Outstanding obligations to E-Book publishers", BigDecimal.ZERO));
        ChartOfAccount subRevenue = accountRepository.save(new ChartOfAccount("4010", "Non-Resident Subscription Revenue", AccountType.INCOME, "Revenue from annual cards and journal passes", BigDecimal.ZERO));
        accountRepository.save(new ChartOfAccount("4020", "Late Access Fees", AccountType.INCOME, "Fees for overdue or extended digital loans", BigDecimal.ZERO));
        ChartOfAccount amortExpense = accountRepository.save(new ChartOfAccount("5010", "Digital Amortization & Licensing Expenses", AccountType.EXPENSE, "Periodic amortization and term license expenses", BigDecimal.ZERO));
        accountRepository.save(new ChartOfAccount("5020", "Server Costs", AccountType.EXPENSE, "Infrastructure and cloud storage fees", BigDecimal.ZERO));

        // 2. Journals
        Journal salesJournal = journalRepository.save(new Journal("SJ", "Sales Journal", JournalType.SALES));
        Journal purchaseJournal = journalRepository.save(new Journal("PJ", "Purchase Journal", JournalType.PURCHASE));
        journalRepository.save(new Journal("BNK", "Bank Journal", JournalType.BANK));
        journalRepository.save(new Journal("CSH", "Cash Journal", JournalType.CASH));
        journalRepository.save(new Journal("GEN", "General Journal", JournalType.GENERAL));

        // 3. Products
        Product prodPerpetual = productRepository.save(new Product("Perpetual E-Book License", ProductType.GOODS, "License", "Single perpetual institutional e-book license bundle", new BigDecimal("500000.00")));
        Product prodNonResident = productRepository.save(new Product("Annual Non-Resident Card", ProductType.SERVICE, "Year", "Annual digital library privileges for non-city residents", new BigDecimal("1500.00")));
        productRepository.save(new Product("Academic Journal Pass", ProductType.SERVICE, "Pass", "Specialized scholarly research database pass", new BigDecimal("3500.00")));

        // 4. Contacts - Publishers (VENDOR)
        Contact pub1 = contactRepository.save(new Contact("Global Academic Publishing", ContactType.VENDOR, "licensing@globalacademic.com", "+1-800-555-0199", "100 Academic Way, Suite 400, New York, NY"));
        Contact pub2 = contactRepository.save(new Contact("Oxford Technical Press", ContactType.VENDOR, "sales@oxfordtechpress.org", "+44-20-7946-0912", "45 Great Russell Street, London, UK"));
        Contact pub3 = contactRepository.save(new Contact("Springer Digital Learning", ContactType.VENDOR, "support@springerdigital.com", "+1-312-555-0144", "88 Knowledge Park, Chicago, IL"));

        // 5. Contacts - Patrons (CUSTOMER)
        Contact pat1 = contactRepository.save(new Contact("Alice Walker", ContactType.CUSTOMER, "alice.walker@email.com", "+1-555-0101", "12 Maple Lane, Springfield", "NON_RESIDENT"));
        Contact pat2 = contactRepository.save(new Contact("Brian Miller", ContactType.CUSTOMER, "brian.m@email.com", "+1-555-0102", "34 Oak Street, Springfield", "NON_RESIDENT"));
        Contact pat3 = contactRepository.save(new Contact("Clara Oswald", ContactType.CUSTOMER, "clara.o@email.com", "+1-555-0103", "56 Pine Boulevard, Springfield", "RESIDENT"));
        Contact pat4 = contactRepository.save(new Contact("David Tennant", ContactType.CUSTOMER, "david.t@email.com", "+1-555-0104", "78 Elm Court, Springfield", "RESIDENT"));
        Contact pat5 = contactRepository.save(new Contact("Emma Watson", ContactType.CUSTOMER, "emma.w@email.com", "+1-555-0105", "90 Cedar Avenue, Springfield", "RESIDENT"));
        Contact pat6 = contactRepository.save(new Contact("Felix Chang", ContactType.CUSTOMER, "felix.c@email.com", "+1-555-0106", "11 Birch Road, Springfield", "RESIDENT"));
        Contact pat7 = contactRepository.save(new Contact("Grace Hopper", ContactType.CUSTOMER, "grace.h@email.com", "+1-555-0107", "22 Willow Way, Springfield", "NON_RESIDENT"));
        Contact pat8 = contactRepository.save(new Contact("Henry Ford", ContactType.CUSTOMER, "henry.f@email.com", "+1-555-0108", "33 Walnut Drive, Springfield", "RESIDENT"));
        Contact pat9 = contactRepository.save(new Contact("Irene Adler", ContactType.CUSTOMER, "irene.a@email.com", "+1-555-0109", "44 Chestnut Circle, Springfield", "NON_RESIDENT"));
        Contact pat10 = contactRepository.save(new Contact("James Wilson", ContactType.CUSTOMER, "james.w@email.com", "+1-555-0110", "55 Magnolia Terrace, Springfield", "RESIDENT"));

        // 6. E-Books (5 Books)
        Ebook ebook1 = ebookRepository.save(new Ebook("978-0000000001", "Introduction to Digital Science", "John Smith", "Global Academic Publishing", "Technology", 2024, "Comprehensive foundational textbook covering computational science, digital archiving, and algorithms.", "English"));
        Ebook ebook2 = ebookRepository.save(new Ebook("978-0000000002", "Municipal Public Administration & Governance", "Elena Gilbert", "Oxford Technical Press", "Public Policy", 2023, "Principles and best practices for municipal leaders, public resource allocation, and civic transparency.", "English"));
        Ebook ebook3 = ebookRepository.save(new Ebook("978-0000000003", "Cloud Computing Architectures for Public Institutions", "Marcus Brody", "Springer Digital Learning", "Computer Science", 2024, "Design frameworks, scalable architectures, and sovereign cloud security in local government.", "English"));
        Ebook ebook4 = ebookRepository.save(new Ebook("978-0000000004", "Urban Planning and Smart Cities", "Sarah Connor", "Global Academic Publishing", "Urban Studies", 2022, "Strategies for modern urban mobility, digital infrastructure, and sustainable smart municipalities.", "English"));
        Ebook ebook5 = ebookRepository.save(new Ebook("978-0000000005", "Financial Management in Modern Municipalities", "Arthur Pendelton", "Oxford Technical Press", "Finance", 2023, "Double-entry municipal accounting, GASB compliance, and capital budget execution.", "English"));

        // 7. E-Book Licenses (3 Licenses with varied concurrency)
        // License 1: Perpetual, Concurrent Limit = 5, E-book 1, Global Academic Publishing
        License lic1 = new License("LIC-0001", ebook1, pub1, LicenseType.PERPETUAL, 5, LocalDate.now().minusMonths(6), null, new BigDecimal("500000.00"), 60);
        lic1.setActiveCheckoutCount(3); // 3 checkouts active, 2 available (60% utilization)
        lic1.setRemainingAssetValue(new BigDecimal("450000.00")); // after 6 months amortization
        lic1 = licenseRepository.save(lic1);

        // License 2: Term, Concurrent Limit = 3, E-book 2, Oxford Technical Press
        License lic2 = new License("LIC-0002", ebook2, pub2, LicenseType.TERM, 3, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10), new BigDecimal("240000.00"), 12);
        lic2.setActiveCheckoutCount(1); // 1 checkout active, 2 available (33.3% utilization)
        lic2.setRemainingAssetValue(new BigDecimal("200000.00"));
        lic2 = licenseRepository.save(lic2);

        // License 3: Perpetual, Concurrent Limit = 2, E-book 3, Springer Digital Learning (Demonstrates 100% capacity and holds!)
        License lic3 = new License("LIC-0003", ebook3, pub3, LicenseType.PERPETUAL, 2, LocalDate.now().minusMonths(1), null, new BigDecimal("300000.00"), 36);
        lic3.setActiveCheckoutCount(2); // 2 checkouts active, 0 available (100% capacity full!)
        lic3.setRemainingAssetValue(new BigDecimal("291666.67"));
        lic3 = licenseRepository.save(lic3);

        // 8. Checkouts & Access Keys
        // Checkouts for License 1
        createDemoCheckout(pat1, ebook1, lic1, "EBOOK-ACCESS-8F72A91C", LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(9), CheckoutStatus.ACTIVE);
        createDemoCheckout(pat2, ebook1, lic1, "EBOOK-ACCESS-4D91B2E7", LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(11), CheckoutStatus.ACTIVE);
        createDemoCheckout(pat3, ebook1, lic1, "EBOOK-ACCESS-C339F01A", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(13), CheckoutStatus.ACTIVE);

        // Checkout for License 2
        createDemoCheckout(pat4, ebook2, lic2, "EBOOK-ACCESS-5B20E891", LocalDateTime.now().minusDays(4), LocalDateTime.now().plusDays(10), CheckoutStatus.ACTIVE);

        // Checkouts for License 3 (Capacity Full: 2/2)
        createDemoCheckout(pat5, ebook3, lic3, "EBOOK-ACCESS-A92B11EF", LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(12), CheckoutStatus.ACTIVE);
        createDemoCheckout(pat6, ebook3, lic3, "EBOOK-ACCESS-77C83DA4", LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(8), CheckoutStatus.ACTIVE);

        // Past returned checkout
        Checkout retCheckout = createDemoCheckout(pat8, ebook1, lic1, "EBOOK-ACCESS-RET9001", LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(6), CheckoutStatus.RETURNED);
        retCheckout.setReturnedDate(LocalDateTime.now().minusDays(7));
        checkoutRepository.save(retCheckout);

        // 9. Digital Holds (For License 3 which is currently full at 2/2 capacity!)
        holdRepository.save(new EbookHold(pat7, ebook3, lic3, 1));
        holdRepository.save(new EbookHold(pat9, ebook3, lic3, 2));

        // 10. Analytic Accounts & Budgets
        AnalyticAccount deptMedia = analyticAccountRepository.save(new AnalyticAccount("City Digital Media Unit", "Digital Content Department", "Allocated for e-book licensing and publisher subscriptions"));
        analyticAccountRepository.save(new AnalyticAccount("Municipal E-Library Operations", "Library Information Systems", "Operational expenses and digital server infrastructure"));

        budgetRepository.save(new Budget("FY 2026-2027 Digital Content Licensing", deptMedia, "2026-2027",
                LocalDate.now().withDayOfYear(1), LocalDate.now().withDayOfYear(1).plusYears(1).minusDays(1), new BigDecimal("2500000.00")));

        // 11. Procurement & Vendor Transactions
        PurchaseOrder po1 = poRepository.save(new PurchaseOrder("PO-2026-001", pub1, LocalDate.now().minusMonths(6), prodPerpetual, 1, new BigDecimal("500000.00"), OrderStatus.BILLED));

        VendorBill bill1 = new VendorBill("BILL-2026-001", pub1, po1, LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(5), new BigDecimal("500000.00"));
        bill1.setPaidAmount(new BigDecimal("250000.00"));
        bill1.setStatus(BillStatus.PARTIALLY_PAID);
        // Post Initial Accounting for Bill:
        JournalEntry jeBill1 = accountingService.postBalancedTransaction(
                JournalType.PURCHASE, bill1.getBillNumber(), "Initial purchase bill for LIC-0001",
                digitalAssets, payables, bill1.getAmount());
        bill1.setJournalEntry(jeBill1);
        vendorBillRepository.save(bill1);

        // Vendor Payment of ₹2,50,000 against Bill 1
        VendorPayment vpay1 = new VendorPayment("VPAY-2026-001", bill1, new BigDecimal("250000.00"), LocalDate.now().minusMonths(5),
                PaymentMethod.BANK_TRANSFER, "WIRE-MUN-98124");
        JournalEntry jeVpay1 = accountingService.postBalancedTransaction(
                JournalType.BANK, vpay1.getPaymentNumber(), "Partial payment for bill " + bill1.getBillNumber(),
                payables, cash, vpay1.getAmount());
        vpay1.setJournalEntry(jeVpay1);
        vendorPaymentRepository.save(vpay1);

        // 12. Non-Resident Memberships & Customer Revenue Transactions
        SalesOrder so1 = salesOrderRepository.save(new SalesOrder("SO-2026-001", pat1, LocalDate.now().minusMonths(2), prodNonResident, 1, new BigDecimal("1500.00"), OrderStatus.BILLED));
        CustomerInvoice inv1 = new CustomerInvoice("INV-2026-001", pat1, prodNonResident, LocalDate.now().minusMonths(2), LocalDate.now().minusMonths(1), new BigDecimal("1500.00"));
        inv1.setSalesOrder(so1);
        inv1.setPaidAmount(new BigDecimal("1500.00"));
        inv1.setStatus(InvoiceStatus.PAID);
        JournalEntry jeInv1 = accountingService.postBalancedTransaction(
                JournalType.SALES, inv1.getInvoiceNumber(), "Annual Non-Resident Card for Alice Walker",
                receivables, subRevenue, inv1.getAmount());
        inv1.setJournalEntry(jeInv1);
        invoiceRepository.save(inv1);

        // Payment for Invoice 1
        CustomerPayment cpay1 = new CustomerPayment("CPAY-2026-001", inv1, new BigDecimal("1500.00"), LocalDate.now().minusMonths(2),
                PaymentMethod.DIGITAL_PAYMENT, "GATEWAY-TXN-44910");
        JournalEntry jeCpay1 = accountingService.postBalancedTransaction(
                JournalType.BANK, cpay1.getPaymentNumber(), "Membership fee payment by Alice Walker",
                cash, receivables, cpay1.getAmount());
        cpay1.setJournalEntry(jeCpay1);
        customerPaymentRepository.save(cpay1);

        // Unpaid invoice for Brian Miller
        CustomerInvoice inv2 = new CustomerInvoice("INV-2026-002", pat2, prodNonResident, LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), new BigDecimal("1500.00"));
        inv2.setStatus(InvoiceStatus.POSTED);
        JournalEntry jeInv2 = accountingService.postBalancedTransaction(
                JournalType.SALES, inv2.getInvoiceNumber(), "Annual Non-Resident Card for Brian Miller",
                receivables, subRevenue, inv2.getAmount());
        inv2.setJournalEntry(jeInv2);
        invoiceRepository.save(inv2);

        // Record Initial Amortization Journal Entry for Demo Data (50,000 amortized)
        accountingService.postBalancedTransaction(
                JournalType.GENERAL, "AMORT-LIC-0001-INIT", "Accumulated straight-line amortization for LIC-0001",
                amortExpense, digitalAssets, new BigDecimal("50000.00"));

        log.info("Demo data successfully loaded into library_ebook_db!");
    }

    private Checkout createDemoCheckout(Contact patron, Ebook ebook, License license, String accessKeyCode,
                                        LocalDateTime checkoutDate, LocalDateTime expiryDate, CheckoutStatus status) {
        Checkout checkout = new Checkout(patron, ebook, license, accessKeyCode, checkoutDate, expiryDate);
        checkout.setStatus(status);
        checkout = checkoutRepository.save(checkout);

        AccessKeyStatus keyStatus = (status == CheckoutStatus.ACTIVE) ? AccessKeyStatus.ACTIVE : AccessKeyStatus.REVOKED;
        AccessKey accessKey = new AccessKey(accessKeyCode, checkout, checkoutDate, expiryDate);
        accessKey.setStatus(keyStatus);
        accessKeyRepository.save(accessKey);

        return checkout;
    }
}
