package com.library.ebook.controller;

import com.library.ebook.dto.ApiResponse;
import com.library.ebook.dto.AmortizationResponse;
import com.library.ebook.service.AmortizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amortization")
@CrossOrigin(origins = "*")
public class AmortizationController {

    private final AmortizationService amortizationService;

    public AmortizationController(AmortizationService amortizationService) {
        this.amortizationService = amortizationService;
    }

    @PostMapping("/{licenseId}")
    public ResponseEntity<ApiResponse<AmortizationResponse>> amortizeLicense(@PathVariable Long licenseId) {
        AmortizationResponse response = amortizationService.amortizeLicense(licenseId);
        return ResponseEntity.ok(ApiResponse.ok("License amortization entry created successfully", response));
    }

    @PostMapping("/run-all")
    public ResponseEntity<ApiResponse<List<AmortizationResponse>>> amortizeAll() {
        List<AmortizationResponse> responses = amortizationService.amortizeAllActiveLicenses();
        return ResponseEntity.ok(ApiResponse.ok("Periodic amortization processed for " + responses.size() + " active licenses", responses));
    }
}
