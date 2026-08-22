package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateEventRequest;
import com.example.eventbooking.dto.response.EventResponse;
import com.example.eventbooking.entity.*;
import com.example.eventbooking.exception.*;
import com.example.eventbooking.repository.*;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    private static final Logger log = LogManager.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;

    private EventResponse toDTO(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .price(event.getPrice())
                .totalSeats(event.getTotalSeats())
                .availableSeats(event.getAvailableSeats())
                .status(event.getStatus())
                .venueName(event.getVenue().getName())
                .organizerName(event.getUser().getUsername())
                .categoryName(event.getCategories().stream().map(Category::getName).collect(Collectors.toSet()))
                .categoryIds(event.getCategories().stream().map(Category::getId).collect(Collectors.toSet()))
                .averageRating(reviewRepository.findAverageRatingByEventId(event.getId()))
                .build();
    }

    private Event findEventOrThrow(Long id) {
        log.trace("Entering findEventOrThrow() — id={}", id);
        return eventRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found — id={}", id);
                    return new ResourceNotFoundException("Event not found with id " + id);
                });
    }

    private void checkOwnership(Event event, Long currentUserId) {
        if (!event.getUser().getId().equals(currentUserId)) {
            log.warn("Ownership violation — userId={} attempted to modify eventId={} owned by userId={}",
                    currentUserId, event.getId(), event.getUser().getId());
            throw new UnauthorizedOperationException("You do not own this event");
        }
    }

    private Venue resolveActiveVenue(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id " + venueId));
        if (!venue.isActive()) {
            throw new InvalidEventStateException("Venue with id " + venueId + " is not active");
        }
        return venue;
    }

    private Set<Category> resolveActiveCategories(Set<Long> categoryIds) {
        Set<Category> categories = categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id)))
                .collect(Collectors.toSet());
        categories.forEach(c -> {
            if (!c.isActive()) {
                throw new InvalidEventStateException("Category '" + c.getName() + "' is not active");
            }
        });
        return categories;
    }

    private void applyRequestToEvent(Event event, CreateEventRequest request, Venue venue, Set<Category> categories) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setPrice(request.getPrice());
        event.setTotalSeats(request.getTotalSeats());
        event.setAvailableSeats(request.getTotalSeats());
        event.setVenue(venue);
        event.setCategories(categories);
    }

    public Page<EventResponse> searchEvents(Long categoryId, String city, LocalDateTime startDate,
                                            LocalDateTime endDate, Double minPrice, Double maxPrice,
                                            Pageable pageable) {
        log.trace("Entering searchEvents()");
        return eventRepository.searchEvents(categoryId, city, startDate, endDate, minPrice, maxPrice, pageable)
                .map(this::toDTO);
    }

    public EventResponse getEventDetails(Long id) {
        return toDTO(findEventOrThrow(id));
    }

    public List<EventResponse> getMyEvents(Long organizerId) {
        return eventRepository.findByUserId(organizerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void validateSeatsWithinVenueCapacity(int totalSeats, Venue venue) {
        if (totalSeats > venue.getCapacity()) {
            throw new InvalidEventStateException(
                    "Total seats (" + totalSeats + ") cannot exceed venue capacity (" + venue.getCapacity() + ")");
        }
    }
    private void validateNoVenueOverlap(Venue venue, LocalDateTime start, LocalDateTime end, Long excludeEventId) {
        List<Event> overlapping = eventRepository.findOverlappingEvents(
                venue.getId(), List.of(EventStatus.DRAFT, EventStatus.PUBLISHED), start, end);

        boolean conflict = overlapping.stream()
                .anyMatch(e -> excludeEventId == null || !e.getId().equals(excludeEventId));

        if (conflict) {
            throw new InvalidEventStateException("Venue is already booked for an overlapping time slot");
        }
    }

    private void validateEventTimes(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new InvalidEventStateException("Event end time must be after start time");
        }
    }

    public EventResponse createEvent(CreateEventRequest request, Long organizerId) {
        log.trace("Entering createEvent() — title={}, organizerId={}", request.getTitle(), organizerId);

        Venue venue = resolveActiveVenue(request.getVenueId());
        validateEventTimes(request.getStartDateTime(), request.getEndDateTime());
        validateSeatsWithinVenueCapacity(request.getTotalSeats(), venue);
        validateNoVenueOverlap(venue, request.getStartDateTime(), request.getEndDateTime(), null);
        Set<Category> categories = resolveActiveCategories(request.getCategoryIds());

        User organizer = new User();
        organizer.setId(organizerId);

        Event event = new Event();
        applyRequestToEvent(event, request, venue, categories);
        event.setStatus(EventStatus.DRAFT);
        event.setUser(organizer);

        Event saved = eventRepository.save(event);
        log.info("Event created — id={}, title='{}', organizerId={}", saved.getId(), saved.getTitle(), organizerId);
        return toDTO(saved);
    }

    public EventResponse updateEvent(Long id, CreateEventRequest request, Long currentUserId) {
        log.trace("Entering updateEvent() — id={}", id);
        Event event = findEventOrThrow(id);
        checkOwnership(event, currentUserId);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new InvalidEventStateException("Only DRAFT events can be updated");
        }

        Venue venue = resolveActiveVenue(request.getVenueId());
        validateEventTimes(request.getStartDateTime(), request.getEndDateTime());
        validateSeatsWithinVenueCapacity(request.getTotalSeats(), venue);
        validateNoVenueOverlap(venue, request.getStartDateTime(), request.getEndDateTime(), id);
        Set<Category> categories = resolveActiveCategories(request.getCategoryIds());
        applyRequestToEvent(event, request, venue, categories);

        Event saved = eventRepository.save(event);
        log.info("Event updated — id={}", saved.getId());
        return toDTO(saved);
    }

    public EventResponse publishEvent(Long id, Long currentUserId) {
        log.trace("Entering publishEvent() — id={}", id);
        Event event = findEventOrThrow(id);
        checkOwnership(event, currentUserId);

        if (event.getStatus() != EventStatus.DRAFT) {
            throw new InvalidEventStateException("Only DRAFT events can be published");
        }
        event.setStatus(EventStatus.PUBLISHED);
        Event saved = eventRepository.save(event);
        log.info("Event published — id={}", saved.getId());
        return toDTO(saved);
    }

    public EventResponse cancelEvent(Long id, Long currentUserId) {
        log.trace("Entering cancelEvent() — id={}", id);
        Event event = findEventOrThrow(id);
        checkOwnership(event, currentUserId);

        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new InvalidEventStateException("Event is already " + event.getStatus());
        }
        event.setStatus(EventStatus.CANCELLED);
        Event saved = eventRepository.save(event);

        cancelBookingsForEvent(id);

        log.info("Event cancelled — id={}", saved.getId());
        return toDTO(saved);
    }

    private void cancelBookingsForEvent(Long eventId) {
        List<Booking> activeBookings = bookingRepository.findByEventId(eventId);
        activeBookings.forEach(b -> {
            if (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.WAITLISTED) {
                b.setStatus(BookingStatus.CANCELLED);
            }
        });
        bookingRepository.saveAll(activeBookings);
        log.info("{} bookings cancelled as a result of event cancellation — eventId={}", activeBookings.size(), eventId);
    }
}