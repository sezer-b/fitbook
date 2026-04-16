package dev.babat.spring.backend.course.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CourseDetailDTO(
        UUID id,
        String name,
        String description,
        String category,
        String providerName,
        String address,
        String city,
        String postcode,
        String country,
        double distanceKm,
        int capacityPerSlot,
        int slotDurationMinutes,
        List<String> imageUrls,
        Map<LocalDate, List<SlotDTO>> slotsByDate
) {}
