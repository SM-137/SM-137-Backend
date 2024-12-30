package com.solux.sm137.controller;

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


    @GetMapping("/complaint/{id}")
    public ApiResponse<ComplaintDetailResponse> getComplaintDetail(@PathVariable Long id) {
        ComplaintDetailResponse complaintDetail = complaintService.getComplaintDetail(id);
        if (complaintDetail != null) {
            return ApiResponse.onSuccess(complaintDetail, SuccessStatus._GET_COMPLAINTS_DETAILS_SUCCESS);
        } else {
            return ApiResponse.onFailure(null, FailureStatus._COMPLAINT_NOT_FOUND);
        }
    }
}
