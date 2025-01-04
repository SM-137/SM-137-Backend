package com.example.sm137.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    /**
     * JWT 토큰 검증
     */
    public boolean verifyToken(String token) {
        try {
            token = token.replace("Bearer ", "");
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        try {
            token = token.replace("Bearer ", "");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject(); // JWT의 subject에 사용자 ID가 저장되어 있다고 가정
        } catch (Exception e) {
            return null; // 예외 발생 시 null 반환
        }
    }
}
