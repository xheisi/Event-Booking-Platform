package com.example.eventbooking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="review")
public class Review {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private double rating;
    private String comment;
    private LocalDateTime createdAt;
}
