package com.solux.sm137.controller;

import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.request.ComplaintUpdateRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.CategoryResponse;
import com.solux.sm137.dto.response.ComplaintResponse;
import com.solux.sm137.dto.response.KeywordSearchResponse;
import com.solux.sm137.dto.response.UserComplaintDetailResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "민원 작성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createComplaint(
            @RequestHeader("Authorization") String token,
            @RequestPart("requestDto") @Valid ComplaintRequest complaintRequest,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        complaintService.createComplaint(accessToken, complaintRequest, attachments);
//        return ApiResponse.onSuccess(null, SuccessStatus._POST_COMPLAINTS_SUCCESS);
        return "성공";
    }


    private ApiResponse buildErrorResponse(int code, String message) {
        FailureStatus failureStatus = FailureStatus.getByCode(code);
        return failureStatus != null ? ApiResponse.onFailure(null, failureStatus) :
                ApiResponse.onFailure(null, FailureStatus._INTERNAL_SERVER_ERROR);
    }

    @Operation(summary = "민원 수정")
    @PatchMapping("/{complaintId}")
    public ApiResponse<Void> updateComplaint(
            @RequestHeader("Authorization") String token,
            @PathVariable Long complaintId,
            @RequestBody ComplaintUpdateRequest request) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        complaintService.updateComplaint(accessToken, complaintId, request);
        return ApiResponse.onSuccess(null, SuccessStatus._PUT_COMPLAINTS_UPDATE_SUCCESS);
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
    @DeleteMapping("/scrap")
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
            @RequestHeader("Authorization") String token,
            @PathVariable Long complaintId
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        UserComplaintDetailResponse detailResponse = complaintService.getUserComplaintDetail(accessToken, complaintId);
        return ApiResponse.onSuccess(detailResponse, SuccessStatus._GET_DETAIL_SUCCESS);
    }

    // 전체 민원 조회
    @Operation(summary = "전체민원조회")
    @PostMapping("/category")
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
    ) {
        List<KeywordSearchResponse> keywordSearchResponses = complaintService.getComplaintKeyword(keyword);
        return ApiResponse.onSuccess(keywordSearchResponses, SuccessStatus._GET_KEYWORD_COMPLAINTS_SUCCESS);
    }
}
