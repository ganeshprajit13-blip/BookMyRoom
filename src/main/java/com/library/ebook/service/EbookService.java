package com.library.ebook.service;

import com.library.ebook.entity.Ebook;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.EbookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EbookService {

    private final EbookRepository ebookRepository;

    public EbookService(EbookRepository ebookRepository) {
        this.ebookRepository = ebookRepository;
    }

    public List<Ebook> getAllEbooks() {
        return ebookRepository.findAll();
    }

    public Ebook getEbookById(Long id) {
        return ebookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("E-Book not found with ID: " + id));
    }

    public Ebook createEbook(Ebook ebook) {
        ebookRepository.findByIsbn(ebook.getIsbn()).ifPresent(existing -> {
            throw new BusinessValidationException("E-Book with ISBN " + ebook.getIsbn() + " already exists.");
        });
        return ebookRepository.save(ebook);
    }

    public Ebook updateEbook(Long id, Ebook updated) {
        Ebook existing = getEbookById(id);
        existing.setTitle(updated.getTitle());
        existing.setAuthor(updated.getAuthor());
        existing.setPublisher(updated.getPublisher());
        existing.setCategory(updated.getCategory());
        existing.setPublicationYear(updated.getPublicationYear());
        existing.setDescription(updated.getDescription());
        existing.setLanguage(updated.getLanguage());
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        return ebookRepository.save(existing);
    }

    public void deleteEbook(Long id) {
        Ebook ebook = getEbookById(id);
        ebook.setStatus("INACTIVE");
        ebookRepository.save(ebook);
    }
}
