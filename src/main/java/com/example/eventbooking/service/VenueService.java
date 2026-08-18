package com.example.eventbooking.service;

import com.example.eventbooking.dto.VenueDTO;
import com.example.eventbooking.entity.Venue;
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

    private VenueDTO toDTO(Venue venue) {
        return VenueDTO.builder()
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

    public List<VenueDTO> getAllVenues() {
        return venueRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public VenueDTO getVenueById(Long id) {
        return toDTO(findVenueOrThrow(id));
    }

    public VenueDTO createVenue(VenueDTO venueDTO) {
        log.trace("Entering createVenue() — name={}", venueDTO.getName());
        log.debug("Creating venue with data: {}", venueDTO);

        Venue venue = new Venue();
        venue.setName(venueDTO.getName());
        venue.setAddress(venueDTO.getAddress());
        venue.setCity(venueDTO.getCity());
        venue.setCapacity(venueDTO.getCapacity());
        Venue saved = venueRepository.save(venue);

        log.info("Venue created successfully — id={}, name='{}'", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    public VenueDTO updateVenue(Long id, VenueDTO venueDTO) {
        log.trace("Entering updateVenue() — id={}", id);
        Venue existing = findVenueOrThrow(id);

        log.debug("Updating venue id={} with data: {}", id, venueDTO);
        existing.setName(venueDTO.getName());
        existing.setAddress(venueDTO.getAddress());
        existing.setCity(venueDTO.getCity());
        existing.setCapacity(venueDTO.getCapacity());
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