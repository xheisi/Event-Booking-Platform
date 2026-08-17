package com.example.eventbooking.exception;

public class SeatsExceededException extends EventBookingException {
    public SeatsExceededException(String message) {
        super(message);
    }
}
