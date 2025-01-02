package com.solux.sm137.repository;

import com.solux.sm137.domain.Scrap;
import com.solux.sm137.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Optional<List<Scrap>> findByUser(User user);
    Scrap save(Scrap scrap);

}
