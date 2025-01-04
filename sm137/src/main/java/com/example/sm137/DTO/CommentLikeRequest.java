package com.example.sm137.DTO;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class CommentLikeRequest {

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}

