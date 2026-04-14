package dev.babat.spring.backend.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JWTProperties(String secret, long expirationMs) {
}
