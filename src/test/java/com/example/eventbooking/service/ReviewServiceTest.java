package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateReviewRequest;
import com.example.eventbooking.entity.BookingStatus;
import com.example.eventbooking.entity.Event;
import com.example.eventbooking.exception.ReviewNotAllowedException;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private EventRepository eventRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void createReview_throwsReviewNotAllowedExceptionWhenEventHasNotHappenedYet() {
        Event futureEvent = new Event();
        futureEvent.setId(1L);
        futureEvent.setEndDateTime(LocalDateTime.now().plusDays(3));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(futureEvent));

        CreateReviewRequest request = CreateReviewRequest.builder()
                .eventId(1L).rating(4.0).comment("Great!").build();

        assertThrows(ReviewNotAllowedException.class, () -> reviewService.createReview(request, 1L));
    }

    @Test
    void createReview_throwsReviewNotAllowedExceptionWhenNoConfirmedBooking() {
        Event pastEvent = new Event();
        pastEvent.setId(1L);
        pastEvent.setEndDateTime(LocalDateTime.now().minusDays(1));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(pastEvent));
        when(bookingRepository.existsByUserIdAndEventIdAndStatus(1L, 1L, BookingStatus.CONFIRMED))
                .thenReturn(false);

        CreateReviewRequest request = CreateReviewRequest.builder()
                .eventId(1L).rating(4.0).comment("Great!").build();

        assertThrows(ReviewNotAllowedException.class, () -> reviewService.createReview(request, 1L));
    }
}