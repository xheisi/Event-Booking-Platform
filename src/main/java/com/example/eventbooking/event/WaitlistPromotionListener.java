package com.example.eventbooking.event;

import com.example.eventbooking.entity.Booking;
import com.example.eventbooking.entity.BookingStatus;
import com.example.eventbooking.entity.Event;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class WaitlistPromotionListener {

    private static final Logger log = LogManager.getLogger(WaitlistPromotionListener.class);

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    @EventListener
    public void onBookingCancelled(BookingCancelledEvent event) {
        log.trace("Entering onBookingCancelled() — eventId={}", event.getEventId());

        Optional<Booking> nextInLine = findNextWaitlisted(event.getEventId());
        nextInLine.ifPresent(this::promote);
    }

    private Optional<Booking> findNextWaitlisted(Long eventId) {
        return bookingRepository.findFirstByEventIdAndStatusOrderByBookingDateAsc(eventId, BookingStatus.WAITLISTED);
    }

    private void promote(Booking booking) {
        Event event = eventRepository.findById(booking.getEvent().getId()).orElse(null);
        if (event == null || event.getAvailableSeats() < booking.getSeatsBooked()) {
            log.warn("Cannot promote waitlisted booking id={} — insufficient seats", booking.getId());
            return;
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        event.setAvailableSeats(event.getAvailableSeats() - booking.getSeatsBooked());

        bookingRepository.save(booking);
        eventRepository.save(event);

        log.info("Waitlisted booking promoted to CONFIRMED — bookingId={}, eventId={}", booking.getId(), event.getId());
    }
}