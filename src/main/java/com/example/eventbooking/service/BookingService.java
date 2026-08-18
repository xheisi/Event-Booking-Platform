package com.example.eventbooking.service;
import com.example.eventbooking.dto.request.CreateBookingRequest;
import com.example.eventbooking.dto.response.BookingResponse;
import com.example.eventbooking.entity.*;
import com.example.eventbooking.event.BookingCancelledEvent;
import com.example.eventbooking.exception.*;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.service.policy.CancellationPolicy;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingService {
    private static final Logger log = LogManager.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final CancellationPolicy cancellationPolicy;
    private final ApplicationEventPublisher eventPublisher;


    private BookingResponse toDTO(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .seatsBooked(booking.getSeatsBooked())
                .bookingStatus(booking.getStatus())
                .bookingDate(booking.getBookingDate())
                .eventTitle(booking.getEvent().getTitle())
                .build();
    }

    private Booking findBookingOrThrow(Long id) {
        log.trace("Entering findBookingOrThrow() — id={}", id);
        return bookingRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Booking not found — id={}", id);
                    return new ResourceNotFoundException("Booking not found with id " + id);
                });
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
    }

    private void checkOwnership(Booking booking, Long currentUserId) {
        if (!booking.getUser().getId().equals(currentUserId)) {
            log.warn("Ownership violation — userId={} attempted to access bookingId={} owned by userId={}",
                    currentUserId, booking.getId(), booking.getUser().getId());
            throw new UnauthorizedOperationException("You do not own this booking");
        }
    }

    private boolean hasActiveClaim(Long userId, Long eventId) {
        return bookingRepository.existsByUserIdAndEventIdAndStatusIn(
                userId, eventId, Arrays.asList(BookingStatus.CONFIRMED, BookingStatus.WAITLISTED));
    }


    public BookingResponse createBooking(CreateBookingRequest request, Long currentUserId) {
        log.trace("Entering createBooking() — eventId={}, userId={}", request.getEventId(), currentUserId);

        Event event = findEventOrThrow(request.getEventId());
        validateBookable(event, request.getSeatsBooked(), currentUserId);

        event.setAvailableSeats(event.getAvailableSeats() - request.getSeatsBooked());
        eventRepository.save(event);

        Booking saved = bookingRepository.save(
                buildBooking(event, currentUserId, request.getSeatsBooked(), BookingStatus.CONFIRMED));

        log.info("Booking created — id={}, eventId={}, userId={}, seats={}",
                saved.getId(), event.getId(), currentUserId, request.getSeatsBooked());
        return toDTO(saved);
    }

    private void validateBookable(Event event, int seatsRequested, Long currentUserId) {
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new EventNotPublishedException("Event with id " + event.getId() + " is not published");
        }
        if (hasActiveClaim(currentUserId, event.getId())) {
            throw new DuplicateResourceException("You already have an active booking or waitlist entry for this event");
        }
        if (seatsRequested > event.getAvailableSeats()) {
            log.warn("createBooking() rejected — eventId={} requested={} available={}",
                    event.getId(), seatsRequested, event.getAvailableSeats());
            throw new SeatsExceededException("Only " + event.getAvailableSeats() + " seats remaining for this event");
        }
    }

    private Booking buildBooking(Event event, Long currentUserId, int seats, BookingStatus status) {
        User user = new User();
        user.setId(currentUserId);

        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setUser(user);
        booking.setSeatsBooked(seats);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(status);
        return booking;
    }


    public BookingResponse joinWaitlist(Long eventId, Long currentUserId) {
        log.trace("Entering joinWaitlist() — eventId={}, userId={}", eventId, currentUserId);

        Event event = findEventOrThrow(eventId);
        validateWaitlistable(event, currentUserId);

        Booking saved = bookingRepository.save(buildBooking(event, currentUserId, 1, BookingStatus.WAITLISTED));

        log.info("Joined waitlist — bookingId={}, eventId={}, userId={}", saved.getId(), eventId, currentUserId);
        return toDTO(saved);
    }

    private void validateWaitlistable(Event event, Long currentUserId) {
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new EventNotPublishedException("Event with id " + event.getId() + " is not published");
        }
        if (event.getAvailableSeats() > 0) {
            throw new InvalidEventStateException("Event still has available seats — book directly instead of waitlisting");
        }
        if (hasActiveClaim(currentUserId, event.getId())) {
            throw new DuplicateResourceException("You already have an active booking or waitlist entry for this event");
        }
    }


    public BookingResponse cancelBooking(Long bookingId, Long currentUserId) {
        log.trace("Entering cancelBooking() — id={}", bookingId);
        Booking booking = findBookingOrThrow(bookingId);
        checkOwnership(booking, currentUserId);
        return doCancel(booking);
    }

    public BookingResponse adminCancelBooking(Long bookingId) {
        log.trace("Entering adminCancelBooking() — id={}", bookingId);
        return doCancel(findBookingOrThrow(bookingId));
    }

    private BookingResponse doCancel(Booking booking) {
        validateCancellable(booking);

        boolean wasConfirmed = booking.getStatus() == BookingStatus.CONFIRMED;
        booking.setStatus(BookingStatus.CANCELLED);
        Booking saved = bookingRepository.save(booking);

        if (wasConfirmed) {
            releaseSeatAndNotify(booking);
        }

        log.info("Booking cancelled — id={}", saved.getId());
        return toDTO(saved);
    }

    private void validateCancellable(Booking booking) {
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingCancellationException("Booking is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED && !cancellationPolicy.canCancel(booking)) {
            throw new BookingCancellationException("Booking cannot be cancelled this close to the event start time");
        }
    }

    private void releaseSeatAndNotify(Booking booking) {
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getSeatsBooked());
        eventRepository.save(event);
        eventPublisher.publishEvent(new BookingCancelledEvent(event.getId()));
    }


    public List<BookingResponse> getMyBookings(Long userId, BookingStatus status) {
        List<Booking> bookings = (status != null)
                ? bookingRepository.findByUserIdAndStatus(userId, status)
                : bookingRepository.findByUserId(userId);
        return bookings.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BookingResponse> getBookingsForOrganizer(Long organizerId) {
        return bookingRepository.findByEventOrganizerId(organizerId).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
}