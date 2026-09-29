package com.library.ebook.repository;

import com.library.ebook.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByStatus(String status);
    List<Budget> findByAnalyticAccountId(Long analyticAccountId);
}
