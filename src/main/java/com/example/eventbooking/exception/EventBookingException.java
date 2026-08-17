package com.example.eventbooking.exception;

public abstract class EventBookingException extends RuntimeException {
    public EventBookingException(String message) {
        super(message);
    }
}
