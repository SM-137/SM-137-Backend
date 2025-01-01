package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

    @PostMapping("/scrap")
    public ApiResponse<Void> scrapComplaint(
            @RequestHeader("Authorization") String token,
            @RequestBody ComplaintRequest request
    ) {
        complaintService.scrapComplaint(token, request);
        return ApiResponse.onSuccess(null, SuccessStatus._POST_SCRAPS_SUCCESS);
    }

    @GetMapping("/detail")
    public ApiResponse<ComplaintDetailResponse> getComplaintDetail(
            @RequestBody ComplaintRequest request
    ){
        ComplaintDetailResponse detailResponse= complaintService.getComplaintDetail(request);
        return ApiResponse.onSuccess(detailResponse, SuccessStatus._GET_DETAIL_SUCCESS);

    }
}
