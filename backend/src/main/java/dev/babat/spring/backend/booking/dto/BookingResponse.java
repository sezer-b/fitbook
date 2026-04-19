package dev.babat.spring.backend.booking.dto;

import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID slotId,
        String status,
        Integer waitlistPosition
) {
}
