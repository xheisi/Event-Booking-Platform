package com.example.eventbooking.controller;

import com.example.eventbooking.dto.VenueDTO;
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
    public ResponseEntity<List<VenueDTO>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getVenueById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getVenueById(id));
    }

    @PostMapping
    public ResponseEntity<VenueDTO> createVenue(@Valid @RequestBody VenueDTO venueDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.createVenue(venueDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueDTO> updateVenue(@PathVariable Long id, @Valid @RequestBody VenueDTO venueDTO) {
        return ResponseEntity.ok(venueService.updateVenue(id, venueDTO));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateVenue(@PathVariable Long id) {
        venueService.deactivateVenue(id);
        return ResponseEntity.noContent().build();
    }
}