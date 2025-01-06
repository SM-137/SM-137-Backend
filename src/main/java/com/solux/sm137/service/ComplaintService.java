package com.solux.sm137.service;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.Scrap;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.ComplaintAnswerRequest;
import com.solux.sm137.dto.response.ComplaintAnswerResponse;
import com.solux.sm137.dto.request.CategoryRequest;
import com.solux.sm137.dto.request.ScrapRequest;
import com.solux.sm137.dto.response.CategoryResponse;
import com.solux.sm137.dto.response.UserComplaintDetailResponse;
import com.solux.sm137.dto.response.KeywordSearchResponse;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.ScrapRepository;
import com.solux.sm137.repository.UserRepository;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.dto.response.ManagerComplaintResponse;
import com.solux.sm137.dto.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ComplaintService {
    private final ScrapRepository scrapRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Transactional
    public void scrapComplaint(String token, ScrapRequest request) {

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        Complaint complaint = complaintRepository.findById(request.getComplaintId()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        Scrap scrap = new Scrap(user, complaint);
        scrapRepository.save(scrap);
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

