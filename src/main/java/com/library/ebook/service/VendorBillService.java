package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.PurchaseOrderRepository;
import com.library.ebook.repository.VendorBillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class VendorBillService {

    private final VendorBillRepository billRepository;
    private final ContactRepository contactRepository;
    private final PurchaseOrderRepository poRepository;
    private final AccountingService accountingService;

    public VendorBillService(VendorBillRepository billRepository,
                             ContactRepository contactRepository,
                             PurchaseOrderRepository poRepository,
                             AccountingService accountingService) {
        this.billRepository = billRepository;
        this.contactRepository = contactRepository;
        this.poRepository = poRepository;
        this.accountingService = accountingService;
    }

    public List<VendorBill> getAllBills() {
        return billRepository.findAll();
    }

    public VendorBill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor Bill not found with ID: " + id));
    }

    public VendorBill createBill(VendorBill bill) {
        if (bill.getVendor() == null || bill.getVendor().getId() == null) {
            throw new BusinessValidationException("Vendor must be specified.");
        }
        Contact vendor = contactRepository.findById(bill.getVendor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + bill.getVendor().getId()));

        if (bill.getBillNumber() == null || bill.getBillNumber().isBlank()) {
            bill.setBillNumber("BILL-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (bill.getBillDate() == null) {
            bill.setBillDate(LocalDate.now());
        }
        if (bill.getDueDate() == null) {
            bill.setDueDate(bill.getBillDate().plusDays(30));
        }
        if (bill.getAmount() == null || bill.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Bill amount must be greater than zero.");
        }
        bill.setVendor(vendor);
        bill.setPaidAmount(BigDecimal.ZERO);
        bill.setStatus(BillStatus.POSTED);

        if (bill.getPurchaseOrder() != null && bill.getPurchaseOrder().getId() != null) {
            PurchaseOrder po = poRepository.findById(bill.getPurchaseOrder().getId()).orElse(null);
            if (po != null) {
                po.setStatus(OrderStatus.BILLED);
                poRepository.save(po);
                bill.setPurchaseOrder(po);
            }
        }

        // Post Accounting Entry:
        // Debit: Digital Asset Inventory (1020)
        // Credit: Publisher Royalty Payables (2010)
        ChartOfAccount debitAccount = accountingService.getAccountByCode("1020");
        ChartOfAccount creditAccount = accountingService.getAccountByCode("2010");
        String desc = "Vendor Bill " + bill.getBillNumber() + " from " + vendor.getName();
        JournalEntry je = accountingService.postBalancedTransaction(
                JournalType.PURCHASE, bill.getBillNumber(), desc, debitAccount, creditAccount, bill.getAmount());

        bill.setJournalEntry(je);
        return billRepository.save(bill);
    }
}
