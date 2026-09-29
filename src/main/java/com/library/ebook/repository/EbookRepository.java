package com.library.ebook.repository;

import com.library.ebook.entity.Ebook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EbookRepository extends JpaRepository<Ebook, Long> {
    Optional<Ebook> findByIsbn(String isbn);
    List<Ebook> findByStatus(String status);
    List<Ebook> findByCategory(String category);
}
