package com.example.eventbooking.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="category")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public int id;

    public String name;

    @ManyToMany
    @JoinTable(
            name="event_id",
            joinColumns=@JoinColumn(name="category_id"),
            inverseJoinColumns=@JoinColumn(name="event_id")
    )
    private Set<Event> events;
}
