package com.library.ebook.service;

import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.repository.ContactRepository;
import com.library.ebook.repository.ProductRepository;
import com.library.ebook.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SalesOrderService {

    private final SalesOrderRepository soRepository;
    private final ContactRepository contactRepository;
    private final ProductRepository productRepository;

    public SalesOrderService(SalesOrderRepository soRepository,
                             ContactRepository contactRepository,
                             ProductRepository productRepository) {
        this.soRepository = soRepository;
        this.contactRepository = contactRepository;
        this.productRepository = productRepository;
    }

    public List<SalesOrder> getAllOrders() {
        return soRepository.findAll();
    }

    public SalesOrder getOrderById(Long id) {
        return soRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sales Order not found with ID: " + id));
    }

    public SalesOrder createOrder(SalesOrder order) {
        if (order.getPatron() == null || order.getPatron().getId() == null) {
            throw new BusinessValidationException("Patron must be specified.");
        }
        Contact patron = contactRepository.findById(order.getPatron().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patron not found with ID: " + order.getPatron().getId()));

        if (patron.getContactType() != ContactType.CUSTOMER) {
            throw new BusinessValidationException("Contact must be of type CUSTOMER.");
        }

        if (order.getProduct() == null || order.getProduct().getId() == null) {
            throw new BusinessValidationException("Product must be specified.");
        }
        Product product = productRepository.findById(order.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + order.getProduct().getId()));

        if (order.getSoNumber() == null || order.getSoNumber().isBlank()) {
            order.setSoNumber("SO-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
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
        order.setPatron(patron);
        order.setProduct(product);
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.CONFIRMED);
        }

        return soRepository.save(order);
    }
}
