package dev.babat.spring.backend.course.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface SlotProjection {
    UUID getId();

    LocalDate getSlotDate();

    LocalTime getStartTime();

    LocalTime getEndTime();

    String getStatus();

    int getConfirmedBookings();
}
