package com.library.ebook.repository;

import com.library.ebook.entity.OrderStatus;
import com.library.ebook.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    Optional<SalesOrder> findBySoNumber(String soNumber);
    List<SalesOrder> findByPatronId(Long patronId);
    List<SalesOrder> findByStatus(OrderStatus status);
}
