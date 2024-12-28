package com.solux.sm137.infra.apiPayload.handler;

import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final FailureStatus failureStatus;

    public BusinessException(FailureStatus failureStatus) {
        super(failureStatus.getMessage());
        this.failureStatus = failureStatus;
    }
}