package com.example.eventbooking.config;

import com.example.eventbooking.entity.User;
import com.example.eventbooking.entity.UserRole;
import com.example.eventbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LogManager.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.username:admin}")
    private String seedUsername;

    @Value("${admin.seed.password:}")
    private String seedPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(seedUsername)) {
            return;
        }
        if (seedPassword == null || seedPassword.isBlank()) {
            log.warn("No admin.seed.password set — skipping admin seeding");
            return;
        }

        User admin = new User();
        admin.setUsername(seedUsername);
        admin.setEmail(seedUsername + "@eventbooking.local");
        admin.setPassword(passwordEncoder.encode(seedPassword));
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);
        log.info("Seeded initial admin account — username={}", seedUsername);
    }
}