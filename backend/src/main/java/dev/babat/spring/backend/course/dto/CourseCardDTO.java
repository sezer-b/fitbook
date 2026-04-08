package dev.babat.spring.backend.course.dto;

import java.util.UUID;

public record CourseCardDTO(
        UUID id,
        String name,
        String category,
        String providerName,
        String city,
        String country,
        double distanceKm,
        String imageUrl,
        int capacityPerSlot,
        int slotDurationMinutes
) {
}