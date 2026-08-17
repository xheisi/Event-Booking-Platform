package com.example.eventbooking.exception;

public class UnauthorizedOperationException extends EventBookingException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
