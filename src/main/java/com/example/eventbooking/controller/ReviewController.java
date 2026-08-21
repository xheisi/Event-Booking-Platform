package com.example.eventbooking.controller;

import com.example.eventbooking.dto.request.CreateReviewRequest;
import com.example.eventbooking.dto.response.ReviewResponse;
import com.example.eventbooking.security.AppUserPrincipal;
import com.example.eventbooking.service.ReviewService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody CreateReviewRequest request,
                                                       @AuthenticationPrincipal AppUserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(request, principal.user().getId()));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(reviewService.getReviewsForEvent(eventId));
    }
}