package com.library.ebook.service;

import com.library.ebook.dto.HoldRequest;
import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.EbookHoldRepository;
import com.library.ebook.repository.EbookRepository;
import com.library.ebook.repository.LicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class HoldService {

    private final EbookHoldRepository holdRepository;
    private final ContactRepository contactRepository;
    private final EbookRepository ebookRepository;
    private final LicenseRepository licenseRepository;

    public HoldService(EbookHoldRepository holdRepository,
                       ContactRepository contactRepository,
                       EbookRepository ebookRepository,
                       LicenseRepository licenseRepository) {
        this.holdRepository = holdRepository;
        this.contactRepository = contactRepository;
        this.ebookRepository = ebookRepository;
        this.licenseRepository = licenseRepository;
    }

    public List<EbookHold> getAllHolds() {
        return holdRepository.findAll();
    }

    public List<EbookHold> getWaitingHolds() {
        return holdRepository.findByStatus(HoldStatus.WAITING);
    }

    public EbookHold getHoldById(Long id) {
        return holdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hold record not found with ID: " + id));
    }

    public EbookHold placeHold(HoldRequest request) {
        if (request.getPatronId() == null) {
            throw new BusinessValidationException("patronId is required to place a hold.");
        }

        Contact patron = contactRepository.findById(request.getPatronId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + request.getPatronId()));

        if (patron.getContactType() != ContactType.CUSTOMER) {
            throw new BusinessValidationException("Contact must be a registered patron (CUSTOMER).");
        }

        License license = null;
        Ebook ebook = null;

        if (request.getLicenseId() != null) {
            license = licenseRepository.findById(request.getLicenseId()).orElse(null);
            if (license != null) {
                ebook = license.getEbook();
            }
        }

        if (ebook == null && request.getEbookId() != null) {
            ebook = ebookRepository.findById(request.getEbookId()).orElse(null);
            if (ebook == null) {
                License lic = licenseRepository.findById(request.getEbookId()).orElse(null);
                if (lic != null) {
                    license = lic;
                    ebook = lic.getEbook();
                }
            }
        }

        if (ebook == null) {
            throw new ResourceNotFoundException("E-Book or License not found for the hold request.");
        }

        if (license == null) {
            // Find active license for the ebook if available
            List<License> licenses = licenseRepository.findByEbookId(ebook.getId());
            if (!licenses.isEmpty()) {
                license = licenses.get(0);
            }
        }

        List<EbookHold> existingWaiting = holdRepository.findByEbookIdAndStatusOrderByQueuePositionAsc(ebook.getId(), HoldStatus.WAITING);
        int nextPosition = existingWaiting.size() + 1;

        EbookHold hold = new EbookHold(patron, ebook, license, nextPosition);
        return holdRepository.save(hold);
    }

    public EbookHold cancelHold(Long id) {
        EbookHold hold = getHoldById(id);
        hold.setStatus(HoldStatus.CANCELLED);
        return holdRepository.save(hold);
    }
}
