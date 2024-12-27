package com.solux.sm137.infra.apiPayload.status;

import com.solux.sm137.infra.apiPayload.BaseCode;
import com.solux.sm137.infra.apiPayload.ResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(200, "요청에 성공했습니다."),
    ;

    private final int code;
    private final String message;

    @Override
    public ResponseDTO getResponse() {
        return ResponseDTO.builder()
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ResponseDTO getResponseHttpStatus() {
        return ResponseDTO.builder()
                .code(code)
                .message(message)
                .build();
    }
}
