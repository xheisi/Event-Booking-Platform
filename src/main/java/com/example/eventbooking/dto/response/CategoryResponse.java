package com.example.eventbooking.dto.response;

import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private boolean active;
}