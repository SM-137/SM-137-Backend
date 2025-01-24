package com.solux.sm137.service;

import com.solux.sm137.domain.*;
import com.solux.sm137.dto.request.*;
import com.solux.sm137.dto.response.*;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.infra.common.jwt.JwtTokenProvider;
import com.solux.sm137.infra.s3.AmazonS3Manager;
import com.solux.sm137.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {
    private final AttachmentRepository attachmentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final ScrapRepository scrapRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final AmazonS3Manager amazonS3Manager;

    @Transactional
    public void createComplaint(String token, ComplaintRequest complaintRequest, List<MultipartFile> attachments) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 첨부파일 처리
        if (attachments == null) attachments = new ArrayList<>();
        List<String> imageUrls = amazonS3Manager.uploadFiles(attachments);

        // 카테고리 저장
        Category category = Category.builder()
                .categoryName(complaintRequest.getCategoryName())
                .build();
        categoryRepository.save(category);

        // 태그 저장
        Tag tag = Tag.builder()
                .tagName(complaintRequest.getTagName())
                .build();
        tagRepository.save(tag);

        // 민원 생성 및 저장 (먼저 저장)
        Complaint complaint = Complaint.builder()
                .user(user)
                .tag(tag)
                .category(category)
                .title(complaintRequest.getTitle())
                .contentProb(complaintRequest.getContentProb())
                .contentDir(complaintRequest.getContentDir())
                .contentExpect(complaintRequest.getContentExpect())
                .status(ComplaintStatus.WAITING)
                .build();
        complaintRepository.save(complaint); // 먼저 저장하여 ID 생성

        // 첨부파일 엔티티 생성 및 연관 설정
        List<Attachment> attachmentEntities = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            Attachment attachment = Attachment.builder()
                    .fileUrl(imageUrl)
                    .complaint(complaint) // Complaint와 연관 설정
                    .build();
            attachmentRepository.save(attachment);
            attachmentEntities.add(attachment);
        }

        // Complaint에 첨부파일 설정 (양방향 관계일 경우 필요)
        complaint.setAttachments(attachmentEntities);
    }

    @Transactional
    public void updateComplaint(String token, Long complaintId, ComplaintUpdateRequest request) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        // 민원 조회
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        // 민원이 현재 사용자 소유인지 확인
        if (!complaint.getUser().getId().equals(user.getId())) {
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

        // 수정된 민원 저장
        complaintRepository.save(complaint);
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
        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();

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
                complaint.getAttachments().stream().map(Attachment::getFileUrl).toList(),
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

        return new ComplaintAnswerResponse(complaint.getId(), complaint.getAnswer());
    }

    @Transactional(readOnly = true)
    public UserComplaintDetailResponse getUserComplaintDetail(String token, Long complaintId) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new IllegalArgumentException("Invalid Token");
        }
        String email = jwtTokenProvider.getEmailFromToken(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));

        Optional<Complaint> complaintOptional = complaintRepository.findById(complaintId);
        if (complaintOptional.isEmpty()) {
            return null;
        }

        Complaint complaint = complaintOptional.get();
        boolean isLiked = complaintRepository.existsLikeByComplaintAndUser(complaint, user);
        boolean isScrapped = complaintRepository.existsScrapByComplaintAndUser(complaint, user);

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
                complaint.getCreatedAt(),
                isLiked,
                isScrapped,
                complaint.getAttachments().stream().map(Attachment::getFileUrl).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getComplaintCategory(CategoryRequest request) {
        List<Complaint> complaints = complaintRepository.findByCategoryNameOrderByCreatedAtDesc(request.getCategoryName()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        if (complaints.isEmpty()) {
            return null;
        }
        return complaints.stream()
                .map(complaint -> new CategoryResponse(
                        complaint.getId(),
                        complaint.getTag().getTagName(),
                        complaint.getStatus(),
                        complaint.getTitle(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size(),
                        complaint.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GetAllComplaintResponse> getAllComplaints() {
        List<Complaint> complaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        if (complaints.isEmpty()) {
            return null;
        }
        return complaints.stream()
                .map(complaint -> new GetAllComplaintResponse(
                        complaint.getId(),
                        complaint.getTag().getTagName(),
                        complaint.getCategory().getCategoryName(),
                        complaint.getStatus(),
                        complaint.getTitle(),
                        complaint.getContentProb(),
                        complaint.getComplaintLikes().size(),
                        complaint.getScraps().size(),
                        complaint.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<KeywordSearchResponse> getComplaintKeyword(String keyword) {
        List<Complaint> complaints = complaintRepository.findByKeywordOrderByCreatedAtDesc(keyword).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        if (complaints.isEmpty()) {
            return null;
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

