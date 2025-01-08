package com.solux.sm137.controller;

import com.solux.sm137.dto.response.LoginResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/google")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "로그인")
    @GetMapping("/login")
    public ApiResponse<LoginResponse> loginCallback(Authentication authentication) {
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        if (authenticationToken == null || authenticationToken.getPrincipal() == null) {
            // 인증되지 않은 유저 처리
            return ApiResponse.onFailure(null, FailureStatus._UNAUTHORIZED);
        }
        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String token = jwtTokenProvider.createToken(email);

        // 로그인 응답 생성
        LoginResponse loginResponse = new LoginResponse(token, name, email);
        return ApiResponse.onSuccess(loginResponse, SuccessStatus._LOGIN_SUCCESS);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // 클라이언트가 토큰을 삭제하도록 유도
        // JWT는 서버에 상태를 저장하지 않기 때문에 토큰이 삭제되면 로그아웃이 처리됨.
        return ApiResponse.onSuccess(null, SuccessStatus._LOGOUT_SUCCESS);
    }
}
