package com.library.ebook.controller;

import com.library.ebook.entity.CustomerInvoice;
import com.library.ebook.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerInvoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerInvoice> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @PostMapping
    public ResponseEntity<CustomerInvoice> createInvoice(@Valid @RequestBody CustomerInvoice invoice) {
        CustomerInvoice created = invoiceService.createInvoice(invoice);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
