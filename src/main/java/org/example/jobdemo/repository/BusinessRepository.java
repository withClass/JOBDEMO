package org.example.jobdemo.repository;

import org.example.jobdemo.entity.Business;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Query("SELECT b FROM Business b WHERE LOWER(b.businessName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Business> searchByBusinessName(@Param("keyword") String keyword, Pageable pageable);

    Optional<Business> findById(Long id);
}
