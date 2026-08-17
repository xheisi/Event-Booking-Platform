package com.example.eventbooking.service;

import com.example.eventbooking.repository.BookingRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookingService {
    private static final Logger log = LogManager.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
}
