package com.solux.sm137.controller;

import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.CategoryResponse;
import com.solux.sm137.dto.response.KeywordSearchResponse;
import com.solux.sm137.dto.response.UserComplaintDetailResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

    @Operation(summary = "민원스크랩")
    @PostMapping("/scrap")
    public ApiResponse<Void> scrapComplaint(
            @RequestHeader("Authorization") String token,
            @RequestBody ScrapRequest request
    ) {
        complaintService.scrapComplaint(token, request);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_SCRAPS_SUCCESS);
    }

    @Operation(summary = "민원내용 상세")
    @GetMapping("/detail/{complaintId}")
    public ApiResponse<UserComplaintDetailResponse> getComplaintDetail(
            @PathVariable Long complaintId
    ) {
        UserComplaintDetailResponse detailResponse = complaintService.getUserComplaintDetail(complaintId);
        return ApiResponse.onSuccess(detailResponse, SuccessStatus._GET_DETAIL_SUCCESS);
    }

    @Operation(summary = "전체민원조회")
    @GetMapping("/category")
    public ApiResponse<List<CategoryResponse>> getComplaintCategory(
            @RequestBody CategoryRequest request
    ) {
        List<CategoryResponse> categoryResponse = complaintService.getComplaintCategory(request);
        return ApiResponse.onSuccess(categoryResponse, SuccessStatus._GET_CATEGORY_COMPLAINTS_SUCCESS);
    }

    @Operation(summary = "민원 키워드검색")
    @GetMapping("/search")
    public ApiResponse<List<KeywordSearchResponse>> getComplaintKeyword(
            @RequestParam(required = true) String keyword
    ) {
        List<KeywordSearchResponse> keywordSearchResponses = complaintService.getComplaintKeyword(keyword);
        return ApiResponse.onSuccess(keywordSearchResponses, SuccessStatus._GET_KEYWORD_COMPLAINTS_SUCCESS);
    }

}