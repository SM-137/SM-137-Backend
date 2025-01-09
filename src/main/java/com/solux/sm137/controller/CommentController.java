package com.solux.sm137.controller;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.CommentRequest;
import com.solux.sm137.repository.CommentRepository;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider; // JwtTokenProvider import 추가
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints/{complaintId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 추가

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable("complaintId") Long complaintId,
            @RequestHeader("Authorization") String token, // Authorization 헤더에서 토큰 받기
            @RequestPart("comment") CommentRequest request) {  // comment 파트를 받음

        // JWT 토큰 검증
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        }
        if (!jwtTokenProvider.validateToken(token)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        // 댓글 내용 검증
        if (request.getContent() == null || request.getContent().isEmpty() || request.getContent().length() > 200) {
            return buildErrorResponse(400, "댓글 내용을 입력해주세요. (최대 200자)");
        }

        // 민원 존재 여부 확인
        var complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint == null) {
            return buildErrorResponse(404, "존재하지 않는 민원입니다.");
        }

        // 사용자 정보 확인 (JWT 토큰에서 사용자 이메일로 사용자 찾기)
        String email = jwtTokenProvider.getEmailFromToken(token);
        var user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return buildErrorResponse(404, "존재하지 않는 사용자입니다.");
        }

        try {
            // 댓글 저장
            Comment comment = new Comment();
            comment.setComplaint(complaint); // 민원 설정
            comment.setUser(user); // 사용자 설정
            comment.setContent(request.getContent()); // 댓글 내용 설정
            commentRepository.save(comment);

            // 성공 응답
            return buildSuccessResponse(201, "댓글이 성공적으로 달렸습니다.", Map.of(
                    "comment_id", comment.getId(),
                    "complaint_id", comment.getComplaint().getId(),
                    "user_id", comment.getUser().getId(),
                    "content", comment.getContent(),
                    "created_at", comment.getCreatedAt()
            ));

        } catch (Exception e) {
            return buildErrorResponse(500, "댓글 작성 중 오류가 발생했습니다.");
        }
    }

    // 댓글 목록 조회
    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getComments(@PathVariable("complaintId") Long complaintId) {
        // 민원 존재 여부 확인
        var complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint == null) {
            return buildErrorResponse(404, "존재하지 않는 민원입니다.");
        }

        // 해당 민원에 달린 댓글 조회
        List<Comment> comments = commentRepository.findByComplaintId(complaintId);

        // 응답 데이터 구성
        return buildSuccessResponse(200, "댓글 목록이 성공적으로 조회되었습니다.", Map.of(
                "comments", comments
        ));
    }

    // 에러 응답 처리
    private ResponseEntity<Map<String, Object>> buildErrorResponse(int code, String message) {
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message,
                "data", Collections.emptyMap()
        ));
    }

    // 성공 응답 처리
    private ResponseEntity<Map<String, Object>> buildSuccessResponse(int code, String message, Map<String, Object> data) {
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message,
                "data", data
        ));
    }
}
