package com.example.eventbooking.repository;

import com.example.eventbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    boolean existsByName(String name);
}