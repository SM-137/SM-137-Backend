package com.example.sm137.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long complaintId;

    @Column(nullable = false, length = 200)
    private String commentContent;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
