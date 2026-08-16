package com.example.eventbooking.repository;

import com.example.eventbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Derived — needed for login lookup (Spring Security will use this)
    Optional<User> findByUsername(String username);

    List<User> findByActive(boolean active);

    // Derived — registration duplicate checks
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}