package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.CustomerInvoiceRepository;
import com.library.ebook.repository.ProductRepository;
import com.library.ebook.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {

    private final CustomerInvoiceRepository invoiceRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final AccountingService accountingService;

    public InvoiceService(CustomerInvoiceRepository invoiceRepository,
                          ContactRepository contactRepository,
                          ProductRepository productRepository,
                          SalesOrderRepository salesOrderRepository,
                          AccountingService accountingService) {
        this.invoiceRepository = invoiceRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.accountingService = accountingService;
    }

    public List<CustomerInvoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public CustomerInvoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Invoice not found with ID: " + id));
    }

    public CustomerInvoice createInvoice(CustomerInvoice invoice) {
        if (invoice.getPatron() == null || invoice.getPatron().getId() == null) {
            throw new BusinessValidationException("Patron must be specified.");
        }
        Contact patron = contactRepository.findById(invoice.getPatron().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + invoice.getPatron().getId()));

        if (invoice.getInvoiceNumber() == null || invoice.getInvoiceNumber().isBlank()) {
            invoice.setInvoiceNumber("INV-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }
        if (invoice.getDueDate() == null) {
            invoice.setDueDate(invoice.getInvoiceDate().plusDays(30));
        }

        if (invoice.getProduct() != null && invoice.getProduct().getId() != null) {
            Product product = productRepository.findById(invoice.getProduct().getId()).orElse(null);
            invoice.setProduct(product);
            if (invoice.getAmount() == null && product != null) {
                invoice.setAmount(product.getRate());
            }
        }

        if (invoice.getAmount() == null || invoice.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Invoice amount must be positive.");
        }

        invoice.setPatron(patron);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setStatus(InvoiceStatus.POSTED);

        if (invoice.getSalesOrder() != null && invoice.getSalesOrder().getId() != null) {
            SalesOrder so = salesOrderRepository.findById(invoice.getSalesOrder().getId()).orElse(null);
            if (so != null) {
                so.setStatus(OrderStatus.BILLED);
                salesOrderRepository.save(so);
                invoice.setSalesOrder(so);
            }
        }

        // Post Revenue Accounting Entry:
        // Debit: Patron Receivable (1030)
        // Credit: Non-Resident Subscription Revenue (4010)
        ChartOfAccount debitAccount = accountingService.getAccountByCode("1030");
        ChartOfAccount creditAccount = accountingService.getAccountByCode("4010");
        String desc = "Patron Invoice " + invoice.getInvoiceNumber() + " for " + patron.getName();
        JournalEntry je = accountingService.postBalancedTransaction(
                JournalType.SALES, invoice.getInvoiceNumber(), desc, debitAccount, creditAccount, invoice.getAmount());

        invoice.setJournalEntry(je);
        return invoiceRepository.save(invoice);
    }
}
