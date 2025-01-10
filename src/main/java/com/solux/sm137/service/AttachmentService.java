package com.solux.sm137.service;

import com.solux.sm137.domain.Attachment;
import com.solux.sm137.domain.Complaint;
import com.solux.sm137.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentRepository attachmentRepository;

    // 첨부파일 저장 메서드에서 IOException을 throws로 선언
    public Attachment saveAttachment(Complaint complaint, MultipartFile file) throws IOException {
        String uploadPath = "/uploads/" + file.getOriginalFilename();  // 경로 예시
        String modifiedName = file.getOriginalFilename(); // 파일명 예시

        // 첨부파일 저장
        Attachment attachment = new Attachment(complaint, uploadPath, file.getOriginalFilename(), modifiedName);
        return attachmentRepository.save(attachment);
    }

    // 특정 민원의 첨부파일 목록 조회
    public List<Attachment> getAttachmentsByComplaint(Complaint complaint) {
        return attachmentRepository.findByComplaint(complaint);
    }
}
