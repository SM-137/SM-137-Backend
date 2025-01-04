package com.example.sm137.repository;

import com.example.sm137.entity.ComplaintLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintLikeRepository extends JpaRepository<ComplaintLike, ComplaintLike.LikeId> {
}
