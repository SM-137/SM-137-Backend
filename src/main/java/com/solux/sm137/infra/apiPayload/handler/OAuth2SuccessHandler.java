package com.solux.sm137.infra.apiPayload.handler;

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

        // JWT를 쿠키로 설정
        Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);  // HTTPS에서만 전송, 로컬에서는 false로 설정 가능
        jwtCookie.setPath("/");    // 애플리케이션 전체에서 사용 가능
        jwtCookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(jwtCookie);

        // 프론트엔드 리다이렉트 URL 설정
        String frontendUrl = "http://localhost:5173/auth/callback";
        if (frontendUrl == null || frontendUrl.isEmpty()) {
            frontendUrl = "https://sm137.netlify.app/auth/callback";
        }
        response.sendRedirect(frontendUrl);
    }
}
