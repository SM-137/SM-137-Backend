package com.solux.sm137.repository;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.CommentLike;
import com.solux.sm137.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByUserAndComment(User user, Comment comment); // 이미 좋아요가 존재하는지 확인

    int countByComment(Comment comment);

    Optional<CommentLike> findByUserAndComment(User user, Comment comment); // 좋아요 조회

}
