package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ComplaintAnswerRequest;
import com.solux.sm137.dto.response.ComplaintAnswerResponse;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.dto.response.ManagerComplaintResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.*;
import com.solux.sm137.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ComplaintService complaintService;

    @GetMapping("/list")
    public ApiResponse<List<ManagerComplaintResponse>> getComplaintList() {
        List<ManagerComplaintResponse> complaintList = complaintService.getComplaintList();
        return ApiResponse.onSuccess(complaintList,  SuccessStatus._GET_MANAGER_COMPLAINTS_SUCCESS);
    }


    @GetMapping("/{complaintId}")
    public ApiResponse<ComplaintDetailResponse> getComplaintDetail(@PathVariable Long complaintId) {
        ComplaintDetailResponse complaintDetail = complaintService.getComplaintDetail(complaintId);
        if (complaintDetail != null) {
            return ApiResponse.onSuccess(complaintDetail, SuccessStatus._GET_COMPLAINTS_DETAILS_SUCCESS);
        } else {
            return ApiResponse.onFailure(null, FailureStatus._COMPLAINT_NOT_FOUND);
        }
    }

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
