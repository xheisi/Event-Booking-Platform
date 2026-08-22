package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.RegisterRequest;
import com.example.eventbooking.exception.DuplicateResourceException;
import com.example.eventbooking.repository.UserRepository;
import com.example.eventbooking.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_throwsDuplicateResourceExceptionWhenUsernameTaken() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        RegisterRequest request = RegisterRequest.builder()
                .username("alice").password("Pass1234!").email("alice@test.com").build();

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsDuplicateResourceExceptionWhenEmailTaken() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@test.com")).thenReturn(true);

        RegisterRequest request = RegisterRequest.builder()
                .username("alice").password("Pass1234!").email("alice@test.com").build();

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
    }
}