package com.solux.sm137.service;

import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.OAuthAttributes;
import com.solux.sm137.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2AccessToken accessToken = userRequest.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        String userInfoEndpointUri = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUri();

        if (!StringUtils.hasText(userInfoEndpointUri)) {
            throw new OAuth2AuthenticationException("User Info Endpoint URI is missing");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
        Map<String, Object> userAttributes = response.getBody();

        if (userAttributes == null) {
            throw new OAuth2AuthenticationException("Failed to retrieve user attributes");
        }

        // OAuthAttributes 객체 생성
        OAuthAttributes attributes = OAuthAttributes.of(userAttributes);

        // 사용자 저장 또는 업데이트
        User user = saveOrUpdate(attributes, userRequest);

        // 로그인 성공 후 토큰 생성
        String jwtToken = generateJwtToken(user);

        // JWT 토큰을 속성에 설정하여 반환
        userAttributes.put("jwtToken", jwtToken);
        userAttributes.put("user", Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "department", user.getDepartment(),
                "number", user.getNumber()
        ));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                userAttributes,
                "name" // 기본 식별자 키
        );
    }


    private User saveOrUpdate(OAuthAttributes attributes, OAuth2UserRequest userRequest) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    entity.update(attributes.getName(), attributes.getEmail(), entity.getNumber(), entity.getDepartment());
                    return entity;
                })
                .orElseGet(() -> createNewUser(attributes, userRequest));

        return userRepository.save(user);
    }

    private User createNewUser(OAuthAttributes attributes, OAuth2UserRequest userRequest) {
        Map<String, Object> additionalInfo = attributes.getAttributes();

        // 필요한 값 추출 및 null 처리
        String name = attributes.getName();
        String email = attributes.getEmail();
        String department = additionalInfo.get("department") != null ? additionalInfo.get("department").toString() : "Unknown";
        String number = additionalInfo.get("number") != null ? additionalInfo.get("number").toString() : "Unknown";

        // 새 사용자 생성
        return User.builder()
                .name(name)
                .email(email)
                .department(department)
                .number(number)
                .build();
    }

    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
