package com.solux.sm137.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private String userEmail;
    private String content;
    private LocalDateTime createdAt;
}
