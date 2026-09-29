package com.library.ebook.repository;

import com.library.ebook.entity.Journal;
import com.library.ebook.entity.JournalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalRepository extends JpaRepository<Journal, Long> {
    Optional<Journal> findByCode(String code);
    List<Journal> findByType(JournalType type);
    List<Journal> findByActive(Boolean active);
}
