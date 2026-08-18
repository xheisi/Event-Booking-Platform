package com.example.eventbooking.event;

import lombok.Getter;

@Getter
public class BookingCancelledEvent {
    private final Long eventId;

    public BookingCancelledEvent(Long eventId) {
        this.eventId = eventId;
    }
}