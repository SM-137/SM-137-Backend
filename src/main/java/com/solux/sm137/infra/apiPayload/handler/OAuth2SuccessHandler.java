package com.solux.sm137.infra.apiPayload.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2SuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String name = (String) oAuth2User.getAttributes().get("name");
        String email = (String) oAuth2User.getAttributes().get("email");
        String jwtToken = jwtTokenProvider.createToken(email);

        // 로그에 사용자 정보와 토큰 출력
        log.info("Authentication successful!");
        log.info("User name: {}", name);
        log.info("User email: {}", email);
        log.info("Generated JWT token: {}", jwtToken);

        // 프론트엔드 리다이렉트 URL 설정
        String frontendUrl = "http://localhost:5173/auth/callback";
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            frontendUrl = "https://sm137.netlify.app/auth/callback";
        }
        String redirectUrl = frontendUrl + "?token=" + jwtToken;
        response.sendRedirect(redirectUrl);
    }
}
