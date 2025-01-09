package com.solux.sm137.controller;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.CommentLike;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.CommentLikeRequest;
import com.solux.sm137.repository.CommentLikeRepository;
import com.solux.sm137.repository.CommentRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider; // JwtTokenProvider 추가
import com.solux.sm137.infra.apiPayload.status.FailureStatus; // FailureStatus 사용
import com.solux.sm137.infra.apiPayload.status.SuccessStatus; // SuccessStatus 사용
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments/{commentId}/like")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 추가

    @Operation(summary = "댓글 좋아요")
    @PostMapping
    public ResponseEntity<Map<String, Object>> likeComment(
            @PathVariable("commentId") Long commentId,
            @RequestHeader("Authorization") String token,
            @RequestBody @Validated CommentLikeRequest request) {

        // JWT 토큰 검증
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }
        if (!jwtTokenProvider.validateToken(token)) {
            return buildErrorResponse(FailureStatus._UNAUTHORIZED);
        }

        // JWT 토큰에서 이메일 추출하여 사용자 정보 확인
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return buildErrorResponse(FailureStatus._USER_NOT_FOUND);
        }

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return buildErrorResponse(FailureStatus._COMPLAINT_NOT_FOUND);
        }

        // 이미 좋아요를 눌렀는지 확인
        if (commentLikeRepository.findByCommentAndUser(comment, user).isPresent()) {
            return buildErrorResponse(FailureStatus._CONFLICT);
        }

        // 좋아요 저장
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLikeRepository.save(commentLike);

        // 성공 응답
        return buildSuccessResponse(SuccessStatus._POST_COMMENTS_SUCCESS);
    }

    // 오류 응답 빌드 (FailureStatus 사용)
    private ResponseEntity<Map<String, Object>> buildErrorResponse(FailureStatus status) {
        return ResponseEntity.status(status.getCode()).body(Map.of(
                "code", status.getCode(),
                "message", status.getMessage(),
                "data", null
        ));
    }

    // 성공 응답 빌드 (SuccessStatus 사용)
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(SuccessStatus status) {
        return ResponseEntity.status(status.getCode()).body(Map.of(
                "code", status.getCode(),
                "message", status.getMessage(),
                "data", null
        ));
    }
}
