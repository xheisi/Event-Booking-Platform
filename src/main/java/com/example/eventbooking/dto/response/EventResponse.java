package com.example.eventbooking.dto.response;

import com.example.eventbooking.entity.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double price;
    private int totalSeats;
    private int availableSeats;
    private EventStatus status;
    private String venueName;
    private String organizerName;
    private Set<String> categoryName;
    private Set<Long> categoryIds;
    private Double averageRating;
}
