package com.solux.sm137.service;

import com.solux.sm137.domain.*;
import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ComplaintAnswerRequest;
import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.*;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {
    private final ScrapRepository scrapRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads";  // 파일 업로드 경로 설정

    private static final Logger log = LoggerFactory.getLogger(ComplaintService.class);

    // 민원 작성 메서드
    @Transactional
    public void createComplaint(String token, ComplaintRequest request, MultipartFile[] files) throws IOException {
        try {
            // JWT 토큰 유효성 검사
            if (!jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("Invalid Token");
            }

            // 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // 카테고리 조회
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            // 상태 값 처리
            ComplaintStatus status;
            try {
                status = ComplaintStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid complaint status", e);
            }

            // 민원 객체 생성 및 저장
            Complaint complaint = Complaint.builder()
                    .user(user)
                    .title(request.getTitle())
                    .contentProb(request.getContentProb())
                    .contentDir(request.getContentDir())
                    .contentExpect(request.getContentExpect())
                    .status(status)
                    .category(category)
                    .build();

            complaint = complaintRepository.save(complaint);

            // 첨부파일 처리
            if (files != null && files.length > 0) {
                saveAttachments(files, complaint);  // 파일 저장
            }
        } catch (IllegalArgumentException e) {
            log.error("Argument error: ", e);
            throw new RuntimeException("Validation failed: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("File processing error: ", e);
            throw new RuntimeException("File upload failed", e);
        } catch (Exception e) {
            log.error("Unexpected error: ", e);
            throw new RuntimeException("An unexpected error occurred while creating the complaint", e);
        }
    }

    private void saveAttachments(MultipartFile[] files, Complaint complaint) throws IOException {
        try {
            for (MultipartFile file : files) {
                String originalFileName = file.getOriginalFilename();
                String modifiedFileName = System.currentTimeMillis() + "_" + originalFileName;

                // 파일 저장 경로 지정
                Path path = Paths.get(uploadDir + File.separator + modifiedFileName);
                Files.write(path, file.getBytes());

                // 첨부파일 엔티티 저장
                Attachment attachment = Attachment.builder()
                        .complaint(complaint)
                        .uploadPath(path.toString())  // 파일 경로 저장
                        .fileName(originalFileName)   // 원본 파일명 저장
                        .modifiedName(modifiedFileName)  // 수정된 파일명 저장
                        .build();

                attachmentRepository.save(attachment);  // 첨부파일 정보 DB에 저장
            }
        } catch (IOException e) {
            log.error("File saving error: ", e);
            throw new IOException("Failed to save attachment files", e);
        }
    }





    @Transactional
    public void scrapComplaint(String token, ScrapRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        Complaint complaint = complaintRepository.findById(request.getComplaintId()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        Scrap scrap = new Scrap(user, complaint);
        scrapRepository.save(scrap);
    }

    @Transactional
    public void deleteScrapComplaint(String token, ScrapRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        Complaint complaint = complaintRepository.findById(request.getComplaintId()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        CompositeId compositeId = new CompositeId(user.getId(), complaint.getId());

        Scrap scrap = scrapRepository.findById(compositeId).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        scrapRepository.delete(scrap);
    }

    @Transactional(readOnly = true)
    public List<ManagerComplaintResponse> getComplaintList() {
        // 모든 민원 리스트를 조회
        List<Complaint> complaints = complaintRepository.findAll();

        // 민원 목록을 ManagerComplaintResponse로 변환
        return complaints.stream()
                .map(complaint -> new ManagerComplaintResponse(
                        complaint.getId(),
                        complaint.getTitle(),
                        complaint.getStatus(),
                        complaint.getCreatedAt(),
                        complaint.getCategory().getCategoryName(),
                        complaint.getComplaintLikes().size()
                ))
                .collect(Collectors.toList()); // Stream을 List로 변환
    }

    @Transactional(readOnly = true)
    public ComplaintDetailResponse getComplaintDetail(Long complaintId) {
        // 민원 상세 조회
        Optional<Complaint> complaintOptional = complaintRepository.findById(complaintId);

        if (complaintOptional.isEmpty()) {
            return null;  // 민원 존재하지 않음
        }

        Complaint complaint = complaintOptional.get();

        User user = complaint.getUser();
        UserInfoResponse userInfoResponse = new UserInfoResponse(
                user.getDepartment(),
                user.getNumber(),
                user.getName(),
                user.getEmail()
        );

        return new ComplaintDetailResponse(
                complaint.getId(),
                complaint.getCategory().getCategoryName(),
                complaint.getTitle(),
                complaint.getContentProb(),
                complaint.getContentDir(),
                complaint.getContentExpect(),
                complaint.getStatus().name(),
                complaint.getAnswer(),
                List.of(userInfoResponse)
        );
    }

    @Transactional
    public ComplaintAnswerResponse registerComplaintAnswer(Long complaintId, ComplaintAnswerRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        // 답변 등록
        complaint.setAnswer(request.getAnswerContent());
        complaint.setStatus(request.getComplaintStatus());
        complaintRepository.save(complaint);

        return new ComplaintAnswerResponse(complaint.getId().toString(), complaint.getAnswer());
    }

    @Transactional(readOnly = true)
    public UserComplaintDetailResponse getUserComplaintDetail(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        return new UserComplaintDetailResponse(
                complaint.getId(),
                complaint.getStatus(),
                complaint.getTitle(),
                complaint.getContentProb(),
                complaint.getContentDir(),
                complaint.getContentExpect(),
                complaint.getAnswer(),
                complaint.getComplaintLikes().size(),
                complaint.getScraps().size(),
                complaint.getCategory().getCategoryName(),
                complaint.getTag().getTagName(),
                complaint.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getComplaintCategory(CategoryRequest request) {
        List<Complaint> complaints = complaintRepository.findByCategoryName(request.getCategoryName()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        if (complaints.isEmpty()) {
            throw new BusinessException(FailureStatus._NOT_FOUND);
        }
        return complaints.stream()
                .map(complaint -> new CategoryResponse(
                        complaint.getId(),
                        complaint.getTag().getTagName(),
                        complaint.getStatus(),
                        complaint.getTitle(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KeywordSearchResponse> getComplaintKeyword(String keyword) {
        List<Complaint> complaints = complaintRepository.findByKeyword(keyword).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        if (complaints.isEmpty()) {
            throw new BusinessException(FailureStatus._NOT_FOUND);
        }
        return complaints.stream()
                .map(complaint -> new KeywordSearchResponse(
                        complaint.getId(),
                        complaint.getStatus(),
                        complaint.getTitle(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size(),
                        complaint.getCategory().getCategoryName()))
                .collect(Collectors.toList());
    }
}

