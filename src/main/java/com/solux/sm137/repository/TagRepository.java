package com.solux.sm137.repository;

import com.solux.sm137.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface TagRepository extends JpaRepository<Tag, Long> {

}
