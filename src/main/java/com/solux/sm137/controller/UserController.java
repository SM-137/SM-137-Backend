package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ModifyUserRequest;
import com.solux.sm137.dto.response.MyComplaintResponse;
import com.solux.sm137.dto.response.ResultResponse;
import com.solux.sm137.dto.response.ScrapResponse;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/modify")
    public ApiResponse<Void> modifyUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ModifyUserRequest request
    ) {
        userService.modifyUser(token, request);
        return ApiResponse.onSuccess(null, SuccessStatus._MODIFY_USER_INFO_SUCCESS);
    }

    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo(
            @RequestHeader("Authorization") String token
    ) {
        UserInfoResponse userInfo = userService.getUserInfo(token);
        return ApiResponse.onSuccess(userInfo, SuccessStatus._GET_USER_INFO_SUCCESS);
    }

    @GetMapping("/complaint")
    public ApiResponse<List<MyComplaintResponse>> getMyComplaints(
            @RequestHeader("Authorization") String token
    ) {
        List<MyComplaintResponse> myComplaints = userService.getMyComplaints(token);
        return ApiResponse.onSuccess(myComplaints, SuccessStatus._GET_MY_COMPLAINTS_SUCCESS);
    }

    @GetMapping("/result")
    public ApiResponse<List<ResultResponse>> getResults(
            @RequestHeader("Authorization") String token
    ) {
        List<ResultResponse> results = userService.getResults(token);
        return ApiResponse.onSuccess(results, SuccessStatus._GET_RESULTS_SUCCESS);
    }

    @GetMapping("/scrap")
    public ApiResponse<List<ScrapResponse>> getScraps(
            @RequestHeader("Authorization") String token
    ) {
        List<ScrapResponse> scraps = userService.getScraps(token);
        return ApiResponse.onSuccess(scraps, SuccessStatus._GET_SCRAPS_SUCCESS);
    }
}
