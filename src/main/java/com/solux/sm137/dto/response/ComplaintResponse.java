package com.solux.sm137.dto.response;

import com.solux.sm137.domain.Attachment;
import com.solux.sm137.domain.ComplaintStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ComplaintResponse {

    private ComplaintStatus status;

    // ComplaintResponse 생성자 수정
    public ComplaintResponse(Long id, String title, String contentProb, String contentDir,
                             String contentExpect, ComplaintStatus status, List<Attachment> attachments, Long userId, Long categoryId, Long tagId) {

        this.status = status;
    }

}
