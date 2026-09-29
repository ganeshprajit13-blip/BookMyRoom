package com.library.ebook.controller;

import com.library.ebook.entity.Contact;
import com.library.ebook.entity.ContactType;
import com.library.ebook.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@CrossOrigin(origins = "*")
public class PublisherController {

    private final ContactService contactService;

    public PublisherController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllPublishers() {
        return ResponseEntity.ok(contactService.getContactsByType(ContactType.VENDOR));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getPublisherById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PostMapping
    public ResponseEntity<Contact> createPublisher(@Valid @RequestBody Contact publisher) {
        publisher.setContactType(ContactType.VENDOR);
        Contact saved = contactService.createContact(publisher);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updatePublisher(@PathVariable Long id, @Valid @RequestBody Contact publisher) {
        publisher.setContactType(ContactType.VENDOR);
        Contact updated = contactService.updateContact(id, publisher);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
