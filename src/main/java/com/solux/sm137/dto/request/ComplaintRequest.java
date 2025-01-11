package com.solux.sm137.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solux.sm137.domain.ComplaintStatus;
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

public class ComplaintRequest {
    private String title;
    private String contentProb;
    private String contentDir;
    private String contentExpect;
    private String categoryName;
    private String tagName;
}
