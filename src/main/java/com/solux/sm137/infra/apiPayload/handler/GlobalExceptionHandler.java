package com.solux.sm137.infra.apiPayload.handler;

import com.solux.sm137.infra.apiPayload.base.ApiResponse;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 비즈니스 로직 에러
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse> handleBusinessException(BusinessException e) {
        ApiResponse apiResponse = ApiResponse.onFailure(null, e.getFailureStatus());

        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    // 검증 실패 에러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 실패 응답 생성
        ApiResponse apiResponse = ApiResponse.of(null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(), FailureStatus._BAD_REQUEST.getCode());
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }

    // 나머지 에러
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> handleException(Exception e) {
        ApiResponse apiResponse = ApiResponse.onFailure(null, FailureStatus._INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(apiResponse.getCode()).body(apiResponse);
    }
}
