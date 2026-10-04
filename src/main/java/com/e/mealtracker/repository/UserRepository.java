package com.e.mealtracker.repository;


import com.e.mealtracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findByUsername(String username, Pageable pageable);


    // 🆕 Soft delete — все юзеры, удалённые до указанной даты
    List<User> findAllByDeletedAtBefore(LocalDateTime threshold);
}
