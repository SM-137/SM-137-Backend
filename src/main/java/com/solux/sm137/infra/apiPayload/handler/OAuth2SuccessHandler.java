package com.solux.sm137.infra.apiPayload.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String name = (String) oAuth2User.getAttributes().get("name");
        String email = (String) oAuth2User.getAttributes().get("email");
        String jwtToken = (String) oAuth2User.getAttributes().get("jwtToken");

        // JSON 응답 생성
        Map<String, String> jsonResponse = Map.of(
                "token", jwtToken,
                "name", name,
                "email", email
        );

        // 프론트엔드 리다이렉트 URL 설정
        String frontendUrl = "http://localhost:5173/auth/callback";
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            frontendUrl = "https://sm137.netlify.app/auth/callback";
        }
        String redirectUrl = frontendUrl + "?token=" + jwtToken;

        // JSON 응답 전송 및 리다이렉션
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Location", redirectUrl);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302 리디렉션
        new ObjectMapper().writeValue(response.getWriter(), jsonResponse);
    }
}
