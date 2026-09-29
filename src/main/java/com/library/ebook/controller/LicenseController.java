package com.library.ebook.controller;

import com.library.ebook.dto.LicenseUtilizationDto;
import com.library.ebook.entity.License;
import com.library.ebook.service.LicenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ebooks/licenses")
@CrossOrigin(origins = "*")
public class LicenseController {

    private final LicenseService licenseService;

    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @GetMapping
    public ResponseEntity<List<License>> getAllLicenses(@RequestParam(required = false) Long ebookId) {
        if (ebookId != null) {
            return ResponseEntity.ok(licenseService.getLicensesByEbookId(ebookId));
        }
        return ResponseEntity.ok(licenseService.getAllLicenses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<License> getLicenseById(@PathVariable Long id) {
        return ResponseEntity.ok(licenseService.getLicenseById(id));
    }

    @PostMapping
    public ResponseEntity<License> createLicense(@Valid @RequestBody License license) {
        License created = licenseService.createLicense(license);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<License> updateLicense(@PathVariable Long id, @RequestBody License license) {
        return ResponseEntity.ok(licenseService.updateLicense(id, license));
    }

    @GetMapping("/utilization")
    public ResponseEntity<List<LicenseUtilizationDto>> getLicenseUtilization() {
        return ResponseEntity.ok(licenseService.getLicenseUtilizationList());
    }
}
