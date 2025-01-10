package com.solux.sm137.repository;

import com.solux.sm137.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserIdAndCommentId(Long userId, Long commentId); // 이미 좋아요가 존재하는지 확인

    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId); // 좋아요 조회
}
