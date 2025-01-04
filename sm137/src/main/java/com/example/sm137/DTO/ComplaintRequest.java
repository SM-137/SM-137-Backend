package com.example.sm137.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.stylesheets.LinkStyle;

import java.lang.reflect.Array;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ComplaintRequest {
    //private Long userId;
    private Long categoryId;
    private Boolean open;
    private String complaintTitle;
    private String contentProb;
    private String contentDir;
    private String contentExpect;
    private List<MultipartFile> attachments;
}
