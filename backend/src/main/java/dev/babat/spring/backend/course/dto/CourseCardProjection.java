package dev.babat.spring.backend.course.dto;

import java.util.UUID;

public interface CourseCardProjection {
    UUID getId();

    String getName();

    String getCity();

    String getCountry();

    String getCategory();

    String getProviderName();

    double getDistanceKm();

    String getImageUrl();

    int getCapacityPerSlot();

    int getSlotDurationMinutes();
}