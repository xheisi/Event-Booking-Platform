package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateVenueRequest;
import com.example.eventbooking.dto.response.VenueResponse;
import com.example.eventbooking.service.VenueService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@AllArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }

    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(@PathVariable Long id, @Valid @RequestBody CreateVenueRequest request) {
        return ResponseEntity.ok(venueService.updateVenue(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateVenue(@PathVariable Long id) {
        venueService.deactivateVenue(id);
        return ResponseEntity.noContent().build();
    }
}