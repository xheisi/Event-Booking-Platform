package com.example.eventbooking.service;

import com.example.eventbooking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger log = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository;
}
