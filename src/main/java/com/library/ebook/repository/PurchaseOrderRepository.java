package com.library.ebook.repository;

import com.library.ebook.entity.OrderStatus;
import com.library.ebook.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    List<PurchaseOrder> findByVendorId(Long vendorId);
    List<PurchaseOrder> findByStatus(OrderStatus status);
}
