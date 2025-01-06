package com.solux.sm137.controller;

import com.solux.sm137.dto.response.LoginResponse;
import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @GetMapping("/callback")
    public ApiResponse<LoginResponse> loginCallback(OAuth2AuthenticationToken authenticationToken) {
        if (authenticationToken == null || authenticationToken.getPrincipal() == null) {
            // 인증되지 않은 유저 처리
            return ApiResponse.onFailure(null, FailureStatus._UNAUTHORIZED);
        }

        // 액세스 토큰, 사용자 이름, 이메일 추출
        String accessToken = authenticationToken.getCredentials().toString(); // 액세스 토큰
        String userName = authenticationToken.getPrincipal().getName(); // 사용자 이름
        String userEmail = authenticationToken.getPrincipal().getAttribute("email"); // 사용자 이메일

        // 로그인 성공 후 반환할 로그인 응답 생성
        LoginResponse loginResponse = new LoginResponse(accessToken, userName, userEmail);

        return ApiResponse.onSuccess(loginResponse, SuccessStatus._LOGIN_SUCCESS);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // 클라이언트가 토큰을 삭제하도록 유도
        // JWT는 서버에 상태를 저장하지 않기 때문에 토큰이 삭제되면 로그아웃이 처리됨.
        return ApiResponse.onSuccess(null, SuccessStatus._LOGOUT_SUCCESS);
    }
}
