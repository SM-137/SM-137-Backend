package com.solux.sm137.repository;

import com.solux.sm137.domain.Scrap;
import com.solux.sm137.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<List<Scrap>> findByUser(User user);
}
