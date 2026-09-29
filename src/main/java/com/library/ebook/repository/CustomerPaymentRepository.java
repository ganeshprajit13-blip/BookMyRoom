package com.library.ebook.repository;

import com.library.ebook.entity.CustomerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long> {
    Optional<CustomerPayment> findByPaymentNumber(String paymentNumber);
    List<CustomerPayment> findByCustomerInvoiceId(Long invoiceId);

    @Query("SELECT SUM(cp.amount) FROM CustomerPayment cp WHERE cp.status = 'COMPLETED'")
    BigDecimal sumTotalPayments();
}
