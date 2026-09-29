package com.library.ebook.controller;

import com.library.ebook.entity.VendorBill;
import com.library.ebook.service.VendorBillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-bills")
@CrossOrigin(origins = "*")
public class VendorBillController {

    private final VendorBillService billService;

    public VendorBillController(VendorBillService billService) {
        this.billService = billService;
    }

    @GetMapping
    public ResponseEntity<List<VendorBill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorBill> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @PostMapping
    public ResponseEntity<VendorBill> createBill(@Valid @RequestBody VendorBill bill) {
        VendorBill created = billService.createBill(bill);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
