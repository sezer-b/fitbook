package dev.babat.spring.backend.booking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SlotBookingDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String status,
        Integer waitlistPosition,
        boolean isGuest,
        OffsetDateTime bookedAt
) {
}
