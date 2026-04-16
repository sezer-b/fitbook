package dev.babat.spring.backend.course.dto;

import dev.babat.spring.backend.course.entity.CourseSlotStatus;

import java.time.LocalTime;
import java.util.UUID;

public record SlotDTO(
        UUID id,
        LocalTime startTime,
        LocalTime endTime,
        int availableSpots,
        CourseSlotStatus status
) {
}
