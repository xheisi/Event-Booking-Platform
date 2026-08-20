package com.example.eventbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateVenueRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotNull
    @Min(1)
    private Integer capacity;
}