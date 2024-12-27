package com.solux.sm137.infra.apiPayload;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.solux.sm137.infra.apiPayload.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code","message","data"})
public class ApiResponse<T> {

    private final Integer code;
    private final String message;
    private final T data;

    public static <T> ApiResponse<T> onSuccess(T data){
        return new ApiResponse<>(SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), data);
    }

    public static <T> ApiResponse<T> onFailure(Integer code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

}
