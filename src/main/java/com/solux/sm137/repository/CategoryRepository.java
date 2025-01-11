package com.solux.sm137.repository;

import com.solux.sm137.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 부모 카테고리 정보로 조회하는 쿼리
    @Query("SELECT c.categoryName FROM Category c WHERE c.id = :categoryId")
    List<String> findCategoryNameByCategoryId(@Param("categoryId") Long categoryId);
}
