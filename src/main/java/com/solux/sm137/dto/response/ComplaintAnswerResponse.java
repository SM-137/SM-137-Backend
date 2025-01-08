package com.solux.sm137.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ComplaintAnswerResponse {
    private String complaintId;
    private String answer;
}