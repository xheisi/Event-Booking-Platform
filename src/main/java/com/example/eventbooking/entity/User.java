package com.example.eventbooking.entity;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String firstName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToMany(mappedBy="user")
    private List<Event> events;

    @OneToMany(mappedBy="user")
    private List<Booking> bookings;
}
