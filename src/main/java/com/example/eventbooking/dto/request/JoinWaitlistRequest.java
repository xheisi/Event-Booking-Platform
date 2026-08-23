package com.example.eventbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class JoinWaitlistRequest {
    @NotNull
    @Min(1)
    private Integer seatsBooked;
}