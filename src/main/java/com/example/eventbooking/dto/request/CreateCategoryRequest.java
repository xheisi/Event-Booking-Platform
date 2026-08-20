package com.example.eventbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateCategoryRequest {
    @NotBlank
    private String name;
}