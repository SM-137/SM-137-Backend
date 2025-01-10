package com.solux.sm137.repository;

import com.solux.sm137.domain.ComplaintLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComplaintLikeRepository extends JpaRepository<ComplaintLike, Long> {
    boolean existsByUserIdAndComplaintId(Long userId, Long complaintId); // 이미 좋아요가 있는지 확인

    Optional<ComplaintLike> findByUserIdAndComplaintId(Long userId, Long complaintId); // 좋아요 조회
}
