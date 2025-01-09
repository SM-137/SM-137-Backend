package com.solux.sm137.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ComplaintLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    public ComplaintLike(User user, Complaint complaint) {
        this.user = user;
        this.complaint = complaint;
    }
}
