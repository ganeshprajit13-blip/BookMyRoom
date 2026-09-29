package com.library.ebook.repository;

import com.library.ebook.entity.AccountType;
import com.library.ebook.entity.JournalItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JournalItemRepository extends JpaRepository<JournalItem, Long> {

    List<JournalItem> findByAccountId(Long accountId);

    @Query("SELECT SUM(ji.debit) FROM JournalItem ji WHERE ji.account.id = :accountId AND ji.journalEntry.posted = true")
    BigDecimal sumDebitByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT SUM(ji.credit) FROM JournalItem ji WHERE ji.account.id = :accountId AND ji.journalEntry.posted = true")
    BigDecimal sumCreditByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT SUM(ji.debit) FROM JournalItem ji WHERE ji.account.code = :code AND ji.journalEntry.posted = true")
    BigDecimal sumDebitByAccountCode(@Param("code") String code);

    @Query("SELECT SUM(ji.credit) FROM JournalItem ji WHERE ji.account.code = :code AND ji.journalEntry.posted = true")
    BigDecimal sumCreditByAccountCode(@Param("code") String code);

    @Query("SELECT SUM(ji.debit) FROM JournalItem ji WHERE ji.account.type = :type AND ji.journalEntry.posted = true")
    BigDecimal sumDebitByAccountType(@Param("type") AccountType type);

    @Query("SELECT SUM(ji.credit) FROM JournalItem ji WHERE ji.account.type = :type AND ji.journalEntry.posted = true")
    BigDecimal sumCreditByAccountType(@Param("type") AccountType type);

    @Query("SELECT SUM(ji.debit) FROM JournalItem ji WHERE ji.account.code = :code AND ji.journalEntry.posted = true AND ji.journalEntry.entryDate BETWEEN :startDate AND :endDate")
    BigDecimal sumDebitByAccountCodeAndDateRange(@Param("code") String code, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
