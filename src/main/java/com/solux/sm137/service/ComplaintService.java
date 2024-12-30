package com.solux.sm137.service;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.User;
import com.solux.sm137.dto.response.ComplaintDetailResponse;
import com.solux.sm137.dto.response.ManagerComplaintResponse;
import com.solux.sm137.dto.response.UserInfoResponse;
import com.solux.sm137.repository.ComplaintRepository;
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

    private final ComplaintRepository complaintRepository;

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
}
