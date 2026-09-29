package com.library.ebook.repository;

import com.library.ebook.entity.AccessKey;
import com.library.ebook.entity.AccessKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessKeyRepository extends JpaRepository<AccessKey, Long> {
    Optional<AccessKey> findByAccessKey(String accessKey);
    Optional<AccessKey> findByCheckoutId(Long checkoutId);
    List<AccessKey> findByStatus(AccessKeyStatus status);
    List<AccessKey> findByStatusAndExpiryDateBefore(AccessKeyStatus status, LocalDateTime dateTime);
}
