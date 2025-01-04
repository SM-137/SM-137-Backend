package com.example.sm137.service;

import com.example.sm137.DTO.ComplaintRequest;
import com.example.sm137.entity.Complaint;
import com.example.sm137.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public Complaint updateComplaint(Long complaintId, ComplaintRequest complaintRequest, Long userId) {
        // 민원 조회
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("해당 민원을 찾을 수 없습니다."));

        // 사용자 검증
        // if (!complaint.getUserId().equals(userId)) {
        //     throw new IllegalAccessException("본인의 글만 수정할 수 있습니다.");
        // }

        // 상태 확인
        if (!"awaiting".equals(complaint.getComplaintStatus())) {
            throw new IllegalArgumentException("대기 상태에서만 수정이 가능합니다.");
        }

        // 민원 정보 수정
        complaint.setCategoryId(complaintRequest.getCategoryId());
        complaint.setComplaintTitle(complaintRequest.getComplaintTitle());
        complaint.setContentProb(complaintRequest.getContentProb());
        complaint.setContentDir(complaintRequest.getContentDir());
        complaint.setContentExpect(complaintRequest.getContentExpect());
        complaint.setUpdatedAt(LocalDateTime.now());

        return complaintRepository.save(complaint);
    }
}
