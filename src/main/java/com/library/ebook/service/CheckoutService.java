package com.library.ebook.service;

import com.library.ebook.dto.CheckoutRequest;
import com.library.ebook.entity.*;
import com.library.ebook.exception.*;
import com.library.ebook.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final LicenseRepository licenseRepository;
    private final ContactRepository contactRepository;
    private final AccessKeyService accessKeyService;
    private final EbookHoldRepository holdRepository;

    public CheckoutService(CheckoutRepository checkoutRepository,
                           LicenseRepository licenseRepository,
                           ContactRepository contactRepository,
                           AccessKeyService accessKeyService,
                           EbookHoldRepository holdRepository) {
        this.checkoutRepository = checkoutRepository;
        this.licenseRepository = licenseRepository;
        this.contactRepository = contactRepository;
        this.accessKeyService = accessKeyService;
        this.holdRepository = holdRepository;
    }

    public List<Checkout> getAllCheckouts() {
        return checkoutRepository.findAll();
    }

    public List<Checkout> getActiveCheckouts() {
        return checkoutRepository.findByStatus(CheckoutStatus.ACTIVE);
    }

    public List<Checkout> getCheckoutsByPatron(Long patronId) {
        return checkoutRepository.findByPatronId(patronId);
    }

    public Checkout getCheckoutById(Long id) {
        return checkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout record not found with ID: " + id));
    }

    public Checkout performCheckout(CheckoutRequest request) {
        // 1. Validate patron
        Contact patron = contactRepository.findById(request.getPatronId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + request.getPatronId()));

        if (patron.getContactType() != ContactType.CUSTOMER) {
            throw new BusinessValidationException("Contact ID " + patron.getId() + " is not a CUSTOMER / Patron.");
        }
        if (!"ACTIVE".equalsIgnoreCase(patron.getStatus())) {
            throw new BusinessValidationException("Patron account is currently inactive.");
        }

        // 2. Validate license
        License license = licenseRepository.findById(request.getLicenseId())
                .orElseThrow(() -> new ResourceNotFoundException("E-Book License not found with ID: " + request.getLicenseId()));

        // 3. Check license status
        if (license.getStatus() != LicenseStatus.ACTIVE) {
            throw new LicenseInactiveException("License " + license.getLicenseNumber() + " is not active (Status: " + license.getStatus() + ").");
        }

        // 4. Check expiry
        if (license.isExpired()) {
            license.setStatus(LicenseStatus.EXPIRED);
            licenseRepository.save(license);
            throw new LicenseExpiredException("License " + license.getLicenseNumber() + " has expired on " + license.getLicenseExpiryDate());
        }

        // 5. Check concurrent checkout count (Rule 1 & Module 5)
        if (license.getActiveCheckoutCount() >= license.getConcurrentUserLimit()) {
            throw new LicenseLimitExceededException("Concurrent checkout limit reached for this license.");
        }

        // Check if patron already has an active checkout for this exact license
        List<Checkout> activeForPatron = checkoutRepository.findByPatronIdAndLicenseIdAndStatus(
                patron.getId(), license.getId(), CheckoutStatus.ACTIVE);
        if (!activeForPatron.isEmpty()) {
            throw new BusinessValidationException("Patron already has an active checkout for this e-book license.");
        }

        // 6 & 7. Create checkout and set dates
        LocalDateTime checkoutDate = LocalDateTime.now();
        LocalDateTime expiryDate = checkoutDate.plusDays(14); // 14-day checkout period

        // Temporary placeholder access key string
        String tempKey = "PENDING";
        Checkout checkout = new Checkout(patron, license.getEbook(), license, tempKey, checkoutDate, expiryDate);
        checkout = checkoutRepository.save(checkout);

        // 8. Generate Digital Access Key
        AccessKey accessKey = accessKeyService.generateAccessKey(checkout, expiryDate);
        checkout.setAccessKey(accessKey.getAccessKey());
        checkout = checkoutRepository.save(checkout);

        // 9. Increment active checkout count
        license.setActiveCheckoutCount(license.getActiveCheckoutCount() + 1);
        licenseRepository.save(license);

        return checkout;
    }

    public Checkout returnBook(Long checkoutId) {
        return returnCheckout(checkoutId);
    }

    public Checkout returnCheckout(Long checkoutId) {
        Checkout checkout = getCheckoutById(checkoutId);

        if (checkout.getStatus() != CheckoutStatus.ACTIVE) {
            throw new BusinessValidationException("Checkout is not active (current status: " + checkout.getStatus() + ")");
        }

        // Update checkout
        checkout.setStatus(CheckoutStatus.RETURNED);
        checkout.setReturnedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);

        // Revoke the associated digital access key
        try {
            AccessKey key = accessKeyService.getAccessKeyByCode(checkout.getAccessKey());
            key.setStatus(AccessKeyStatus.REVOKED);
        } catch (Exception ignored) {}

        // Decrement license active checkout count
        License license = checkout.getLicense();
        if (license != null && license.getActiveCheckoutCount() > 0) {
            license.setActiveCheckoutCount(license.getActiveCheckoutCount() - 1);
            licenseRepository.save(license);

            // Check if there are waiting holds for this license or ebook
            processNextHold(license);
        }

        return checkout;
    }

    private void processNextHold(License license) {
        List<EbookHold> waitingHolds = holdRepository.findByLicenseIdAndStatusOrderByQueuePositionAsc(license.getId(), HoldStatus.WAITING);
        if (waitingHolds.isEmpty() && license.getEbook() != null) {
            waitingHolds = holdRepository.findByEbookIdAndStatusOrderByQueuePositionAsc(license.getEbook().getId(), HoldStatus.WAITING);
        }
        if (!waitingHolds.isEmpty()) {
            EbookHold nextHold = waitingHolds.get(0);
            nextHold.setStatus(HoldStatus.FULFILLED);
            holdRepository.save(nextHold);
        }
    }
}
