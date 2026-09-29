package com.library.ebook.repository;

import com.library.ebook.entity.VendorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorPaymentRepository extends JpaRepository<VendorPayment, Long> {
    Optional<VendorPayment> findByPaymentNumber(String paymentNumber);
    List<VendorPayment> findByVendorBillId(Long vendorBillId);

    @Query("SELECT SUM(vp.amount) FROM VendorPayment vp WHERE vp.status = 'COMPLETED'")
    BigDecimal sumTotalPayments();
}
