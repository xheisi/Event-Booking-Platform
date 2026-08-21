package com.example.eventbooking.dto.response;
import lombok.*;

@Builder
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class TokenResponse {
    private String token;
    private String tokenType;
    private UserResponse user;
}
