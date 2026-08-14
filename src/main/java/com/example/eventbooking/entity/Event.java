package com.example.eventbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="event")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    //@Column(nullable = false), @Column(unique = true), @Column(length = 500)
    //@Column(nullable = false, unique = true, length = 100)
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double price;
    private int totalSeats;
    private int availableSeats;
    private String status;


    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToMany(mappedBy="category_id")
    private Set<Category> categories;

    @OneToMany(mappedBy="event")
    private List<Booking> bookings;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;




}
