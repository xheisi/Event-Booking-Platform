package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateEventRequest;
import com.example.eventbooking.dto.response.EventResponse;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventResponse>> searchEvents(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        return ResponseEntity.ok(eventService.searchEvents(categoryId, city, startDate, endDate, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetails(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventDetails(id));
    }

    // TEMP: organizerId param stands in for the authenticated user until JWT security is added
    @GetMapping("/my")
    public ResponseEntity<List<EventResponse>> getMyEvents(@RequestParam Long organizerId) {
        return ResponseEntity.ok(eventService.getMyEvents(organizerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    // TEMP: organizerId param stands in for the authenticated user until JWT security is added
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                     @RequestParam Long organizerId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, organizerId));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                     @Valid @RequestBody CreateEventRequest request,
                                                     @RequestParam Long currentUserId) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, currentUserId));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id, @RequestParam Long currentUserId) {
        return ResponseEntity.ok(eventService.publishEvent(id, currentUserId));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable Long id, @RequestParam Long currentUserId) {
        return ResponseEntity.ok(eventService.cancelEvent(id, currentUserId));
    }
}