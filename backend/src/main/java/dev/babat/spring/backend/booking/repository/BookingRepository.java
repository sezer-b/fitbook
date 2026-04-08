package dev.babat.spring.backend.booking.repository;

import dev.babat.spring.backend.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
}
