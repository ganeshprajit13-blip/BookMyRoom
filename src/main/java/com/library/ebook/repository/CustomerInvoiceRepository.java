package com.library.ebook.repository;

import com.library.ebook.entity.CustomerInvoice;
import com.library.ebook.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerInvoiceRepository extends JpaRepository<CustomerInvoice, Long> {
    Optional<CustomerInvoice> findByInvoiceNumber(String invoiceNumber);
    List<CustomerInvoice> findByPatronId(Long patronId);
    List<CustomerInvoice> findByStatus(InvoiceStatus status);

    @Query("SELECT SUM(ci.amount) FROM CustomerInvoice ci WHERE ci.status <> 'DRAFT'")
    BigDecimal sumTotalPostedInvoices();

    @Query("SELECT SUM(ci.amount - ci.paidAmount) FROM CustomerInvoice ci WHERE ci.status IN ('POSTED', 'PARTIALLY_PAID')")
    BigDecimal sumOutstandingReceivables();

    @Query("SELECT SUM(ci.amount) FROM CustomerInvoice ci WHERE ci.status <> 'DRAFT' AND ci.invoiceDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
