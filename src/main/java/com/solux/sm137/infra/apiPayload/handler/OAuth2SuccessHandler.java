package com.solux.sm137.infra.apiPayload.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String jwtToken = (String) oAuth2User.getAttributes().get("jwtToken");

        response.setHeader("Authorization", "Bearer " + jwtToken);
        // 프론트엔드 리다이렉트 URL 설정
        getRedirectStrategy().sendRedirect(request, response, "/success-url");
    }
}