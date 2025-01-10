package com.solux.sm137.controller;

import com.solux.sm137.domain.Comment;
import com.solux.sm137.dto.request.CommentRequest;
import com.solux.sm137.dto.response.CommentResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/complaints/{complaintId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ApiResponse<CommentResponse> createComment(
            @RequestHeader("Authorization") String token,
            @PathVariable("complaintId") Long complaintId,
            @RequestBody CommentRequest request) {

        // JWT 토큰 검증
        String accessToken = token != null && token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();  // "Bearer " 제거 후 trim()
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        // 댓글 작성 서비스 호출
        Comment comment = commentService.createComment(accessToken, complaintId, request);

        // CommentResponse로 변환
        CommentResponse commentResponse = new CommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getEmail(),
                comment.getContent(),
                comment.getCreatedAt()
        );

        return ApiResponse.onSuccess(commentResponse, SuccessStatus._POST_COMMENTS_SUCCESS);
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(@PathVariable("complaintId") Long complaintId) {
        List<Comment> comments = commentService.getComments(complaintId);

        // 댓글 목록을 CommentResponse 목록으로 변환
        List<CommentResponse> commentResponses = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getUser().getId(),
                        comment.getUser().getEmail(),
                        comment.getContent(),
                        comment.getCreatedAt()
                ))
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(commentResponses, SuccessStatus._GET_COMMENTS_SUCCESS);
    }

    // 에러 응답 처리
    private ApiResponse buildErrorResponse(int code, String message) {
        // FailureStatus에 해당하는 오류를 찾기
        FailureStatus failureStatus = FailureStatus.getByCode(code);

        if (failureStatus != null) {
            // FailureStatus가 존재하는 경우, onFailure 메서드를 호출하여 응답 반환
            return ApiResponse.onFailure(null, failureStatus);
        } else {
            // 상태 코드에 맞는 FailureStatus가 없으면, 기본 메시지와 함께 실패 상태를 반환
            return ApiResponse.onFailure(null, FailureStatus._INTERNAL_SERVER_ERROR);
        }
    }


}
