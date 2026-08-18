package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateReviewRequest;
import com.example.eventbooking.dto.response.ReviewResponse;
import com.example.eventbooking.entity.*;
import com.example.eventbooking.exception.ResourceNotFoundException;
import com.example.eventbooking.exception.ReviewNotAllowedException;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.ReviewRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewService {
    private static final Logger log = LogManager.getLogger(ReviewService.class);

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    private ReviewResponse toDTO(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .username(review.getUser().getUsername())
                .build();
    }

    public ReviewResponse createReview(CreateReviewRequest request, Long currentUserId) {
        log.trace("Entering createReview() — eventId={}, userId={}", request.getEventId(), currentUserId);

        Event event = findEventOrThrow(request.getEventId());
        validateReviewEligible(event, currentUserId);

        Review saved = reviewRepository.save(buildReview(event, request, currentUserId));

        log.info("Review created — id={}, eventId={}, userId={}, rating={}",
                saved.getId(), event.getId(), currentUserId, request.getRating());
        return toDTO(saved);
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id " + eventId));
    }

    private void validateReviewEligible(Event event, Long currentUserId) {
        if (event.getEndDateTime().isAfter(LocalDateTime.now())) {
            throw new ReviewNotAllowedException("Cannot review an event that hasn't taken place yet");
        }

        boolean hadConfirmedBooking = bookingRepository.existsByUserIdAndEventIdAndStatus(
                currentUserId, event.getId(), BookingStatus.CONFIRMED);
        if (!hadConfirmedBooking) {
            throw new ReviewNotAllowedException("You must have had a confirmed booking for this event to review it");
        }

        if (reviewRepository.existsByUserIdAndEventId(currentUserId, event.getId())) {
            throw new ReviewNotAllowedException("You have already reviewed this event");
        }
    }

    private Review buildReview(Event event, CreateReviewRequest request, Long currentUserId) {
        User user = new User();
        user.setId(currentUserId);

        Review review = new Review();
        review.setEvent(event);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    public List<ReviewResponse> getReviewsForEvent(Long eventId) {
        return reviewRepository.findByEventId(eventId).stream().map(this::toDTO).collect(Collectors.toList());
    }
}