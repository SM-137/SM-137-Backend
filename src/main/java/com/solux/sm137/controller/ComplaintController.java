package com.solux.sm137.controller;

import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.request.ComplaintUpdateRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.*;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    public ApiResponse<ComplaintResponse> createComplaint(
            @RequestHeader("Authorization") String token,
            @RequestParam("title") String title,
            @RequestParam("contentProb") String contentProb,
            @RequestParam("contentDir") String contentDir,
            @RequestParam("contentExpect") String contentExpect,
            @RequestParam("tagId") Long tagId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) {

        // 필수 파라미터 유효성 검사
        if (title == null || contentProb == null || contentDir == null || contentExpect == null ) {
            return buildErrorResponse(400, "Missing required fields.");
        }

        // JWT 토큰 검증
        String accessToken = token != null && token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        // ComplaintRequest 객체 생성
        ComplaintRequest complaintRequest = new ComplaintRequest(
                categoryId,
                tagId,
                title,
                contentProb,
                contentDir,
                contentExpect,
                Arrays.asList(attachments)  // MultipartFile[]을 List로 변환
        );

        // 정상적인 처리 로직 (파일 처리 포함)
        try {
            ComplaintResponse complaintResponse = complaintService.createComplaint(complaintRequest, accessToken);
            return ApiResponse.onSuccess(complaintResponse, SuccessStatus._POST_COMPLAINTS_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }


    private ApiResponse buildErrorResponse(int code, String message) {
        FailureStatus failureStatus = FailureStatus.getByCode(code);
        return failureStatus != null ? ApiResponse.onFailure(null, failureStatus) :
                ApiResponse.onFailure(null, FailureStatus._INTERNAL_SERVER_ERROR);
    }



    @Operation(summary = "민원 수정")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateComplaint(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @ModelAttribute ComplaintUpdateRequest request,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments) {

        // JWT 토큰에서 accessToken 추출
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();

        // 토큰 검증
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return buildErrorResponse(401, "Unauthorized: Invalid or expired token.");
        }

        try {
            // 민원 수정 처리
            complaintService.updateComplaint(accessToken, id, request, attachments);
            return ApiResponse.onSuccess(null, SuccessStatus._PUT_COMPLAINTS_UPDATE_SUCCESS);
        } catch (Exception e) {
            // 예외 발생 시 오류 처리
            return ApiResponse.onFailure(null, FailureStatus._BAD_REQUEST);
        }
    }




    // 민원 스크랩 추가
    @Operation(summary = "민원스크랩")
    @PostMapping("/scrap")
    public ApiResponse<Void> scrapComplaint(
            @RequestHeader("Authorization") String token,
            @RequestBody ScrapRequest request
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        complaintService.scrapComplaint(accessToken, request);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_SCRAPS_SUCCESS);
    }

    // 민원 스크랩 취소
    @Operation(summary = "민원스크랩 취소")
    @DeleteMapping("/scrap/delete")
    public ApiResponse<Void> deleteScrapComplaint(
            @RequestHeader("Authorization") String token,
            @RequestBody ScrapRequest request
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        complaintService.deleteScrapComplaint(accessToken, request);
        return ApiResponse.onSuccess(null, SuccessStatus._DELETE_SCRAPS_SUCCESS);
    }

    // 민원 상세 조회
    @Operation(summary = "민원내용 상세")
    @GetMapping("/detail/{complaintId}")
    public ApiResponse<UserComplaintDetailResponse> getComplaintDetail(
            @PathVariable Long complaintId
    ) {
        UserComplaintDetailResponse detailResponse = complaintService.getUserComplaintDetail(complaintId);
        return ApiResponse.onSuccess(detailResponse, SuccessStatus._GET_DETAIL_SUCCESS);
    }

    // 전체 민원 조회
    @Operation(summary = "전체민원조회")
    @GetMapping("/category")
    public ApiResponse<List<CategoryResponse>> getComplaintCategory(
            @RequestBody CategoryRequest request
    ) {
        List<CategoryResponse> categoryResponse = complaintService.getComplaintCategory(request);
        return ApiResponse.onSuccess(categoryResponse, SuccessStatus._GET_CATEGORY_COMPLAINTS_SUCCESS);
    }

    // 민원 키워드 검색
    @Operation(summary = "민원 키워드검색")
    @GetMapping("/search")
    public ApiResponse<List<KeywordSearchResponse>> getComplaintKeyword(
            @RequestParam(required = true) String keyword
    ){
        List<KeywordSearchResponse> keywordSearchResponses = complaintService.getComplaintKeyword(keyword);
        return ApiResponse.onSuccess(keywordSearchResponses, SuccessStatus._GET_KEYWORD_COMPLAINTS_SUCCESS);
    }
}
