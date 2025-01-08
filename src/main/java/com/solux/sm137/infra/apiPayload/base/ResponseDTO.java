package com.solux.sm137.infra.apiPayload.base;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResponseDTO {
    private final int code;
    private final String message;
    private final Object data;
}
