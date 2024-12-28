package com.solux.sm137.repository;

import com.solux.sm137.domain.Complaint;
import com.solux.sm137.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    public Optional<List<Complaint>> findByUser(User user);
}
