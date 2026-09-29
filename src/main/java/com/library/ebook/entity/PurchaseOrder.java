package com.library.ebook.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "po_number", nullable = false, unique = true, length = 50)
    private String poNumber;

    @NotNull(message = "Vendor is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Contact vendor;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity = 1;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", message = "Rate must be non-negative")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal rate;

    @NotNull(message = "Total is required")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal total;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.CONFIRMED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public PurchaseOrder() {}

    public PurchaseOrder(String poNumber, Contact vendor, LocalDate orderDate, Product product, Integer quantity, BigDecimal rate, OrderStatus status) {
        this.poNumber = poNumber;
        this.vendor = vendor;
        this.orderDate = orderDate;
        this.product = product;
        this.quantity = quantity != null ? quantity : 1;
        this.rate = rate;
        this.total = rate.multiply(BigDecimal.valueOf(this.quantity));
        this.status = status != null ? status : OrderStatus.CONFIRMED;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.orderDate == null) {
            this.orderDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = OrderStatus.CONFIRMED;
        }
        if (this.quantity == null) {
            this.quantity = 1;
        }
        if (this.rate != null && this.total == null) {
            this.total = this.rate.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public Contact getVendor() { return vendor; }
    public void setVendor(Contact vendor) { this.vendor = vendor; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
