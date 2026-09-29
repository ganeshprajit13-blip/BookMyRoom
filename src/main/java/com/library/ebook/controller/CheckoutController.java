package com.library.ebook.controller;

import com.library.ebook.dto.ApiResponse;
import com.library.ebook.dto.CheckoutRequest;
import com.library.ebook.entity.Checkout;
import com.library.ebook.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ebooks/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping
    public ResponseEntity<List<Checkout>> getAllCheckouts(@RequestParam(required = false) Long patronId,
                                                          @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        if (patronId != null) {
            return ResponseEntity.ok(checkoutService.getCheckoutsByPatron(patronId));
        }
        if (activeOnly) {
            return ResponseEntity.ok(checkoutService.getActiveCheckouts());
        }
        return ResponseEntity.ok(checkoutService.getAllCheckouts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkout> getCheckoutById(@PathVariable Long id) {
        return ResponseEntity.ok(checkoutService.getCheckoutById(id));
    }

    @PostMapping
    public ResponseEntity<Checkout> performCheckout(@Valid @RequestBody CheckoutRequest request) {
        Checkout checkout = checkoutService.performCheckout(request);
        return new ResponseEntity<>(checkout, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<Checkout>> returnCheckout(@PathVariable Long id) {
        Checkout returned = checkoutService.returnCheckout(id);
        return ResponseEntity.ok(ApiResponse.ok("E-Book successfully returned and license capacity freed.", returned));
    }
}
