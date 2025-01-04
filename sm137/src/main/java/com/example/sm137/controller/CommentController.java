package com.example.sm137.controller;

import com.example.sm137.DTO.CommentRequest;
import com.example.sm137.entity.Comment;
import com.example.sm137.repository.CommentRepository;
import com.example.sm137.repository.ComplaintRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints/{complaintId}/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;

    public CommentController(CommentRepository commentRepository, ComplaintRepository complaintRepository) {
        this.commentRepository = commentRepository;
        this.complaintRepository = complaintRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable("complaintId") Long complaintId,
            @RequestBody @Validated CommentRequest request) {

        // 댓글 내용 검증
        if (request.getContent() == null || request.getContent().isEmpty() || request.getContent().length() > 200) {
            return buildErrorResponse(400, "댓글 내용을 입력해주세요. (최대 200자)");
        }

        // 민원 존재 여부 확인
        var complaint = complaintRepository.findById(complaintId).orElse(null);
        if (complaint == null) {
            return buildErrorResponse(404, "존재하지 않는 민원입니다.");
        }

        try {
            // 댓글 저장
            Comment comment = new Comment();
            comment.setComplaintId(complaintId);
            comment.setUserId(request.getUserId()); // 요청에서 사용자 ID를 받음
            comment.setCommentContent(request.getContent());
            comment.setCreatedAt(LocalDateTime.now());
            commentRepository.save(comment);

            // 성공 응답
            return buildSuccessResponse(201, "댓글이 성공적으로 달렸습니다.", Map.of(
                    "comment_id", comment.getCommentId(),
                    "complaint_id", comment.getComplaintId(),
                    "user_id", comment.getUserId(),
                    "content", comment.getCommentContent(),
                    "created_at", comment.getCreatedAt()
            ));

        } catch (Exception e) {
            return buildErrorResponse(500, "댓글 작성 중 오류가 발생했습니다.");
        }
    }

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

    private ResponseEntity<Map<String, Object>> buildErrorResponse(int code, String message) {
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message,
                "data", Collections.emptyMap()
        ));
    }

    private ResponseEntity<Map<String, Object>> buildSuccessResponse(int code, String message, Map<String, Object> data) {
        return ResponseEntity.status(code).body(Map.of(
                "code", code,
                "message", message,
                "data", data
        ));
    }
}
