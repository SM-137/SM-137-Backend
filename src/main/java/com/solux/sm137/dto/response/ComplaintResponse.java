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

    private Long id;
    private Long userId;
    private String title;
    private String contentProb;
    private String contentDir;
    private String contentExpect;
    private ComplaintStatus status;
    private List<Attachment> attachments;
    private Long categoryId;
    private Long tagId;

    // ComplaintResponse 생성자 수정
    public ComplaintResponse(Long id, String title, String contentProb, String contentDir,
                             String contentExpect, ComplaintStatus status, List<Attachment> attachments, Long userId, Long categoryId, Long tagId) {
        this.id = id;
        this.title = title;
        this.contentProb = contentProb;
        this.contentDir = contentDir;
        this.contentExpect = contentExpect;
        this.status = status;
        this.attachments = attachments;
        this.userId = userId;
        this.categoryId = categoryId;
        this.tagId = tagId;
    }

}
