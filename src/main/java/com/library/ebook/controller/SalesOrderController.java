package com.library.ebook.controller;

import com.library.ebook.entity.SalesOrder;
import com.library.ebook.service.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@CrossOrigin(origins = "*")
public class SalesOrderController {

    private final SalesOrderService soService;

    public SalesOrderController(SalesOrderService soService) {
        this.soService = soService;
    }

    @GetMapping
    public ResponseEntity<List<SalesOrder>> getAllOrders() {
        return ResponseEntity.ok(soService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(soService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<SalesOrder> createOrder(@Valid @RequestBody SalesOrder order) {
        SalesOrder created = soService.createOrder(order);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
