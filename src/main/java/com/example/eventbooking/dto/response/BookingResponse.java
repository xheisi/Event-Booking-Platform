package com.example.eventbooking.dto.response;
import com.example.eventbooking.entity.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private int seatsBooked;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
    private String eventTitle;
}
