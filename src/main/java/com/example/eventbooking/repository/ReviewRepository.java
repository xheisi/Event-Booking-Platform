package com.example.eventbooking.repository;

import com.example.eventbooking.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByEventId(Long eventId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId")
    Double findAverageRatingByEventId(@Param("eventId") Long eventId);
}