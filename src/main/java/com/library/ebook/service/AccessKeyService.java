package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.AccessKeyRepository;
import com.library.ebook.repository.CheckoutRepository;
import com.library.ebook.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccessKeyService {

    private final AccessKeyRepository accessKeyRepository;
    private final CheckoutRepository checkoutRepository;
    private final LicenseRepository licenseRepository;

    public AccessKeyService(AccessKeyRepository accessKeyRepository,
                            CheckoutRepository checkoutRepository,
                            LicenseRepository licenseRepository) {
        this.accessKeyRepository = accessKeyRepository;
        this.checkoutRepository = checkoutRepository;
        this.licenseRepository = licenseRepository;
    }

    public List<AccessKey> getAllAccessKeys() {
        return accessKeyRepository.findAll();
    }

    public AccessKey getAccessKeyByCode(String code) {
        return accessKeyRepository.findByAccessKey(code)
                .orElseThrow(() -> new ResourceNotFoundException("Digital Access Key not found: " + code));
    }

    public AccessKey generateAccessKey(Checkout checkout, LocalDateTime expiryDate) {
        String randomHex = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String keyCode = "EBOOK-ACCESS-" + randomHex;

        AccessKey key = new AccessKey(keyCode, checkout, LocalDateTime.now(), expiryDate);
        return accessKeyRepository.save(key);
    }

    public AccessKey getKeyByCheckout(Long checkoutId) {
        return accessKeyRepository.findByCheckoutId(checkoutId).orElse(null);
    }

    public int revokeExpiredKeys() {
        return revokeExpiredAccessKeys();
    }

    public int revokeExpiredAccessKeys() {
        LocalDateTime now = LocalDateTime.now();
        List<AccessKey> expiredKeys = accessKeyRepository.findByStatusAndExpiryDateBefore(AccessKeyStatus.ACTIVE, now);

        int count = 0;
        for (AccessKey key : expiredKeys) {
            key.setStatus(AccessKeyStatus.REVOKED);
            accessKeyRepository.save(key);

            Checkout checkout = key.getCheckout();
            if (checkout != null && checkout.getStatus() == CheckoutStatus.ACTIVE) {
                checkout.setStatus(CheckoutStatus.EXPIRED);
                checkoutRepository.save(checkout);

                // Decrement active checkout count on license
                License license = checkout.getLicense();
                if (license != null && license.getActiveCheckoutCount() > 0) {
                    license.setActiveCheckoutCount(license.getActiveCheckoutCount() - 1);
                    licenseRepository.save(license);
                }
            }
            count++;
        }
        return count;
    }
}
