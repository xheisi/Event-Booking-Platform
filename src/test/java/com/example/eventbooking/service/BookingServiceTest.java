package com.example.eventbooking.service;

import com.example.eventbooking.dto.request.CreateBookingRequest;
import com.example.eventbooking.entity.Event;
import com.example.eventbooking.entity.EventStatus;
import com.example.eventbooking.exception.SeatsExceededException;
import com.example.eventbooking.repository.BookingRepository;
import com.example.eventbooking.repository.EventRepository;
import com.example.eventbooking.service.policy.CancellationPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private CancellationPolicy cancellationPolicy;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingService bookingService;

    private Event publishedEventWithOneSeat;

    @BeforeEach
    void setUp() {
        publishedEventWithOneSeat = new Event();
        publishedEventWithOneSeat.setId(1L);
        publishedEventWithOneSeat.setStatus(EventStatus.PUBLISHED);
        publishedEventWithOneSeat.setAvailableSeats(1);
    }

    @Test
    void rejectsBookingWhenRequestedSeatsExceedAvailableSeats() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(publishedEventWithOneSeat));
        when(bookingRepository.existsByUserIdAndEventIdAndStatusIn(anyLong(), anyLong(), anyList()))
                .thenReturn(false);

        CreateBookingRequest request = CreateBookingRequest.builder()
                .eventId(1L)
                .seatsBooked(5)
                .build();

        assertThrows(SeatsExceededException.class, () -> bookingService.createBooking(request, 99L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void confirmedBookingReducesAvailableSeats() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(publishedEventWithOneSeat));
        when(bookingRepository.existsByUserIdAndEventIdAndStatusIn(anyLong(), anyLong(), anyList()))
                .thenReturn(false);
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateBookingRequest request = CreateBookingRequest.builder()
                .eventId(1L)
                .seatsBooked(1)
                .build();

        bookingService.createBooking(request, 99L);

        assertEquals(0, publishedEventWithOneSeat.getAvailableSeats());
        verify(eventRepository).save(publishedEventWithOneSeat);
    }
}