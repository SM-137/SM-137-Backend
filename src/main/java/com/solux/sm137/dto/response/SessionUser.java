package com.solux.sm137.dto.response;

import com.solux.sm137.domain.User;
import lombok.Getter;
import java.io.Serializable;


@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }
}