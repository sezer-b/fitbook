package dev.babat.spring.backend.course.dto;

import dev.babat.spring.backend.course.entity.ScheduleConfig;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CourseManagementDTO(
        UUID id,
        String name,
        String description,
        String category,
        String address,
        String city,
        String postcode,
        String country,
        int capacityPerSlot,
        int slotDurationMinutes,
        ScheduleConfig scheduleConfig,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        List<String> imageUrls,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}