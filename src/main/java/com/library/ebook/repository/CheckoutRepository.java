package com.library.ebook.repository;

import com.library.ebook.entity.Checkout;
import com.library.ebook.entity.CheckoutStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {
    List<Checkout> findByStatus(CheckoutStatus status);
    List<Checkout> findByPatronId(Long patronId);
    List<Checkout> findByLicenseId(Long licenseId);
    Optional<Checkout> findByAccessKey(String accessKey);
    long countByStatus(CheckoutStatus status);
    long countByPatronIdAndStatus(Long patronId, CheckoutStatus status);
    List<Checkout> findByPatronIdAndLicenseIdAndStatus(Long patronId, Long licenseId, CheckoutStatus status);
    List<Checkout> findByStatusAndExpiryDateBefore(CheckoutStatus status, LocalDateTime dateTime);
}
