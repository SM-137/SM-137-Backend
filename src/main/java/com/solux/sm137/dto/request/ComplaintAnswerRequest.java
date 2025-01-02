package com.solux.sm137.dto.request;

import com.solux.sm137.domain.ComplaintStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComplaintAnswerRequest {
    private ComplaintStatus complaintStatus;
    private String answerContent;
}
