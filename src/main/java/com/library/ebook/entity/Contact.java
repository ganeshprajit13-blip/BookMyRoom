package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Contact name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull(message = "Contact type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "contact_type", nullable = false, length = 20)
    private ContactType contactType;

    @Email(message = "Email should be valid")
    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "membership_type", length = 30)
    private String membershipType = "RESIDENT";

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    public Contact() {}

    public Contact(String name, ContactType contactType, String email, String phone, String address) {
        this.name = name;
        this.contactType = contactType;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membershipType = "RESIDENT";
        this.status = "ACTIVE";
    }

    public Contact(String name, ContactType contactType, String email, String phone, String address, String membershipType) {
        this.name = name;
        this.contactType = contactType;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.membershipType = membershipType != null ? membershipType : "RESIDENT";
        this.status = "ACTIVE";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) {
            this.createdDate = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ContactType getContactType() { return contactType; }
    public void setContactType(ContactType contactType) { this.contactType = contactType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
}
