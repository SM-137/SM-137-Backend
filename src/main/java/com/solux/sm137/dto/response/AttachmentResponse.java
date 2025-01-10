package com.solux.sm137.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentResponse {
    private Long id;
    private String fileName;

    // 생성자
    public AttachmentResponse(Long id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }


}

