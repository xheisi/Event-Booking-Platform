package com.example.eventbooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VenueDTO {
    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @Min(1)
    private int capacity;
}
