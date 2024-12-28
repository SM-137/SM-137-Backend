package com.solux.sm137.controller;

import com.solux.sm137.dto.request.ModifyUserRequest;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
}
