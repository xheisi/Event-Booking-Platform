package com.example.eventbooking.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private double rating;
    private String comment;
    private LocalDateTime createdAt;
    private String username;
}
