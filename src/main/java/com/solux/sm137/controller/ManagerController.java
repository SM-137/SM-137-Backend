package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ComplaintAnswerRequest;
import com.solux.sm137.dto.response.ComplaintAnswerResponse;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.dto.response.ManagerComplaintResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ComplaintService complaintService;

    @Operation(summary = "처리 민원 리스트")
    @GetMapping("/list")
    public ApiResponse<List<ManagerComplaintResponse>> getComplaintList() {
        List<ManagerComplaintResponse> complaintList = complaintService.getComplaintList();
        return ApiResponse.onSuccess(complaintList, SuccessStatus._GET_MANAGER_COMPLAINTS_SUCCESS);
    }

    @Operation(summary = "민원 상세 조회")
    @GetMapping("/{complaintId}")
    public ApiResponse<ComplaintDetailResponse> getComplaintDetail(@PathVariable Long complaintId) {
        ComplaintDetailResponse complaintDetail = complaintService.getComplaintDetail(complaintId);
        if (complaintDetail != null) {
            return ApiResponse.onSuccess(complaintDetail, SuccessStatus._GET_COMPLAINTS_DETAILS_SUCCESS);
        } else {
            return ApiResponse.onFailure(null, FailureStatus._COMPLAINT_NOT_FOUND);
        }
    }

    @Operation(summary = "민원 답변 등록")
    @PostMapping("/{complaintId}/answer")
    public ApiResponse<ComplaintAnswerResponse> registerComplaintAnswer(
            @PathVariable Long complaintId,
            @RequestBody ComplaintAnswerRequest request) {
        try {
            ComplaintAnswerResponse response = complaintService.registerComplaintAnswer(complaintId, request);
            return ApiResponse.onSuccess(response, SuccessStatus._POST_ANSWER_SUCCESS);
        } catch (Exception e) {
            return ApiResponse.onFailure(null, FailureStatus._ANSWER_REGISTRATION_FAILED);
        }
    }
}
