package com.example.sm137.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comment_like")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long commentlikeId;

    @Column(nullable = false)
    private long commentId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}
