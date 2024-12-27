package com.solux.sm137.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UserInfoResponse {
    private String name;
    private String email;
    private String number;
    private String department;

    public UserInfoResponse(String name, String email, String number, String department) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.department = department;
    }
}
