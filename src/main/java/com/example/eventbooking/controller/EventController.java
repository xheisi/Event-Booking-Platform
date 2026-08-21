package com.example.eventbooking.controller;
import com.example.eventbooking.dto.request.CreateEventRequest;
import com.example.eventbooking.dto.response.EventResponse;
import com.example.eventbooking.security.AppUserPrincipal;
import com.example.eventbooking.service.EventService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/my")
    public ResponseEntity<List<EventResponse>> getMyEvents(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(eventService.getMyEvents(principal.user().getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request,
                                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, principal.user().getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id,
                                                     @Valid @RequestBody CreateEventRequest request,
                                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, principal.user().getId()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable Long id,
                                                      @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(eventService.publishEvent(id, principal.user().getId()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable Long id,
                                                     @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(eventService.cancelEvent(id, principal.user().getId()));
    }
}