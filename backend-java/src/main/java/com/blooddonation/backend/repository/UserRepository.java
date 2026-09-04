package com.blooddonation.backend.repository;

import com.blooddonation.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByStatusIgnoreCase(String status);

    List<User> findByRoleIgnoreCaseAndStatusIgnoreCase(
            String role,
            String status
    );

    Optional<User> findByEmail(String email);
}