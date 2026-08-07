package dev.babat.spring.backend.course.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record CourseSlotDTO(
        UUID id,
        LocalDate slotDate,
        LocalTime startTime,
        LocalTime endTime,
        String status
) {
}
