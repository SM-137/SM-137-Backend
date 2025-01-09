package com.solux.sm137.controller;

import com.solux.sm137.domain.Attachment;
import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.ComplaintStatus;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.request.ComplaintUpdateRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.CategoryResponse;
import com.solux.sm137.dto.response.KeywordSearchResponse;
import com.solux.sm137.dto.response.UserComplaintDetailResponse;
import com.solux.sm137.infra.exception.UnauthorizedException;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.repository.AttachmentRepository;
import com.solux.sm137.repository.CategoryRepository;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;
    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;



    @Operation(summary = "민원 작성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<Void> createComplaint(
            @RequestHeader("Authorization") String token,
            @RequestPart("complaint") ComplaintRequest request,
            @RequestPart("files") MultipartFile[] files
    ) throws IOException {



        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);  // "Bearer " 제거
        }

        // JWT 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        // Example of Validation
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title is required.");
        }



        // 파일 유효성 검사
        if (files != null) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    throw new IllegalArgumentException("Empty file detected");
                }

                String contentType = file.getContentType();
                if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("application/"))) {
                    throw new IllegalArgumentException("Invalid file type: " + contentType);
                }
            }
        }

        //complaintService.createComplaint(token, request, files);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_COMPLAINTS_SUCCESS);
    }

    @Operation(summary = "민원 수정")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateComplaint(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @ModelAttribute ComplaintUpdateRequest request
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        //complaintService.ComplaintUpdate(accessToken, id, request);
        return ApiResponse.onSuccess(null, SuccessStatus._PUT_COMPLAINTS_SUCCESS);
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
