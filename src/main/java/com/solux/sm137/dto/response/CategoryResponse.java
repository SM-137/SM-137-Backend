package com.solux.sm137.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class CategoryResponse {
    private Long complaintId;
    private String tag;
    private ComplaintStatus complaintStatus;
    private String complaintTitle;
    private String contentProb;
    private Integer likeCount;
    private Integer scrapCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
