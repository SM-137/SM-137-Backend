package com.solux.sm137.service;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.Scrap;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.request.ComplaintRequest;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.infra.apiPayload.handler.BusinessException;
import com.solux.sm137.infra.apiPayload.status.FailureStatus;
import com.solux.sm137.repository.ComplaintRepository;
import com.solux.sm137.repository.ScrapRepository;
import com.solux.sm137.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ComplaintService {
    private final ScrapRepository scrapRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Transactional
    public void scrapComplaint(String token, ComplaintRequest request ) {

        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token is empty");
        }

        User user = userRepository.findById(1L).orElseThrow(() -> new BusinessException(FailureStatus._USER_NOT_FOUND));
        Complaint complaint = complaintRepository.findById(request.getComplaintId()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));

        Scrap scrap = new Scrap(user, complaint);
        scrapRepository.save(scrap);
    }

    @Transactional
    public ComplaintDetailResponse getComplaintDetail(ComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(request.getComplaintId()).orElseThrow(() -> new BusinessException(FailureStatus._NOT_FOUND));
        return new ComplaintDetailResponse(
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

}
