package com.solux.sm137.infra.common.exception;

public class TokenInvalidSecretKeyException extends RuntimeException {
    public TokenInvalidSecretKeyException(String token) {
        super("유효하지 않은 JWT Secret key입니다." + token);
    }
}