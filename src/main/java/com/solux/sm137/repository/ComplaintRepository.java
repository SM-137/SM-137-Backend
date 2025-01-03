package com.solux.sm137.repository;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<List<Complaint>> findByUser(User user);
    Optional<Complaint> findById(Long id);

    @Query("SELECT c FROM Complaint c JOIN c.category Category WHERE Category.categoryName = :categoryName")
    Optional<List<Complaint>> findByCategoryName(@Param("categoryName") String categoryName);
}
