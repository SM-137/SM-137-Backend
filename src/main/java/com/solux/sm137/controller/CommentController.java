package com.solux.sm137.controller;

import com.solux.sm137.dto.request.CommentRequest;
import com.solux.sm137.dto.response.CommentResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints/{complaintId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ApiResponse<Void> createComment(
            @RequestHeader("Authorization") String token,
            @PathVariable("complaintId") Long complaintId,
            @RequestBody CommentRequest request
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        commentService.createComment(accessToken, complaintId, request);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_COMMENTS_SUCCESS);
    }

    @Operation(summary = "댓글 목록 조회")
    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(
            @RequestHeader("Authorization") String token,
            @PathVariable("complaintId") Long complaintId
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        List<CommentResponse> comments = commentService.getComments(complaintId, accessToken);
        return ApiResponse.onSuccess(comments, SuccessStatus._GET_COMMENTS_SUCCESS);
    }
}
