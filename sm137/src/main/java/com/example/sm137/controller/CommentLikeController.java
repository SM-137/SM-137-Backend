package com.example.sm137.controller;

import com.example.sm137.DTO.CommentLikeRequest;
import com.example.sm137.entity.Comment;
import com.example.sm137.entity.CommentLike;
import com.example.sm137.repository.CommentLikeRepository;
import com.example.sm137.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments/{commentId}/like")
public class CommentLikeController {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    public CommentLikeController(CommentLikeRepository commentLikeRepository, CommentRepository commentRepository) {
        this.commentLikeRepository = commentLikeRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> likeComment(
            @PathVariable("commentId") Long commentId,
            @RequestBody @Validated CommentLikeRequest request) {

        // 댓글 존재 여부 확인
        if (!commentRepository.existsById(commentId)) {
            return buildErrorResponse(404, "존재하지 않는 댓글입니다.");
        }

        // 이미 좋아요를 눌렀는지 확인
        if (commentLikeRepository.findByCommentIdAndUserId(commentId, request.getUserId()).isPresent()) {
            return buildErrorResponse(409, "이미 이 댓글에 좋아요를 누르셨습니다.");
        }

        // 좋아요 저장
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(request.getUserId());
        commentLike.setCreatedAt(LocalDateTime.now());
        commentLikeRepository.save(commentLike);
        commentLikeRepository.flush();  // 즉시 DB에 반영

        // 성공 응답
        return buildSuccessResponse(201, "댓글 좋아요가 성공적으로 달렸습니다.", Map.of(
                "commentlike_id", commentLike.getCommentlikeId(),
                "comment_id", commentLike.getCommentId(),
                "user_id", commentLike.getUserId(),
                "created_at", commentLike.getCreatedAt()
        ));
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        response.put("data", null);
        return ResponseEntity.status(code).body(response);
    }


    private ResponseEntity<Map<String, Object>> buildSuccessResponse(int code, String message, Map<String, Object> data) {
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message,
                "data", data
        ));
    }
}
