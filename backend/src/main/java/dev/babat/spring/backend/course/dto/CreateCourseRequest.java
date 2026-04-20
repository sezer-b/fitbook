package dev.babat.spring.backend.course.dto;

import dev.babat.spring.backend.course.entity.ScheduleConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank String name,
        String description,
        @NotNull UUID categoryId,
        @NotBlank String address,
        @NotBlank String city,
        String postcode,
        @NotBlank String country,
        @NotNull Integer capacityPerSlot,
        @NotNull Integer slotDurationMinutes,
        @NotNull ScheduleConfig scheduleConfig,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @NotNull Double lat,
        @NotNull Double lng
) {
}