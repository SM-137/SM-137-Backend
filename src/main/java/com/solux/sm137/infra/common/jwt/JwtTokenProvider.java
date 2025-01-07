package com.solux.sm137.infra.common.jwt;

import com.solux.sm137.infra.common.exception.*;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(@Value("${jwt.secret-key}") final String secretKey,
                            @Value("${jwt.expiration-time}") final long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // JWT 토큰 생성
    public String createToken(final String payload) {
        final Date now = new Date();
        final Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(payload) // payload (예: 사용자 이메일)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘
                .compact();
    }

    // JWT 토큰에서 payload 추출
    public String getPayload(final String token) {
        return tokenToJws(token).getBody().getSubject();
    }

    // 토큰 유효성 검사
    public void validateAbleToken(final String token) {
        try {
            final Jws<Claims> claims = tokenToJws(token);
            validateExpiredToken(claims); // 만료된 토큰 확인
        } catch (final JwtException | InvalidLoginException e) {
            throw new TokenInvalidSecretKeyException(token); // 잘못된 키나 서명
        }
    }

    // JWT 토큰을 JWS로 변환
    private Jws<Claims> tokenToJws(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
        } catch (final IllegalArgumentException | MalformedJwtException e) {
            throw new TokenInvalidFormException(); // 잘못된 토큰 형식
        } catch (final SignatureException e) {
            throw new TokenInvalidSecretKeyException(token); // 잘못된 서명
        } catch (final ExpiredJwtException e) {
            throw new TokenInvalidExpiredException(); // 만료된 토큰
        }
    }

    // 만료된 토큰 확인
    private void validateExpiredToken(final Jws<Claims> claims) {
        if (claims.getBody().getExpiration().before(new Date())) {
            throw new TokenInvalidExpiredException(); // 만료된 토큰 예외
        }
    }
}
