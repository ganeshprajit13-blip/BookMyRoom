package com.library.ebook.repository;

import com.library.ebook.entity.BillStatus;
import com.library.ebook.entity.VendorBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorBillRepository extends JpaRepository<VendorBill, Long> {
    Optional<VendorBill> findByBillNumber(String billNumber);
    List<VendorBill> findByVendorId(Long vendorId);
    List<VendorBill> findByStatus(BillStatus status);

    @Query("SELECT SUM(vb.amount) FROM VendorBill vb WHERE vb.status <> 'DRAFT'")
    BigDecimal sumTotalPostedBills();

    @Query("SELECT SUM(vb.amount - vb.paidAmount) FROM VendorBill vb WHERE vb.status IN ('POSTED', 'PARTIALLY_PAID')")
    BigDecimal sumOutstandingPayables();

    @Query("SELECT SUM(vb.amount) FROM VendorBill vb WHERE vb.status <> 'DRAFT' AND vb.billDate BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
