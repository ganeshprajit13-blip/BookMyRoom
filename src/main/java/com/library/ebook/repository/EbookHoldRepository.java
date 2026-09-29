package com.library.ebook.repository;

import com.library.ebook.entity.EbookHold;
import com.library.ebook.entity.HoldStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EbookHoldRepository extends JpaRepository<EbookHold, Long> {
    List<EbookHold> findByStatus(HoldStatus status);
    List<EbookHold> findByLicenseIdAndStatusOrderByQueuePositionAsc(Long licenseId, HoldStatus status);
    List<EbookHold> findByEbookIdAndStatusOrderByQueuePositionAsc(Long ebookId, HoldStatus status);
    List<EbookHold> findByPatronId(Long patronId);
    long countByStatus(HoldStatus status);
    long countByPatronIdAndStatus(Long patronId, HoldStatus status);
}
