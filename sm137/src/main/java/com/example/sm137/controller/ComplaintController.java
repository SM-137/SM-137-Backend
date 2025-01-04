package com.example.sm137.controller;

import com.example.sm137.DTO.ComplaintRequest;
import com.example.sm137.DTO.ComplaintUpdateRequest;
import com.example.sm137.entity.Attachment;
import com.example.sm137.entity.Complaint;
import com.example.sm137.repository.AttachmentRepository;
import com.example.sm137.repository.CategoryRepository;
import com.example.sm137.repository.ComplaintLikeRepository;
import com.example.sm137.repository.ComplaintRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintRepository complaintRepository;
    private final CategoryRepository categoryRepository;
    private final ComplaintLikeRepository complaintLikeRepository;
    private final AttachmentRepository attachmentRepository;

    public ComplaintController(
            ComplaintRepository complaintRepository,
            CategoryRepository categoryRepository,
            ComplaintLikeRepository complaintLikeRepository,
            AttachmentRepository attachmentRepository) {
        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.complaintLikeRepository = complaintLikeRepository;
        this.attachmentRepository = attachmentRepository;
    }

    // 민원 작성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createComplaint(
            @Validated @ModelAttribute ComplaintRequest complaintRequest,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", errorMessage,
                    "data", Collections.emptyMap()
            ));
        }

        try {
            if (complaintRequest.getCategoryId() == null ||
                    complaintRequest.getComplaintTitle() == null ||
                    complaintRequest.getContentProb() == null) {
                throw new IllegalArgumentException("필수 필드가 누락되었습니다.");
            }

            Complaint complaint = new Complaint();
            complaint.setUserId(1L); // 고정된 사용자 ID (추후 인증 처리 필요)
            complaint.setCategoryId(complaintRequest.getCategoryId());
            complaint.setOpen(complaintRequest.getOpen());
            complaint.setComplaintTitle(complaintRequest.getComplaintTitle());
            complaint.setContentProb(complaintRequest.getContentProb());
            complaint.setContentDir(complaintRequest.getContentDir());
            complaint.setContentExpect(complaintRequest.getContentExpect());
            complaint.setCreatedAt(LocalDateTime.now());

            complaintRepository.save(complaint);

            // 첨부 파일 저장
            if (complaintRequest.getAttachments() != null && !complaintRequest.getAttachments().isEmpty()) {
                saveAttachments(complaintRequest.getAttachments(), complaint.getComplaintId());
            }

            List<String> categories = categoryRepository.findCategoryNameByCategoryId(complaint.getCategoryId());
            if (categories == null || categories.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "code", 400,
                        "message", "유효하지 않은 카테고리 ID입니다.",
                        "data", Collections.emptyMap()
                ));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "민원이 신청되었습니다.");
            response.put("data", Map.of(
                    "complaint_id", complaint.getComplaintId(),
                    "status", "awaiting",
                    "category", categories
            ));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", e.getMessage(),
                    "data", Collections.emptyMap()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "code", 500,
                    "message", "민원 작성 중 오류가 발생했습니다.",
                    "data", Collections.emptyMap()
            ));
        }
    }

    // 민원 수정
    @PutMapping("/{complaintId}")
    public ResponseEntity<Map<String, Object>> updateComplaint(
            @PathVariable("complaintId") Long complaintId,
            @Validated @ModelAttribute ComplaintUpdateRequest complaintUpdateRequest,
            BindingResult result) {

        if (result.hasErrors()) {
            String errorMessage = result.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", errorMessage,
                    "data", Collections.emptyMap()
            ));
        }

        try {
            Complaint complaint = complaintRepository.findById(complaintId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 민원을 찾을 수 없습니다."));

            if (!"awaiting".equals(complaint.getComplaintStatus())) {
                return ResponseEntity.status(409).body(Map.of(
                        "code", 409,
                        "message", "대기 상태에서만 수정이 가능합니다.",
                        "data", Collections.emptyMap()
                ));
            }

            complaint.setComplaintTitle(complaintUpdateRequest.getComplaintTitle());
            complaint.setOpen(complaintUpdateRequest.getOpen());
            complaint.setContentProb(complaintUpdateRequest.getContentProb());
            complaint.setContentDir(complaintUpdateRequest.getContentDir());
            complaint.setContentExpect(complaintUpdateRequest.getContentExpect());
            complaint.setUpdatedAt(LocalDateTime.now());

            complaintRepository.save(complaint);

            // 첨부 파일 저장
            if (complaintUpdateRequest.getAttachments() != null && !complaintUpdateRequest.getAttachments().isEmpty()) {
                saveAttachments(complaintUpdateRequest.getAttachments(), complaintId);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "민원이 성공적으로 수정되었습니다.");
            response.put("data", Map.of(
                    "complaint_id", complaint.getComplaintId(),
                    "status", complaint.getComplaintStatus(),
                    "updated_at", complaint.getUpdatedAt()
            ));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "code", 400,
                    "message", e.getMessage(),
                    "data", Collections.emptyMap()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "code", 500,
                    "message", "민원 수정 중 오류가 발생했습니다.",
                    "data", Collections.emptyMap()
            ));
        }
    }

    // 첨부 파일 저장 메서드
    private void saveAttachments(List<MultipartFile> attachments, Long complaintId) throws Exception {
        // 프로젝트 루트 기준 정적 리소스 경로 설정
        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/uploads";
        File uploadDirFile = new File(uploadDir);

        // 경로가 존재하지 않으면 생성
        if (!uploadDirFile.exists()) {
            boolean dirCreated = uploadDirFile.mkdirs();
            if (!dirCreated) {
                throw new Exception("파일 저장 디렉토리를 생성할 수 없습니다: " + uploadDir);
            }
        }

        for (MultipartFile file : attachments) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                String modifiedName = System.currentTimeMillis() + "_" + originalFilename;
                File dest = new File(uploadDirFile, modifiedName);

                // 파일 저장
                file.transferTo(dest);

                // 첨부파일 정보 저장
                Attachment attachment = new Attachment();
                attachment.setComplaintId(complaintId);
                attachment.setUploadPath("/uploads"); // 정적 리소스 경로
                attachment.setFileName(originalFilename);
                attachment.setModifiedName(modifiedName);
                attachment.setCreatedAt(LocalDateTime.now());

                attachmentRepository.save(attachment);
            }
        }
    }
}

