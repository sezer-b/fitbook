package dev.babat.spring.backend.course.dto;

import dev.babat.spring.backend.course.entity.ScheduleConfig;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateCourseRequest(
        String name,
        String description,
        UUID categoryId,
        String address,
        String city,
        String postcode,
        String country,
        Integer capacityPerSlot,
        Integer slotDurationMinutes,
        ScheduleConfig scheduleConfig,
        LocalDate startDate,
        LocalDate endDate,
        Double lat,
        Double lng
) {}