package com.solux.sm137.infra.common.exception;

public class TokenInvalidFormException extends RuntimeException {
    public TokenInvalidFormException() {
        super("유효하지 않은 JWT 토큰입니다.");
    }
}
