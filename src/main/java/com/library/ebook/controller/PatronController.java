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
@RequestMapping("/api/patrons")
@CrossOrigin(origins = "*")
public class PatronController {

    private final ContactService contactService;

    public PatronController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllPatrons() {
        return ResponseEntity.ok(contactService.getContactsByType(ContactType.CUSTOMER));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getPatronById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PostMapping
    public ResponseEntity<Contact> createPatron(@Valid @RequestBody Contact patron) {
        patron.setContactType(ContactType.CUSTOMER);
        if (patron.getMembershipType() == null || patron.getMembershipType().isBlank()) {
            patron.setMembershipType("RESIDENT");
        }
        Contact saved = contactService.createContact(patron);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updatePatron(@PathVariable Long id, @Valid @RequestBody Contact patron) {
        patron.setContactType(ContactType.CUSTOMER);
        Contact updated = contactService.updateContact(id, patron);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatron(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}
