package com.solux.sm137.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solux.sm137.domain.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class ManagerComplaintResponse {
    private Long complaintId;
    private String complaintTitle;
    private ComplaintStatus complaintStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private String category;
    private int likeCount;
}
