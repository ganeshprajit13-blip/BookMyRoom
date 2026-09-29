package com.library.ebook.controller;

import com.library.ebook.entity.VendorPayment;
import com.library.ebook.service.VendorPaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-payments")
@CrossOrigin(origins = "*")
public class VendorPaymentController {

    private final VendorPaymentService paymentService;

    public VendorPaymentController(VendorPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<VendorPayment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorPayment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping
    public ResponseEntity<VendorPayment> recordPayment(@Valid @RequestBody VendorPayment payment) {
        VendorPayment created = paymentService.recordPayment(payment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
