package com.solux.sm137.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.solux.sm137.infra.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 기본 생성자는 protected
@AllArgsConstructor
public class Complaint extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonManagedReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    @JsonManagedReference
    private Tag tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contentProb;

    @Column(nullable = false)
    private String contentDir;

    @Column(nullable = false)
    private String contentExpect;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'WAITING'")
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

    // 새로운 생성자 (public으로 수정)
    public Complaint(User user, Tag tag, Category category, String title, String contentProb, String contentDir, String contentExpect, ComplaintStatus status) {
        this.user = user;
        this.tag = tag;
        this.category = category;
        this.title = title;
        this.contentProb = contentProb;
        this.contentDir = contentDir;
        this.contentExpect = contentExpect;
        this.status = status;
    }
}


