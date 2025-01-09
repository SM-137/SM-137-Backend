package com.solux.sm137.controller;

import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.service.ComplaintLikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints/{complaintId}/like")
@RequiredArgsConstructor
public class ComplaintLikeController {

    private final ComplaintLikeService complaintLikeService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "민원 좋아요 추가")
    @PostMapping
    public ApiResponse<Void> addComplaintLike(
            @RequestHeader("Authorization") String token,
            @PathVariable("complaintId") Long complaintId) {

        // JWT 토큰 검증
        String accessToken = token != null && token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();  // "Bearer " 제거 후 trim()
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        try {
            // 민원 좋아요 추가 서비스 호출
            complaintLikeService.addComplaintLike(accessToken, complaintId);
            return ApiResponse.onSuccess(null, SuccessStatus._POST_COMPLAINT_LIKE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }

    @Operation(summary = "민원 좋아요 삭제")
    @DeleteMapping
    public ApiResponse<Void> removeComplaintLike(
            @RequestHeader("Authorization") String token,
            @PathVariable("complaintId") Long complaintId) {

        // JWT 토큰 검증
        String accessToken = token != null && token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();  // "Bearer " 제거 후 trim()
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        try {
            // 민원 좋아요 삭제 서비스 호출
            complaintLikeService.removeComplaintLike(accessToken, complaintId);
            return ApiResponse.onSuccess(null, SuccessStatus._DELETE_COMPLAINT_LIKE_SUCCESS);
        } catch (IllegalArgumentException e) {
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
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
