package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "journals")
public class Journal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Journal code is required")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank(message = "Journal name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull(message = "Journal type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JournalType type;

    @Column(nullable = false)
    private Boolean active = true;

    public Journal() {}

    public Journal(String code, String name, JournalType type) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JournalType getType() { return type; }
    public void setType(JournalType type) { this.type = type; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
