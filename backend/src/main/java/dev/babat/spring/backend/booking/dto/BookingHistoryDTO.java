package dev.babat.spring.backend.booking.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingHistoryDTO(
        UUID id,
        UUID slotId,
        String courseName,
        String providerName,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        String status,
        Integer waitlistPosition,
        OffsetDateTime bookedAt
) {
}