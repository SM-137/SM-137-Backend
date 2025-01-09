package com.solux.sm137.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ComplaintUpdateRequest {
    private String complaintTitle;
    private Boolean open;
    private String contentProb;
    private String contentDir;
    private String contentExpect;
    private List<MultipartFile> attachments;
}
