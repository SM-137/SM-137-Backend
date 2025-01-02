package com.solux.sm137.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ComplaintDetailResponse {
    private Long complaintId;
    private String category;
    private String complaintTitle;
    private String complaintProb;
    private String complaintDir;
    private String complaintExpect;
    private String complaintStatus;
    private String answerContent;
    private List<UserInfoResponse> user;
}
