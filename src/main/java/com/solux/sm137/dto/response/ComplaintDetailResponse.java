package com.solux.sm137.dto.response;

import com.solux.sm137.domain.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ComplaintDetailResponse {
    private Long complaintId;
    private ComplaintStatus complaintStatus;
    private String complaintTitle;
    private String contentProb;
    private String contentDir;
    private String contentExpect;
    private String answer;
    private Integer likeCount;
    private Integer scrapCount;
    private String category;
    private LocalDateTime createdAt;
}
