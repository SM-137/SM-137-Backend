package com.solux.sm137.controller;

import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.CategoryResponse;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.dto.response.KeywordSearchResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

    @PostMapping("/scrap")
    public ApiResponse<Void> scrapComplaint(
            @RequestHeader("Authorization") String token,
            @RequestBody ScrapRequest request
    ) {
        complaintService.scrapComplaint(token, request);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_SCRAPS_SUCCESS);
    }

    @GetMapping("/detail/{complaintId}")
    public ApiResponse<ComplaintDetailResponse> getComplaintDetail(
            @PathVariable Long complaintId
    ){
        ComplaintDetailResponse detailResponse= complaintService.getComplaintDetail(complaintId);
        return ApiResponse.onSuccess(detailResponse, SuccessStatus._GET_DETAIL_SUCCESS);
    }

    @GetMapping("/category")
    public ApiResponse<List<CategoryResponse>> getComplaintCategory(
            @RequestBody CategoryRequest request
    ){
        List<CategoryResponse> categoryResponse = complaintService.getComplaintCategory(request);
        return ApiResponse.onSuccess(categoryResponse, SuccessStatus._GET_CATEGORY_COMPLAINTES_SUCCESS);
    }

    @GetMapping("/search")
    public ApiResponse<List<KeywordSearchResponse>> getComplaintKeyword(
            @RequestParam(required = true) String keyword
    ){
        List<KeywordSearchResponse> keywordSearchResponses = complaintService.getComplaintKeyword(keyword);
        return ApiResponse.onSuccess(keywordSearchResponses, SuccessStatus._GET_KEYWORD_COMPLAINTES_SUCCESS);
    }

}