package com.example.eventbooking.service;

import com.example.eventbooking.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventService {
    private static final Logger log = LogManager.getLogger(EventService.class);
    private final EventRepository eventRepository;
}
