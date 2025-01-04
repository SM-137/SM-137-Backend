package com.example.sm137.repository;

import com.example.sm137.entity.Category; // 올바른 Category 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // @Param import
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.categoryName FROM Category c WHERE c.categoryId = :categoryId")
    List<String> findCategoryNameByCategoryId(@Param("categoryId") Long categoryId);
}
