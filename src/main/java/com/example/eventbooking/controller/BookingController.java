package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateBookingRequest;
import com.example.eventbooking.dto.response.BookingResponse;
import com.example.eventbooking.entity.BookingStatus;
import com.example.eventbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                         @RequestParam Long currentUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request, currentUserId));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PostMapping("/waitlist")
    public ResponseEntity<BookingResponse> joinWaitlist(@RequestParam Long eventId, @RequestParam Long currentUserId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.joinWaitlist(eventId, currentUserId));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestParam Long currentUserId,
                                                               @RequestParam(required = false) BookingStatus status) {
        return ResponseEntity.ok(bookingService.getMyBookings(currentUserId, status));
    }

    // TEMP: currentUserId param stands in for the authenticated user until JWT security is added
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id, @RequestParam Long currentUserId) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, currentUserId));
    }

    @PatchMapping("/{id}/admin-cancel")
    public ResponseEntity<BookingResponse> adminCancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.adminCancelBooking(id));
    }

    // TEMP: organizerId param stands in for the authenticated user until JWT security is added
    @GetMapping("/organizer")
    public ResponseEntity<List<BookingResponse>> getBookingsForOrganizer(@RequestParam Long organizerId) {
        return ResponseEntity.ok(bookingService.getBookingsForOrganizer(organizerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}