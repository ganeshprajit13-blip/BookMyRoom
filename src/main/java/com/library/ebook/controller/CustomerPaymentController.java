package com.library.ebook.controller;

import com.library.ebook.entity.CustomerPayment;
import com.library.ebook.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-payments")
@CrossOrigin(origins = "*")
public class CustomerPaymentController {

    private final PaymentService paymentService;

    public CustomerPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerPayment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerPayment> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerPayment> recordPayment(@Valid @RequestBody CustomerPayment payment) {
        CustomerPayment created = paymentService.recordPayment(payment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
