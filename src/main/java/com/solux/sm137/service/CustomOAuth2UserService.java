package com.solux.sm137.service;

import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.OAuthAttributes;
import com.solux.sm137.dto.response.LoginResponse;
import com.solux.sm137.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpServletResponse response;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        log.info("OAuth2UserRequest: {}", userRequest);
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        // OAuth2User 정보를 로그로 확인
//        if (oAuth2User != null) {
//            log.info("Loaded OAuth2User: {}", oAuth2User.getAttributes());  // OAuth2User의 속성 정보 로그 찍기
//        } else {
//            log.warn("OAuth2User is null.");
//        }
//
//        // OAuth2AuthorizedClientService를 이용하여 클라이언트 등록 정보 가져오기
//        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
//        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
//                authentication.getAuthorizedClientRegistrationId(), authentication.getName()
//        );
//
//        // OAuth2AuthorizedClient에서 ClientRegistration과 AccessToken을 가져오기
//        String registrationId = authorizedClient.getClientRegistration().getRegistrationId();
//        String userNameAttributeName = authorizedClient.getClientRegistration().getProviderDetails()
//                .getUserInfoEndpoint().getUserNameAttributeName();
//
//        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
//
//        User user = saveOrUpdate(attributes, userRequest);
//
//        String accessTokenValue = authorizedClient.getAccessToken().getTokenValue();
//        log.info("Access Token: {}", accessTokenValue);  // 액세스 토큰 출력
//
//        // JWT 생성
//        String jwtToken = generateJwtToken(user);
//
//        // JWT 토큰을 응답 헤더에 설정하여 클라이언트에 전달
//        response.setHeader("Authorization", "Bearer " + jwtToken);
//
//        // OAuth2AuthenticationToken을 SecurityContextHolder에 설정하여 인증 정보를 유지
//        OAuth2AuthenticationToken newAuthentication = new OAuth2AuthenticationToken(
//                oAuth2User,
//                oAuth2User.getAuthorities(),
//                authentication.getAuthorizedClientRegistrationId()
//        );
//
//        // 인증 정보를 SecurityContext에 설정
//        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
//
//        // 사용자 정보 반환
//        return new DefaultOAuth2User(
//                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), // 권한 정보 추가
//                attributes.getAttributes(),
//                attributes.getNameAttributeKey()
//        );
//    }
@Override
public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
    log.warn("!!!!!OAuth2UserRequest: {}", userRequest);
    // OAuth2 인증된 사용자 정보
    OAuthAttributes attributes = OAuthAttributes.ofGoogle(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(), oAuth2User.getAttributes());

    // 사용자 정보 저장 또는 업데이트
    User user = saveOrUpdate(attributes, userRequest);

    // JWT 토큰 생성
    String jwtToken = generateJwtToken(user);

    // JWT 토큰을 SecurityContext에 저장
    SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(oAuth2User, oAuth2User.getAuthorities(), userRequest.getClientRegistration().getRegistrationId()));

    return oAuth2User;  // 사용자 정보를 반환
}


    private User saveOrUpdate(OAuthAttributes attributes, OAuth2UserRequest userRequest) {
        // 이메일로 유저 조회 후 없으면 신규 저장
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> {
                    // 기존 사용자라면 이름만 업데이트
                    entity.update(attributes.getName(),attributes.getEmail(), entity.getNumber(), entity.getDepartment());
                    return entity;
                })
                .orElseGet(() -> {
                    // 새 사용자라면 학번과 학과도 입력받아 저장
                    return createNewUser(attributes, userRequest);
                });

        return userRepository.save(user);
    }

    private User createNewUser(OAuthAttributes attributes, OAuth2UserRequest userRequest) {
        // 프론트엔드에서 받은 학번과 학과를 처리하는 로직
        String number = userRequest.getAdditionalParameters().get("number").toString();  // 학번
        String department = userRequest.getAdditionalParameters().get("department").toString();  // 학과

        // User 엔티티 생성 후 학번과 학과 추가
        User user = attributes.toEntity();
        user.setNumber(number);
        user.setDepartment(department);

        return user;
    }

    // JWT 토큰 생성 메서드
    private String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("role", "ROLE_USER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간 (1일)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
