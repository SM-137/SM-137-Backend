package com.solux.sm137.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;   // 액세스 토큰
    private String userName;      // 사용자 이름
    private String userEmail;     // 사용자 이메일
}
