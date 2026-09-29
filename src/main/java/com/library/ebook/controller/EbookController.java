package com.library.ebook.controller;

import com.library.ebook.dto.ApiResponse;
import com.library.ebook.entity.Ebook;
import com.library.ebook.service.EbookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ebooks")
@CrossOrigin(origins = "*")
public class EbookController {

    private final EbookService ebookService;

    public EbookController(EbookService ebookService) {
        this.ebookService = ebookService;
    }

    @GetMapping
    public ResponseEntity<List<Ebook>> getAllEbooks() {
        return ResponseEntity.ok(ebookService.getAllEbooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ebook> getEbookById(@PathVariable Long id) {
        return ResponseEntity.ok(ebookService.getEbookById(id));
    }

    @PostMapping
    public ResponseEntity<Ebook> createEbook(@Valid @RequestBody Ebook ebook) {
        Ebook created = ebookService.createEbook(ebook);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ebook> updateEbook(@PathVariable Long id, @Valid @RequestBody Ebook ebook) {
        return ResponseEntity.ok(ebookService.updateEbook(id, ebook));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteEbook(@PathVariable Long id) {
        ebookService.deleteEbook(id);
        return ResponseEntity.ok(ApiResponse.ok("E-Book deactivated successfully", null));
    }
}
