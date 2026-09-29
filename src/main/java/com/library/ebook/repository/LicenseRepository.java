package com.library.ebook.repository;

import com.library.ebook.entity.License;
import com.library.ebook.entity.LicenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    List<License> findByEbookId(Long ebookId);
    List<License> findByStatus(LicenseStatus status);
    Optional<License> findByLicenseNumber(String licenseNumber);
    long countByStatus(LicenseStatus status);

    @Query("SELECT SUM(l.remainingAssetValue) FROM License l WHERE l.status = 'ACTIVE'")
    BigDecimal sumRemainingAssetValue();

    @Query("SELECT SUM(l.purchaseCost) FROM License l")
    BigDecimal sumTotalPurchaseCost();

    @Query("SELECT l FROM License l WHERE l.status = 'ACTIVE' AND l.licenseExpiryDate IS NOT NULL AND l.licenseExpiryDate <= :date")
    List<License> findExpiringLicenses(LocalDate date);
}
