//package com.solux.sm137.infra.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.HttpStatusEntryPoint;
//
//@RequiredArgsConstructor
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // CSRF 설정
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // 세션 관리 설정
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                // 인증 실패 시 처리
//                .exceptionHandling(exceptions -> exceptions
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                )
//
//                // 요청 경로에 따른 인증/권한 설정
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/v1/user/modify").authenticated()
//                        .anyRequest().permitAll()
//                )
//
//                // OAuth2 리소스 서버 설정
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt
//                                .jwkSetUri("https://your-auth-server.com/.well-known/jwks.json")
//                        )
//                );
//        return http.build();
//    }
//
//}