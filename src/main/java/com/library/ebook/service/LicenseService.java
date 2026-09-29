package com.library.ebook.service;

import com.library.ebook.dto.LicenseUtilizationDto;
import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.EbookRepository;
import com.library.ebook.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LicenseService {

    private final LicenseRepository licenseRepository;
    private final EbookRepository ebookRepository;
    private final ContactRepository contactRepository;

    public LicenseService(LicenseRepository licenseRepository, EbookRepository ebookRepository, ContactRepository contactRepository) {
        this.licenseRepository = licenseRepository;
        this.ebookRepository = ebookRepository;
        this.contactRepository = contactRepository;
    }

    public List<License> getAllLicenses() {
        return licenseRepository.findAll();
    }

    public License getLicenseById(Long id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Book License not found with ID: " + id));
    }

    public List<License> getLicensesByEbookId(Long ebookId) {
        return licenseRepository.findByEbookId(ebookId);
    }

    public License createLicense(License license) {
        if (license.getEbook() == null || license.getEbook().getId() == null) {
            throw new BusinessValidationException("Associated E-Book must be specified.");
        }
        Ebook ebook = ebookRepository.findById(license.getEbook().getId())
                .orElseThrow(() -> new ResourceNotFoundException("E-Book not found with ID: " + license.getEbook().getId()));

        if (license.getPublisher() == null || license.getPublisher().getId() == null) {
            throw new BusinessValidationException("Publisher must be specified.");
        }
        Contact publisher = contactRepository.findById(license.getPublisher().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with ID: " + license.getPublisher().getId()));

        if (publisher.getContactType() != ContactType.VENDOR) {
            throw new BusinessValidationException("Selected publisher must be of type VENDOR.");
        }

        if (license.getLicenseType() == LicenseType.TERM && license.getLicenseExpiryDate() == null) {
            throw new BusinessValidationException("Term licenses require an expiry date.");
        }

        if (license.getLicenseExpiryDate() != null && license.getLicenseStartDate() != null) {
            if (license.getLicenseExpiryDate().isBefore(license.getLicenseStartDate())) {
                throw new BusinessValidationException("License expiry date cannot be before start date.");
            }
        }

        license.setEbook(ebook);
        license.setPublisher(publisher);
        if (license.getActiveCheckoutCount() == null) {
            license.setActiveCheckoutCount(0);
        }
        if (license.getRemainingAssetValue() == null) {
            license.setRemainingAssetValue(license.getPurchaseCost());
        }
        return licenseRepository.save(license);
    }

    public License updateLicense(Long id, License updated) {
        License existing = getLicenseById(id);
        if (updated.getConcurrentUserLimit() != null) {
            existing.setConcurrentUserLimit(updated.getConcurrentUserLimit());
        }
        if (updated.getLicenseExpiryDate() != null) {
            existing.setLicenseExpiryDate(updated.getLicenseExpiryDate());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        if (updated.getAmortizationPeriod() != null) {
            existing.setAmortizationPeriod(updated.getAmortizationPeriod());
        }
        return licenseRepository.save(existing);
    }

    public List<LicenseUtilizationDto> getLicenseUtilizationList() {
        return licenseRepository.findAll().stream().map(license -> {
            LicenseUtilizationDto dto = new LicenseUtilizationDto();
            dto.setLicenseId(license.getId());
            dto.setLicenseNumber(license.getLicenseNumber());
            dto.setEbookTitle(license.getEbook() != null ? license.getEbook().getTitle() : "N/A");
            dto.setIsbn(license.getEbook() != null ? license.getEbook().getIsbn() : "N/A");
            dto.setPublisherName(license.getPublisher() != null ? license.getPublisher().getName() : "N/A");
            dto.setLicenseType(license.getLicenseType().name());
            dto.setConcurrentLimit(license.getConcurrentUserLimit());
            dto.setActiveCheckouts(license.getActiveCheckoutCount());
            dto.setAvailableSlots(license.getAvailableCapacity());
            dto.setUtilizationPercentage(Math.round(license.getUtilizationPercentage() * 10.0) / 10.0);
            dto.setExpiryDate(license.getLicenseExpiryDate());
            dto.setStatus(license.getStatus().name());
            return dto;
        }).collect(Collectors.toList());
    }

    public LicenseUtilizationDto getLicenseUtilization(Long licenseId) {
        License license = getLicenseById(licenseId);
        LicenseUtilizationDto dto = new LicenseUtilizationDto();
        dto.setLicenseId(license.getId());
        dto.setLicenseNumber(license.getLicenseNumber());
        dto.setEbookTitle(license.getEbook() != null ? license.getEbook().getTitle() : "N/A");
        dto.setIsbn(license.getEbook() != null ? license.getEbook().getIsbn() : "N/A");
        dto.setPublisherName(license.getPublisher() != null ? license.getPublisher().getName() : "N/A");
        dto.setLicenseType(license.getLicenseType().name());
        dto.setConcurrentLimit(license.getConcurrentUserLimit());
        dto.setActiveCheckouts(license.getActiveCheckoutCount());
        dto.setAvailableSlots(license.getAvailableCapacity());
        dto.setUtilizationPercentage(Math.round(license.getUtilizationPercentage() * 100.0) / 100.0);
        dto.setExpiryDate(license.getLicenseExpiryDate());
        dto.setStatus(license.getStatus().name());
        return dto;
    }

    public List<License> getExpiringLicenses(int withinDays) {
        LocalDate threshold = LocalDate.now().plusDays(withinDays);
        return licenseRepository.findExpiringLicenses(threshold);
    }
}
