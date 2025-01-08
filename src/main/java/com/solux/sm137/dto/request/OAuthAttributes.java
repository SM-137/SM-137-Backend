package com.solux.sm137.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthAttributes {
    private String department;
    private String number;
    private Map<String, Object> attributes;
    private String name;
    private String email;

    public OAuthAttributes(String name, String email, String department, String number, Map<String, Object> attributes) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.number = number;
        this.attributes = attributes;
    }

    public static OAuthAttributes of(Map<String, Object> attributes) {
        return new OAuthAttributes(
                (String) attributes.get("name"),
                (String) attributes.get("email"),
                (String) attributes.get("department"),
                (String) attributes.get("number"),
                attributes
        );
    }
}