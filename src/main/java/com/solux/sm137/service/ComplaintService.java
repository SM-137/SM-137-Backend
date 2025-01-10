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

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AttachmentService attachmentService;
    private final ScrapRepository scrapRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional
    public ComplaintResponse createComplaint(ComplaintRequest complaintRequest, String token) {

        // JWT 토큰에서 사용자 정보 추출
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tag와 Category 객체 조회
        Tag tag = tagRepository.findById(complaintRequest.getTagId())
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        Category category = categoryRepository.findById(complaintRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 민원 생성
        Complaint complaint = new Complaint(
                user,
                tag,
                category,
                complaintRequest.getTitle(),
                complaintRequest.getContentProb(),
                complaintRequest.getContentDir(),
                complaintRequest.getContentExpect(),
                ComplaintStatus.WAITING // 기본값으로 "대기 중" 설정
        );

        // 민원 저장
        Complaint savedComplaint = complaintRepository.save(complaint);

        // 첨부파일 저장
        if (complaintRequest.getAttachments() != null && !complaintRequest.getAttachments().isEmpty()) {
            for (MultipartFile file : complaintRequest.getAttachments()) {
                attachmentService.saveAttachment(savedComplaint, file);
            }
        }


        // 첨부파일 포함한 응답 반환
        List<Attachment> attachmentList = attachmentService.getAttachmentsByComplaint(savedComplaint);
        return new ComplaintResponse(
                savedComplaint.getId(),
                savedComplaint.getTitle(),
                savedComplaint.getContentProb(),
                savedComplaint.getContentDir(),
                savedComplaint.getContentExpect(),
                savedComplaint.getStatus(),
                attachmentList,
                savedComplaint.getUser().getId(),  // userId를 응답에 포함
                savedComplaint.getCategory().getId(),
                savedComplaint.getTag().getId()
        );
    }

    @Transactional
    public void updateComplaint(String token, Long id, ComplaintUpdateRequest request, MultipartFile[] attachments) {
        // JWT 토큰에서 사용자 정보 추출
        String email = jwtTokenProvider.getEmailFromToken(token);
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
        if (!complaint.getStatus().equals(ComplaintStatus.WAITING)) {
            throw new BusinessException(FailureStatus._CONFLICT);
        }

        // 민원 내용 수정
        complaint.setTitle(request.getTitle());
        complaint.setContentProb(request.getContentProb());
        complaint.setContentDir(request.getContentDir());
        complaint.setContentExpect(request.getContentExpect());

        // 기존 첨부파일 삭제
        deleteOldAttachments(complaint);

        // 새로운 첨부파일이 있다면 저장
        if (attachments != null && attachments.length > 0) {
            for (MultipartFile file : attachments) {
                attachmentService.saveAttachment(complaint, file);
            }
        }

        // 수정된 민원 저장
        complaintRepository.save(complaint);
    }

    private void deleteOldAttachments(Complaint complaint) {
        List<Attachment> existingAttachments = complaint.getAttachments();
        for (Attachment attachment : existingAttachments) {
            // 파일 시스템에서 삭제
            attachmentService.deleteAttachment(attachment);
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

