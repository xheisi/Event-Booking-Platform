package com.example.eventbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    @Column(nullable = false)
    private boolean active = true;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy="user")
    private List<Event> events;

    @OneToMany(mappedBy="user")
    private List<Booking> bookings;

    @OneToMany(mappedBy="user")
    private List<Review> reviews;
}
