package com.example.sm137.repository;

import com.example.sm137.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByComplaintId(Long complaintId); // 민원 ID로 첨부파일 검색
}
