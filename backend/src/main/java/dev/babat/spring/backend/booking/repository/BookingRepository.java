package dev.babat.spring.backend.booking.repository;

import dev.babat.spring.backend.booking.dto.SlotBookingProjection;
import dev.babat.spring.backend.booking.entity.BookingEntity;
import dev.babat.spring.backend.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    int countBySlotIdAndStatus(UUID slotId, BookingStatus status);

    List<BookingEntity> findBySlotIdAndStatusOrderByWaitlistPositionAsc(UUID slotId, BookingStatus status);

    List<BookingEntity> findByUserIdOrderByBookedAtDesc(UUID userId);

    @Query(value = """
        SELECT b.id, b.status, b.waitlist_position, b.booked_at,
               b.guest_first_name, b.guest_last_name, b.guest_email, b.guest_phone,
               u.first_name AS user_first_name, u.last_name AS user_last_name, u.email AS user_email
        FROM bookings b
        LEFT JOIN users u ON u.id = b.user_id
        WHERE b.slot_id = :slotId
        AND b.status != 'CANCELLED'
        ORDER BY b.status ASC, b.waitlist_position ASC, b.booked_at ASC
        """, nativeQuery = true)
    List<SlotBookingProjection> findBookingsForSlot(@Param("slotId") UUID slotId);
}
