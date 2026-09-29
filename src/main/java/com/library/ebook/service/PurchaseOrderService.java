package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.ProductRepository;
import com.library.ebook.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderService(PurchaseOrderRepository poRepository,
                                ContactRepository contactRepository,
                                ProductRepository productRepository) {
        this.poRepository = poRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
    }

    public List<PurchaseOrder> getAllOrders() {
        return poRepository.findAll();
    }

    public PurchaseOrder getOrderById(Long id) {
        return poRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase Order not found with ID: " + id));
    }

    public PurchaseOrder createOrder(PurchaseOrder order) {
        if (order.getVendor() == null || order.getVendor().getId() == null) {
            throw new BusinessValidationException("Vendor must be specified.");
        }
        Contact vendor = contactRepository.findById(order.getVendor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with ID: " + order.getVendor().getId()));

        if (vendor.getContactType() != ContactType.VENDOR) {
            throw new BusinessValidationException("Contact must be of type VENDOR.");
        }

        if (order.getProduct() == null || order.getProduct().getId() == null) {
            throw new BusinessValidationException("Product must be specified.");
        }
        Product product = productRepository.findById(order.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + order.getProduct().getId()));

        if (order.getPoNumber() == null || order.getPoNumber().isBlank()) {
            order.setPoNumber("PO-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }
        if (order.getQuantity() == null || order.getQuantity() < 1) {
            order.setQuantity(1);
        }
        if (order.getRate() == null) {
            order.setRate(product.getRate());
        }
        order.setTotal(order.getRate().multiply(BigDecimal.valueOf(order.getQuantity())));
        order.setVendor(vendor);
        order.setProduct(product);
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        return poRepository.save(order);
    }
}
