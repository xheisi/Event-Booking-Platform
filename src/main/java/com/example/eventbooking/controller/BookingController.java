package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateBookingRequest;
import com.example.eventbooking.dto.response.BookingResponse;
import com.example.eventbooking.entity.BookingStatus;
import com.example.eventbooking.security.AppUserPrincipal;
import com.example.eventbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@AllArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request, principal.user().getId()));
    }

    @PostMapping("/waitlist")
    public ResponseEntity<BookingResponse> joinWaitlist(@RequestParam Long eventId,
                                                        @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.joinWaitlist(eventId, principal.user().getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal AppUserPrincipal principal,
                                                               @RequestParam(required = false) BookingStatus status) {
        return ResponseEntity.ok(bookingService.getMyBookings(principal.user().getId(), status));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable Long id,
                                                         @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, principal.user().getId()));
    }

    @PatchMapping("/{id}/admin-cancel")
    public ResponseEntity<BookingResponse> adminCancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.adminCancelBooking(id));
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<BookingResponse>> getBookingsForOrganizer(@AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.ok(bookingService.getBookingsForOrganizer(principal.user().getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}