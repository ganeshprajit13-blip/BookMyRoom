package com.library.ebook.repository;

import com.library.ebook.entity.AnalyticAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticAccountRepository extends JpaRepository<AnalyticAccount, Long> {
    Optional<AnalyticAccount> findByName(String name);
    List<AnalyticAccount> findByStatus(String status);
}
