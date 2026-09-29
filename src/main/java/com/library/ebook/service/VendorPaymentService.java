package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.InvalidPaymentException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.VendorBillRepository;
import com.library.ebook.repository.VendorPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VendorPaymentService {

    private final VendorPaymentRepository paymentRepository;
    private final VendorBillRepository billRepository;
    private final AccountingService accountingService;

    public VendorPaymentService(VendorPaymentRepository paymentRepository,
                                VendorBillRepository billRepository,
                                AccountingService accountingService) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.accountingService = accountingService;
    }

    public List<VendorPayment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public VendorPayment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Payment not found with ID: " + id));
    }

    public VendorPayment recordPayment(VendorPayment payment) {
        if (payment.getVendorBill() == null || payment.getVendorBill().getId() == null) {
            throw new BusinessValidationException("Vendor bill must be specified.");
        }
        VendorBill bill = billRepository.findById(payment.getVendorBill().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Bill not found with ID: " + payment.getVendorBill().getId()));

        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Payment amount must be greater than zero.");
        }

        // Rule 6: Vendor payment cannot exceed outstanding vendor bill amount
        BigDecimal outstanding = bill.getOutstandingAmount();
        if (payment.getAmount().compareTo(outstanding) > 0) {
            throw new InvalidPaymentException("Payment amount (" + payment.getAmount() +
                    ") exceeds outstanding bill balance (" + outstanding + ").");
        }

        if (payment.getPaymentNumber() == null || payment.getPaymentNumber().isBlank()) {
            payment.setPaymentNumber("VPAY-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDate.now());
        }
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setVendorBill(bill);

        // Update bill paid amount and status
        BigDecimal newPaidAmount = (bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO).add(payment.getAmount());
        bill.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(bill.getAmount()) >= 0) {
            bill.setStatus(BillStatus.PAID);
        } else {
            bill.setStatus(BillStatus.PARTIALLY_PAID);
        }
        billRepository.save(bill);

        // Post Journal Entry:
        // Debit: Publisher Royalty Payables (2010)
        // Credit: Cash (1010)
        ChartOfAccount debitAccount = accountingService.getAccountByCode("2010");
        ChartOfAccount creditAccount = accountingService.getAccountByCode("1010");
        String desc = "Vendor payment " + payment.getPaymentNumber() + " for bill " + bill.getBillNumber();
        JournalEntry je = accountingService.postBalancedTransaction(
                JournalType.BANK, payment.getPaymentNumber(), desc, debitAccount, creditAccount, payment.getAmount());

        payment.setJournalEntry(je);
        return paymentRepository.save(payment);
    }
}
