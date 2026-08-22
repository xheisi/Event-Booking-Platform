package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateEventRequest;
import com.example.eventbooking.entity.*;
import com.example.eventbooking.exception.InvalidEventStateException;
import com.example.eventbooking.exception.UnauthorizedOperationException;
import com.example.eventbooking.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private VenueRepository venueRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private BookingRepository bookingRepository;

    @InjectMocks
    private EventService eventService;

    private Event draftEvent;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setId(1L);

        draftEvent = new Event();
        draftEvent.setId(10L);
        draftEvent.setStatus(EventStatus.DRAFT);
        draftEvent.setUser(owner);
    }

    @Test
    void publishEvent_throwsUnauthorizedOperationExceptionWhenCallerIsNotOwner() {
        when(eventRepository.findById(10L)).thenReturn(Optional.of(draftEvent));

        assertThrows(UnauthorizedOperationException.class, () -> eventService.publishEvent(10L, 999L));
    }

    @Test
    void publishEvent_throwsInvalidEventStateExceptionWhenAlreadyPublished() {
        draftEvent.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(draftEvent));

        assertThrows(InvalidEventStateException.class, () -> eventService.publishEvent(10L, 1L));
    }

    @Test
    void createEvent_throwsInvalidEventStateExceptionWhenVenueInactive() {
        Venue inactiveVenue = new Venue();
        inactiveVenue.setId(5L);
        inactiveVenue.setActive(false);
        when(venueRepository.findById(5L)).thenReturn(Optional.of(inactiveVenue));

        CreateEventRequest request = CreateEventRequest.builder()
                .title("Test Event").venueId(5L).categoryIds(Set.of())
                .build();

        assertThrows(InvalidEventStateException.class, () -> eventService.createEvent(request, 1L));
    }
}