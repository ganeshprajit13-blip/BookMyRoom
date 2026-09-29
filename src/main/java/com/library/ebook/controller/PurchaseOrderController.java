package com.library.ebook.controller;

import com.library.ebook.entity.PurchaseOrder;
import com.library.ebook.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@CrossOrigin(origins = "*")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    public PurchaseOrderController(PurchaseOrderService poService) {
        this.poService = poService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(poService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(poService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@Valid @RequestBody PurchaseOrder order) {
        PurchaseOrder created = poService.createOrder(order);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
