package dev.babat.spring.backend.course.dto;

import java.util.UUID;

public interface CourseDetailProjection {
    UUID getId();

    String getName();

    String getDescription();

    String getAddress();

    String getCity();

    String getPostcode();

    String getCountry();

    String getCategory();

    String getProviderName();

    double getDistanceKm();

    int getCapacityPerSlot();

    int getSlotDurationMinutes();
}