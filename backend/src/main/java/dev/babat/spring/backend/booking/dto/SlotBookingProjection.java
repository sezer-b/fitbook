package dev.babat.spring.backend.booking.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface SlotBookingProjection {
    UUID getId();

    String getStatus();

    Integer getWaitlistPosition();

    OffsetDateTime getBookedAt();

    String getGuestFirstName();

    String getGuestLastName();

    String getGuestEmail();

    String getGuestPhone();

    String getUserFirstName();

    String getUserLastName();

    String getUserEmail();
}
