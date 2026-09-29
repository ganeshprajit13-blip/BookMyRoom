package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "chart_of_accounts")
public class ChartOfAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Account code is required")
    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank(message = "Account name is required")
    @Column(nullable = false, length = 150)
    private String name;

    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;

    public ChartOfAccount() {}

    public ChartOfAccount(String code, String name, AccountType type, String description, BigDecimal balance) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.description = description;
        this.balance = balance != null ? balance : BigDecimal.ZERO;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.balance == null) {
            this.balance = BigDecimal.ZERO;
        }
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

    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
