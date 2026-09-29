package com.library.ebook.repository;

import com.library.ebook.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {
    Optional<JournalEntry> findByEntryNumber(String entryNumber);
    List<JournalEntry> findByJournalId(Long journalId);
    List<JournalEntry> findByPosted(Boolean posted);
    List<JournalEntry> findByEntryDateBetween(LocalDate start, LocalDate end);
    List<JournalEntry> findTop20ByOrderByEntryDateDescIdDesc();
}
