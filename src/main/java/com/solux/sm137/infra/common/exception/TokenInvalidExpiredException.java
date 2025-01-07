package com.solux.sm137.infra.common.exception;

public class TokenInvalidExpiredException extends RuntimeException {
    public TokenInvalidExpiredException() {
        super("만료된 JWT 토큰입니다.");
    }
}