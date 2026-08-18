package com.example.eventbooking.service.policy;

import com.example.eventbooking.entity.Booking;

public interface CancellationPolicy {
    boolean canCancel(Booking booking);
}