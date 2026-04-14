package dev.babat.spring.backend.auth.dto;

public record AuthResponse(String token, String role, String id) {
}
