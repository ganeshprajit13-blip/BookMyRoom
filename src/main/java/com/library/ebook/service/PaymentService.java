package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.InvalidPaymentException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.CustomerInvoiceRepository;
import com.library.ebook.repository.CustomerPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final CustomerPaymentRepository paymentRepository;
    private final CustomerInvoiceRepository invoiceRepository;
    private final AccountingService accountingService;

    public PaymentService(CustomerPaymentRepository paymentRepository,
                          CustomerInvoiceRepository invoiceRepository,
                          AccountingService accountingService) {
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.accountingService = accountingService;
    }

    public List<CustomerPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public CustomerPayment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Payment not found with ID: " + id));
    }

    public CustomerPayment recordPayment(CustomerPayment payment) {
        if (payment.getCustomerInvoice() == null || payment.getCustomerInvoice().getId() == null) {
            throw new BusinessValidationException("Invoice must be specified.");
        }
        CustomerInvoice invoice = invoiceRepository.findById(payment.getCustomerInvoice().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + payment.getCustomerInvoice().getId()));

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Payment amount must be greater than zero.");
        }

        // Rule 7: Customer payment cannot exceed outstanding invoice amount
        BigDecimal outstanding = invoice.getOutstandingAmount();
        if (payment.getAmount().compareTo(outstanding) > 0) {
            throw new InvalidPaymentException("Payment amount (" + payment.getAmount() +
                    ") exceeds outstanding invoice balance (" + outstanding + ").");
        }

        if (payment.getPaymentNumber() == null || payment.getPaymentNumber().isBlank()) {
            payment.setPaymentNumber("CPAY-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCustomerInvoice(invoice);

        // Update invoice paid amount and status
        BigDecimal newPaidAmount = (invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO).add(payment.getAmount());
        invoice.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceRepository.save(invoice);

        // Post Journal Entry:
        // Debit: Cash (1010)
        // Credit: Patron Receivable (1030)
        ChartOfAccount debitAccount = accountingService.getAccountByCode("1010");
        ChartOfAccount creditAccount = accountingService.getAccountByCode("1030");
        String desc = "Patron payment " + payment.getPaymentNumber() + " for invoice " + invoice.getInvoiceNumber();
        JournalEntry je = accountingService.postBalancedTransaction(
                JournalType.BANK, payment.getPaymentNumber(), desc, debitAccount, creditAccount, payment.getAmount());

        payment.setJournalEntry(je);
        return paymentRepository.save(payment);
    }
}
