package com.library.ebook.repository;

import com.library.ebook.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActive(Boolean active);
    Optional<Product> findByName(String name);
}
