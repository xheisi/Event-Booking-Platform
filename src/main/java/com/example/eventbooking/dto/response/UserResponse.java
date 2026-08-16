package com.example.eventbooking.dto.response;

import com.example.eventbooking.entity.UserRole;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private boolean active;
}
