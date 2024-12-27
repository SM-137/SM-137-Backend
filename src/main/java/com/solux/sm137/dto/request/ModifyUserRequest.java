package com.solux.sm137.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyUserRequest {
    @NotNull(message = "학번 입력은 필수입니다.")
    private String number;

    @NotNull(message = "학과 입력은 필수입니다.")
    private String department;
}
