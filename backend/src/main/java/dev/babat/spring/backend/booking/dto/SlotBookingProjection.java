package dev.babat.spring.backend.booking.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SlotBookingProjection {
    UUID getId();

    String getStatus();

    Integer getWaitlistPosition();

    LocalDateTime getBookedAt();

    String getGuestFirstName();

    String getGuestLastName();

    String getGuestEmail();

    String getGuestPhone();

    String getUserFirstName();

    String getUserLastName();

    String getUserEmail();
}
