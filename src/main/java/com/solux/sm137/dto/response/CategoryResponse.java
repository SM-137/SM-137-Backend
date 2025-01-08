package com.solux.sm137.dto.response;

import com.solux.sm137.domain.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long complaintId;
    private String tag;
    private ComplaintStatus complaintStatus;
    private String complaintTitle;
    private String contentProb;
    private Integer likeCount;
    private Integer scrapCount;
}
