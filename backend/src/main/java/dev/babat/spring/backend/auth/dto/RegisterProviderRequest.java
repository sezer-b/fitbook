package dev.babat.spring.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterProviderRequest(
        @Email @NotBlank String email,
        @Size(min = 8, max = 100) @NotBlank String password,
        @NotBlank String businessName,
        String description,
        String phone,
        String website) {
}
