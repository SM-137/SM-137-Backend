package com.example.sm137.repository;

import com.example.sm137.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 특정 commentId와 userId로 좋아요 조회
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
}
