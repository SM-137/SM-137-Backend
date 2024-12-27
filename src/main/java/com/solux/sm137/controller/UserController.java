package com.solux.sm137.controller;

import com.solux.sm137.dto.ModifyUserRequest;
import com.solux.sm137.infra.apiPayload.ApiResponse;
import com.solux.sm137.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/modify")
    public ApiResponse<Void> userModify(
            @RequestHeader("Authorization") String token,
            @RequestBody ModifyUserRequest request
    ) {
        userService.modifyUser(token, request);
        return ApiResponse.onSuccess(null);
    }
}
