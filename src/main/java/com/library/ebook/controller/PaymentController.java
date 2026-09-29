package com.library.ebook.controller;

import com.library.ebook.dto.UnifiedPaymentDto;
import com.library.ebook.entity.CustomerInvoice;
import com.library.ebook.entity.CustomerPayment;
import com.library.ebook.entity.PaymentMethod;
import com.library.ebook.entity.VendorBill;
import com.library.ebook.entity.VendorPayment;
import com.library.ebook.service.PaymentService;
import com.library.ebook.service.VendorPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService customerPaymentService;
    private final VendorPaymentService vendorPaymentService;

    public PaymentController(PaymentService customerPaymentService, VendorPaymentService vendorPaymentService) {
        this.customerPaymentService = customerPaymentService;
        this.vendorPaymentService = vendorPaymentService;
    }

    @GetMapping
    public ResponseEntity<List<UnifiedPaymentDto>> getAllUnifiedPayments() {
        List<UnifiedPaymentDto> result = new ArrayList<>();

        // 1. Publisher / Vendor Payments
        for (VendorPayment vp : vendorPaymentService.getAllPayments()) {
            String pubName = (vp.getVendorBill() != null && vp.getVendorBill().getVendor() != null)
                    ? vp.getVendorBill().getVendor().getName() : "Publisher";
            String ref = (vp.getVendorBill() != null) ? vp.getVendorBill().getBillNumber() : "N/A";
            result.add(new UnifiedPaymentDto(
                    vp.getId(),
                    vp.getPaymentNumber(),
                    pubName,
                    "PUBLISHER",
                    ref,
                    vp.getAmount(),
                    vp.getPaymentMethod() != null ? vp.getPaymentMethod().name() : "BANK_TRANSFER",
                    vp.getPaymentDate(),
                    vp.getStatus() != null ? vp.getStatus().name() : "COMPLETED"
            ));
        }

        // 2. Patron Customer Payments
        for (CustomerPayment cp : customerPaymentService.getAllPayments()) {
            String patronName = (cp.getCustomerInvoice() != null && cp.getCustomerInvoice().getPatron() != null)
                    ? cp.getCustomerInvoice().getPatron().getName() : "Patron";
            String ref = (cp.getCustomerInvoice() != null) ? cp.getCustomerInvoice().getInvoiceNumber() : "N/A";
            result.add(new UnifiedPaymentDto(
                    cp.getId(),
                    cp.getPaymentNumber(),
                    patronName,
                    "PATRON",
                    ref,
                    cp.getAmount(),
                    cp.getPaymentMethod() != null ? cp.getPaymentMethod().name() : "DIGITAL_PAYMENT",
                    cp.getPaymentDate(),
                    cp.getStatus() != null ? cp.getStatus().name() : "COMPLETED"
            ));
        }

        // Sort descending by date
        result.sort((a, b) -> {
            if (a.getPaymentDate() == null || b.getPaymentDate() == null) return 0;
            return b.getPaymentDate().compareTo(a.getPaymentDate());
        });

        return ResponseEntity.ok(result);
    }

    @PostMapping("/patron")
    public ResponseEntity<CustomerPayment> recordPatronPayment(@RequestBody Map<String, Object> req) {
        CustomerPayment payment = new CustomerPayment();
        if (req.get("invoiceId") != null) {
            CustomerInvoice inv = new CustomerInvoice();
            inv.setId(Long.valueOf(req.get("invoiceId").toString()));
            payment.setCustomerInvoice(inv);
        }
        if (req.get("amount") != null) {
            payment.setAmount(new BigDecimal(req.get("amount").toString()));
        }
        if (req.get("paymentMethod") != null) {
            payment.setPaymentMethod(PaymentMethod.valueOf(req.get("paymentMethod").toString()));
        }
        if (req.get("reference") != null) {
            payment.setPaymentGatewayReference(req.get("reference").toString());
        }
        payment.setPaymentDate(LocalDate.now());

        CustomerPayment saved = customerPaymentService.recordPayment(payment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/vendor")
    public ResponseEntity<VendorPayment> recordVendorPayment(@RequestBody Map<String, Object> req) {
        VendorPayment payment = new VendorPayment();
        if (req.get("billId") != null) {
            VendorBill bill = new VendorBill();
            bill.setId(Long.valueOf(req.get("billId").toString()));
            payment.setVendorBill(bill);
        }
        if (req.get("amount") != null) {
            payment.setAmount(new BigDecimal(req.get("amount").toString()));
        }
        if (req.get("paymentMethod") != null) {
            payment.setPaymentMethod(PaymentMethod.valueOf(req.get("paymentMethod").toString()));
        }
        if (req.get("reference") != null) {
            payment.setBankReference(req.get("reference").toString());
        }
        payment.setPaymentDate(LocalDate.now());

        VendorPayment saved = vendorPaymentService.recordPayment(payment);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}
