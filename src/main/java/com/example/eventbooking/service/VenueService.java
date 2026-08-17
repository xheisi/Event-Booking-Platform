package com.example.eventbooking.service;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.entity.Venue;
import com.example.eventbooking.repository.VenueRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class VenueService {
    private static final Logger log = LogManager.getLogger(VenueService.class);
    private final VenueRepository venueRepository;

    public List<Venue> getAllVenues() {
       return venueRepository.findAll();
    }

    public Venue getVenueById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Venue not found: id={}", id);
                    return new ResourceNotFoundException("Venue not found with id " + id);
                });
    }

    public Venue createVenue(Venue venue) {
        Venue saved = venueRepository.save(venue);
        log.info("Venue created with id: " + saved.getId() + " and name: " + saved.getName());
        return saved;
    }

    public Venue updateVenue(Long id, Venue updatedVenue) {
        Venue existing = getVenueById(id);
        existing.setName(updatedVenue.getName());
        existing.setAddress(updatedVenue.getAddress());
        existing.setCity(updatedVenue.getCity());
        existing.setCapacity(updatedVenue.getCapacity());
        Venue saved = venueRepository.save(existing);
        log.info("Venue updated with id: " + saved.getId() + " and name: " + saved.getName());
        return saved;
    }

    public void deactivateVenue(Long id) {
        Venue venue = getVenueById(id);
        venue.setActive(false);
        venueRepository.save(venue);
        log.info("Venue deactivated: id={}", id);
    }

}