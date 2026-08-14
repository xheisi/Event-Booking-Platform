package com.example.eventbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="booking")
public class Booking {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;
    private int seatsBooked;
    private String status;
    private LocalDate bookingDate;

    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
