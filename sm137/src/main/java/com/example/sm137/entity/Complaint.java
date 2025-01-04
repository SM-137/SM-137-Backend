package com.example.sm137.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Complaint")
@Getter
@Setter
@NoArgsConstructor // 기본 생성자를 자동으로 생성한다.
@AllArgsConstructor // 모든 필드를 포함하는 생성자를 자동으로 생성한다.
@ToString // 객체의 문자열 표현을 생성한다.
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Boolean open;

    @Column(nullable = false)
    private String complaintTitle;

    @Column(nullable = false)
    private String contentProb;

    private String contentDir;

    private String contentExpect;

    @Column(nullable = false)
    private String complaintStatus = "awaiting"; // 민원 상태

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt; // 수정 시간

    private String answer;
}
