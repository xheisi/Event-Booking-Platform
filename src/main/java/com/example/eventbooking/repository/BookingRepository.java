package com.example.eventbooking.repository;

import com.example.eventbooking.entity.Booking;
import com.example.eventbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Derived — attendee's own bookings, optionally filtered by status
    List<Booking> findByUserId(Long userId);
    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    // Derived — oldest waitlisted booking for an event (for promotion on cancellation)
    Optional<Booking> findFirstByEventIdAndStatusOrderByBookingDateAsc(Long eventId, BookingStatus status);

    // JPQL — bookings across all events owned by a given organizer
    @Query("SELECT b FROM Booking b WHERE b.event.user.id = :organizerId")
    List<Booking> findByEventOrganizerId(@Param("organizerId") Long organizerId);

    // Native — reporting-style aggregate: top 5 most-booked events
    @Query(value = """
        SELECT e.id, e.title, SUM(b.seats_booked) AS total_booked
        FROM booking b
        JOIN event e ON b.event_id = e.id
        WHERE b.status = 'CONFIRMED'
        GROUP BY e.id, e.title
        ORDER BY total_booked DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5MostBookedEvents();
}