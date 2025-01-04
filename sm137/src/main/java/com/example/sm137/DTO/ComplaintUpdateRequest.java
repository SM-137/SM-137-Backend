package com.example.sm137.DTO;

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
    private String complaintTitle; // 민원의 제목
    private Boolean open; // 공개 여부
    private String contentProb; // 문제 내용
    private String contentDir; // 개선 방향
    private String contentExpect;
    private List<MultipartFile> attachments;
}
