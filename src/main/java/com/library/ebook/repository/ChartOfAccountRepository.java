package com.library.ebook.repository;

import com.library.ebook.entity.AccountType;
import com.library.ebook.entity.ChartOfAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChartOfAccountRepository extends JpaRepository<ChartOfAccount, Long> {
    Optional<ChartOfAccount> findByCode(String code);
    default Optional<ChartOfAccount> findByAccountCode(String code) {
        return findByCode(code);
    }
    Optional<ChartOfAccount> findByName(String name);
    List<ChartOfAccount> findByType(AccountType type);
    List<ChartOfAccount> findByActive(Boolean active);
}
