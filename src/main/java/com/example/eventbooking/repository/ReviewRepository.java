package com.example.eventbooking.repository;

import com.example.eventbooking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Derived — all reviews for an event
    List<Review> findByEventId(Long eventId);

    // Derived — check "at most one review per event" before attempting insert
    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // JPQL — average rating for an event (supports the EventResponse.averageRating field)
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId")
    Double findAverageRatingByEventId(@Param("eventId") Long eventId);
}