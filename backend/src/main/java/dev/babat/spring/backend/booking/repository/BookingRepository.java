package dev.babat.spring.backend.booking.repository;

import dev.babat.spring.backend.booking.entity.BookingEntity;
import dev.babat.spring.backend.booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {

    int countBySlotIdAndStatus(UUID slotId, BookingStatus status);

    List<BookingEntity> findBySlotIdAndStatusOrderByWaitlistPositionAsc(UUID slotId, BookingStatus status);
}
