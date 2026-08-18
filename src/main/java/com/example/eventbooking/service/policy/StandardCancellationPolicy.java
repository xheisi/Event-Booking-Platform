package com.example.eventbooking.service.policy;

import com.example.eventbooking.entity.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StandardCancellationPolicy implements CancellationPolicy {

    private static final int CANCELLATION_WINDOW_HOURS = 24;

    @Override
    public boolean canCancel(Booking booking) {
        LocalDateTime deadline = booking.getEvent().getStartDateTime().minusHours(CANCELLATION_WINDOW_HOURS);
        return LocalDateTime.now().isBefore(deadline);
    }
}