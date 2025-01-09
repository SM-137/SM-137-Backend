package com.solux.sm137.service;

import com.solux.sm137.domain.*;
import com.solux.sm137.dto.request.*;
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

    // 민원 작성
    @Transactional
    public void createComplaint(String token, ComplaintRequest request) {
        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }

        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        Category category = categoryRepository.findById(request.getCategoryId()) // 적절한 방식으로 ID로 조회
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 카테고리 ID입니다."));
        // 민원 객체 생성
        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .contentProb(request.getContentProb())
                .contentDir(request.getContentDir())
                .contentExpect(request.getContentExpect())
                .status(ComplaintStatus.valueOf(request.getStatus().toUpperCase()))  // 상태 설정
                .category(category)  // 카테고리 설정
                .build();  // 빌더 패턴을 사용하여 Complaint 객체 생성


        // 민원 저장
        complaintRepository.save(complaint);

        // 첨부파일 처리
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            saveAttachments(complaint, request.getAttachments());
        }
    }

    // 첨부파일 저장 처리
    private void saveAttachments(Complaint complaint, List<MultipartFile> attachments) {
        for (MultipartFile file : attachments) {
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            try {
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save attachment", e);
            }

            // 첨부파일 엔티티로 DB 저장 (예시)
            //Attachment attachment = new Attachment(complaint, fileName);
            //attachmentRepository.save(attachment);
        }
    }

    //민원 수정
    @Transactional
    public void updateComplaint(String token, Long id, ComplaintUpdateRequest request) {
        // JWT 토큰 검증
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        // 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 조회
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        // 민원이 현재 사용자 소유인지 확인
        if (!complaint.getUser().getEmail().equals(email)) {
            throw new BusinessException(FailureStatus._UNAUTHORIZED);
        }

        // 민원 상태가 "대기 중"이 아니면 수정 불가
        if (!"WAITING".equals(complaint.getStatus())) {
            throw new BusinessException(FailureStatus._CONFLICT);
        }

        // 민원 내용 수정
        complaint.setTitle(request.getComplaintTitle());
        complaint.setContentProb(request.getContentProb());
        complaint.setContentDir(request.getContentDir());
        complaint.setContentExpect(request.getContentExpect());

        // 첨부파일 존재할 때 업데이트
        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            saveAttachments(request.getAttachments());
        }

        // 수정된 민원 저장
        complaintRepository.save(complaint);
    }

    private void saveAttachments(List<MultipartFile> attachments) {
        for (MultipartFile file : attachments) {
            // 첨부파일 저장 로직 구현
            String fileName = file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            try {
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save attachment", e);
            }
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

