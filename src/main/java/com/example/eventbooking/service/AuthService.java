package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.LoginRequest;
import com.example.eventbooking.dto.request.RegisterRequest;
import com.example.eventbooking.dto.response.TokenResponse;
import com.example.eventbooking.dto.response.UserResponse;
import com.example.eventbooking.entity.User;
import com.example.eventbooking.entity.UserRole;
import com.example.eventbooking.exception.DuplicateResourceException;
import com.example.eventbooking.repository.UserRepository;
import com.example.eventbooking.security.AppUserPrincipal;
import com.example.eventbooking.security.jwt.JwtService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private static final Logger log = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private UserResponse toDTO(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .build();
    }

    public UserResponse register(RegisterRequest request) {
        log.trace("Entering register() — username={}", request.getUsername());
        validateNotTaken(request.getUsername(), request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ATTENDEE);
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("User registered — id={}, username={}", saved.getId(), saved.getUsername());
        return toDTO(saved);
    }

    public UserResponse registerOrganizer(RegisterRequest request) {
        log.trace("Entering registerOrganizer() — username={}", request.getUsername());
        validateNotTaken(request.getUsername(), request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.ORGANIZER);
        user.setActive(true);

        User saved = userRepository.save(user);
        log.info("Organizer registered — id={}, username={}", saved.getId(), saved.getUsername());
        return toDTO(saved);
    }

    private void validateNotTaken(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered");
        }
    }

    public TokenResponse login(LoginRequest request) {
        log.trace("Entering login() — username={}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found — this should not happen"));

        String token = jwtService.generateToken(new AppUserPrincipal(user));
        log.info("User logged in — username={}", user.getUsername());

        return TokenResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(toDTO(user))
                .build();
    }
}