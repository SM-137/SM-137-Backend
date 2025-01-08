package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ModifyUserRequest;
import com.solux.sm137.dto.response.MyComplaintResponse;
import com.solux.sm137.dto.response.ResultResponse;
import com.solux.sm137.dto.response.ScrapResponse;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "개인정보 수정")
    @PatchMapping("/modify")
    public ApiResponse<Void> modifyUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ModifyUserRequest request
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        userService.modifyUser(accessToken, request);
        return ApiResponse.onSuccess(null, SuccessStatus._MODIFY_USER_INFO_SUCCESS);
    }

    @Operation(summary = "마이페이지")
    @GetMapping
    public ApiResponse<UserInfoResponse> getUserInfo(
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        UserInfoResponse userInfo = userService.getUserInfo(accessToken);
        return ApiResponse.onSuccess(userInfo, SuccessStatus._GET_USER_INFO_SUCCESS);
    }

    @Operation(summary = "내 민원")
    @GetMapping("/complaint")
    public ApiResponse<List<MyComplaintResponse>> getMyComplaints(
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        List<MyComplaintResponse> myComplaints = userService.getMyComplaints(accessToken);
        return ApiResponse.onSuccess(myComplaints, SuccessStatus._GET_MY_COMPLAINTS_SUCCESS);
    }

    @Operation(summary = "결과 조회")
    @GetMapping("/result")
    public ApiResponse<List<ResultResponse>> getResults(
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        List<ResultResponse> results = userService.getResults(accessToken);
        return ApiResponse.onSuccess(results, SuccessStatus._GET_RESULTS_SUCCESS);
    }

    @Operation(summary = "스크랩한 민원")
    @GetMapping("/scrap")
    public ApiResponse<List<ScrapResponse>> getScraps(
            @RequestHeader("Authorization") String token
    ) {
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        List<ScrapResponse> scraps = userService.getScraps(accessToken);
        return ApiResponse.onSuccess(scraps, SuccessStatus._GET_SCRAPS_SUCCESS);
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/signout")
    public ApiResponse<ResultResponse> signOut(@RequestHeader("Authorization") String token) {
        // 토큰에서 'Bearer ' 부분을 제거
        String accessToken = token.startsWith("Bearer ") ? token.substring(7).trim() : token;
        userService.deleteUser(accessToken);
        return ApiResponse.onSuccess(null, SuccessStatus._USER_DELETED);
    }
}
