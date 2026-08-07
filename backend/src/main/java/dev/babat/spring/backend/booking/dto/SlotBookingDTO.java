package dev.babat.spring.backend.booking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlotBookingDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String status,
        Integer waitlistPosition,
        boolean isGuest,
        LocalDateTime bookedAt
) {
}
