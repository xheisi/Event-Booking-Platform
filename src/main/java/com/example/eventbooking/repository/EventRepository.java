package com.example.eventbooking.repository;

import com.example.eventbooking.entity.Event;
import com.example.eventbooking.entity.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserId(Long userId);

    List<Event> findByStatus(EventStatus status);

    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN e.categories c
        WHERE e.status = com.example.eventbooking.entity.EventStatus.PUBLISHED
        AND (:categoryId IS NULL OR c.id = :categoryId)
        AND (:city IS NULL OR e.venue.city = :city)
        AND (:startDate IS NULL OR e.startDateTime >= :startDate)
        AND (:endDate IS NULL OR e.endDateTime <= :endDate)
        AND (:minPrice IS NULL OR e.price >= :minPrice)
        AND (:maxPrice IS NULL OR e.price <= :maxPrice)
        """)
    Page<Event> searchEvents(
            @Param("categoryId") Long categoryId,
            @Param("city") String city,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    @Query("""
    SELECT e FROM Event e
    WHERE e.venue.id = :venueId
    AND e.status IN :statuses
    AND e.startDateTime < :endDateTime
    AND e.endDateTime > :startDateTime
    """)
    List<Event> findOverlappingEvents(@Param("venueId") Long venueId,
                                      @Param("statuses") List<EventStatus> statuses,
                                      @Param("startDateTime") LocalDateTime startDateTime,
                                      @Param("endDateTime") LocalDateTime endDateTime);
}