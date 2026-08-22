package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateVenueRequest;
import com.example.eventbooking.dto.response.VenueResponse;
import com.example.eventbooking.entity.Venue;
import com.example.eventbooking.exception.DuplicateResourceException;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.VenueRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VenueService {
    private static final Logger log = LogManager.getLogger(VenueService.class);
    private final VenueRepository venueRepository;

    private VenueResponse toDTO(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .city(venue.getCity())
                .capacity(venue.getCapacity())
                .active(venue.isActive())
                .build();
    }

    private Venue findVenueOrThrow(Long id) {
        log.trace("Entering findVenueOrThrow() — id={}", id);
        return venueRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Venue not found — id={}", id);
                    return new ResourceNotFoundException("Venue not found with id " + id);
                });
    }

    public List<VenueResponse> getAllVenues() {
        return venueRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public VenueResponse getVenueById(Long id) {
        return toDTO(findVenueOrThrow(id));
    }

    public VenueResponse createVenue(CreateVenueRequest request) {
        log.trace("Entering createVenue() — name={}", request.getName());
        log.debug("Creating venue with data: {}", request);

        if (venueRepository.existsByName(request.getName())) {
            log.warn("Duplicate venue name rejected — name={}", request.getName());
            throw new DuplicateResourceException("Venue '" + request.getName() + "' already exists");
        }

        Venue venue = new Venue();
        venue.setName(request.getName());
        venue.setAddress(request.getAddress());
        venue.setCity(request.getCity());
        venue.setCapacity(request.getCapacity());
        Venue saved = venueRepository.save(venue);

        log.info("Venue created successfully — id={}, name='{}'", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    public VenueResponse updateVenue(Long id, CreateVenueRequest request) {
        log.trace("Entering updateVenue() — id={}", id);
        Venue existing = findVenueOrThrow(id);

        log.debug("Updating venue id={} with data: {}", id, request);
        existing.setName(request.getName());
        existing.setAddress(request.getAddress());
        existing.setCity(request.getCity());
        existing.setCapacity(request.getCapacity());
        Venue saved = venueRepository.save(existing);

        log.info("Venue updated successfully — id={}", saved.getId());
        return toDTO(saved);
    }

    public void deactivateVenue(Long id) {
        log.trace("Entering deactivateVenue() — id={}", id);
        Venue venue = findVenueOrThrow(id);
        venue.setActive(false);
        venueRepository.save(venue);
        log.info("Venue deactivated — id={}", id);
    }
}