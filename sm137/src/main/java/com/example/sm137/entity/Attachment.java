package com.example.sm137.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @Column(name = "complaint_id", nullable = false)
    private Long complaintId;

    @Column(name = "upload_path", nullable = false)
    private String uploadPath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "modified_name", nullable = false)
    private String modifiedName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
