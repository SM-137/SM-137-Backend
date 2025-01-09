package com.solux.sm137.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.solux.sm137.infra.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Complaint extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @JsonBackReference
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    @Column(nullable = false)
    @JsonBackReference
    private String title;

    @Column(nullable = false)
    private String contentProb;

    @Column(nullable = false)
    private String contentDir;

    @Column(nullable = false)
    private String contentExpect;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'WAITING'", nullable = false)
    private ComplaintStatus status;

    private String answer;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<Scrap> scraps;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<ComplaintLike> complaintLikes;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL)
    private List<Attachment> attachments;
}
