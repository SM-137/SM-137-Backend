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

    @Query(value = "SELECT * FROM complaint c " +
            "WHERE MATCH(title, content_prob, content_dir, content_expect) " +
            "AGAINST(:keyword IN NATURAL LANGUAGE MODE)",
            nativeQuery = true)
    Optional<List<Complaint>> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM Scrap s WHERE s.complaint = :complaint AND s.user = :user")
    boolean existsScrapByComplaintAndUser(@Param("complaint") Complaint complaint, @Param("user") User user);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END " +
            "FROM ComplaintLike l WHERE l.complaint = :complaint AND l.user = :user")
    boolean existsLikeByComplaintAndUser(@Param("complaint") Complaint complaint, @Param("user") User user);

}

