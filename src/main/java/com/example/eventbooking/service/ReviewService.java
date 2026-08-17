package com.example.eventbooking.service;

import com.example.eventbooking.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReviewService {
    private static final Logger log = LogManager.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
}
