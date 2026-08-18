package com.example.eventbooking.service;

import com.example.eventbooking.dto.response.UserResponse;
import com.example.eventbooking.entity.User;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;

    private UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    private User findUserOrThrow(Long id) {
        log.trace("Entering findUserOrThrow() — id={}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found — id={}", id);
                    return new ResourceNotFoundException("User not found with id " + id);
                });
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        return toDTO(findUserOrThrow(id));
    }

    public void activateUser(Long id) {
        log.trace("Entering activateUser() — id={}", id);
        User user = findUserOrThrow(id);
        user.setActive(true);
        userRepository.save(user);
        log.info("User activated — id={}", id);
    }

    public void deactivateUser(Long id) {
        log.trace("Entering deactivateUser() — id={}", id);
        User user = findUserOrThrow(id);
        user.setActive(false);
        userRepository.save(user);
        log.info("User deactivated — id={}", id);
    }
}