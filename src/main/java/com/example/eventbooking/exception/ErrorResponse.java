package com.example.eventbooking.exception;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
