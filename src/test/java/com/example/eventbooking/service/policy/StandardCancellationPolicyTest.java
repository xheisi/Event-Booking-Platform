package com.example.eventbooking.service.policy;

import com.example.eventbooking.entity.Booking;
import com.example.eventbooking.entity.Event;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardCancellationPolicyTest {

    private final StandardCancellationPolicy policy = new StandardCancellationPolicy();

    @Test
    void allowsCancellationWhenEventIsMoreThan24HoursAway() {
        Event event = new Event();
        event.setStartDateTime(LocalDateTime.now().plusDays(2));

        Booking booking = new Booking();
        booking.setEvent(event);

        assertTrue(policy.canCancel(booking));
    }

    @Test
    void blocksCancellationWhenEventIsWithin24Hours() {
        Event event = new Event();
        event.setStartDateTime(LocalDateTime.now().plusHours(2));

        Booking booking = new Booking();
        booking.setEvent(event);

        assertFalse(policy.canCancel(booking));
    }
}