package com.example.sm137.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "complaint_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintLike {

    @EmbeddedId
    private LikeId id;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LikeId implements Serializable {
        private Long userId;
        private Long complaintId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LikeId likeId = (LikeId) o;
            return userId.equals(likeId.userId) && complaintId.equals(likeId.complaintId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, complaintId);
        }
    }

}
