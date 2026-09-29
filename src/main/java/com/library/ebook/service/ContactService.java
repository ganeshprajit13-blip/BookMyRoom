package com.library.ebook.service;

import com.library.ebook.entity.Contact;
import com.library.ebook.entity.ContactType;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getContactsByType(ContactType type) {
        return contactRepository.findByContactType(type);
    }

    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));
    }

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact updateContact(Long id, Contact updatedContact) {
        Contact existing = getContactById(id);
        existing.setName(updatedContact.getName());
        existing.setEmail(updatedContact.getEmail());
        existing.setPhone(updatedContact.getPhone());
        existing.setAddress(updatedContact.getAddress());
        if (updatedContact.getContactType() != null) {
            existing.setContactType(updatedContact.getContactType());
        }
        if (updatedContact.getStatus() != null) {
            existing.setStatus(updatedContact.getStatus());
        }
        return contactRepository.save(existing);
    }

    public void deleteContact(Long id) {
        Contact contact = getContactById(id);
        contact.setStatus("INACTIVE");
        contactRepository.save(contact);
    }
}
