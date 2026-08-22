package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateVenueRequest;
import com.example.eventbooking.entity.Venue;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void createVenue_savesAndReturnsVenueResponse() {
        CreateVenueRequest request = CreateVenueRequest.builder()
                .name("Riverside Hall").address("12 River St").city("Berlin").capacity(200)
                .build();

        Venue saved = new Venue();
        saved.setId(1L);
        saved.setName("Riverside Hall");
        saved.setActive(true);
        when(venueRepository.save(any(Venue.class))).thenReturn(saved);

        var result = venueService.createVenue(request);

        assertEquals("Riverside Hall", result.getName());
        assertTrue(result.isActive());
        verify(venueRepository).save(any(Venue.class));
    }

    @Test
    void getVenueById_throwsResourceNotFoundExceptionWhenMissing() {
        when(venueRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> venueService.getVenueById(99L));
    }
}