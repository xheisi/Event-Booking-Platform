package com.example.eventbooking.service;

import com.example.eventbooking.entity.User;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void deactivateUser_setsActiveToFalseAndSaves() {
        User user = new User();
        user.setId(1L);
        user.setActive(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deactivateUser(1L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_throwsResourceNotFoundExceptionWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }
}