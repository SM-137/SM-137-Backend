package com.solux.sm137.repository;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.User;
import com.solux.sm137.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    // 특정 commentId와 userId로 좋아요 조회
    Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
