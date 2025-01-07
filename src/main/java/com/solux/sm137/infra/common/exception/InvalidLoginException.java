package com.solux.sm137.infra.common.exception;

public class InvalidLoginException extends RuntimeException {
    public InvalidLoginException() {
        super("잘못된 로그인 시도입니다.");
    }
}