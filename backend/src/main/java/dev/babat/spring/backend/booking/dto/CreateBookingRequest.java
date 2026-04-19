package dev.babat.spring.backend.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID slotId,
        String guestFirstName,
        String guestLastName,
        @Email String guestEmail,
        String guestPhone
) {
}
