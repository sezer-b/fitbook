package dev.babat.spring.backend.auth.service;

import dev.babat.spring.backend.auth.dto.AuthResponse;
import dev.babat.spring.backend.auth.dto.LoginRequest;
import dev.babat.spring.backend.auth.dto.RegisterProviderRequest;
import dev.babat.spring.backend.common.enums.AuthProvider;
import dev.babat.spring.backend.common.security.JWTService;
import dev.babat.spring.backend.provider.entity.ProviderEntity;
import dev.babat.spring.backend.provider.entity.SubscriptionStatus;
import dev.babat.spring.backend.provider.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProviderAuthService {

    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Transactional
    public AuthResponse register(RegisterProviderRequest request) {
        try {
            ProviderEntity provider = new ProviderEntity();
            provider.setEmail(request.email());
            provider.setPasswordHash(passwordEncoder.encode(request.password()));
            provider.setBusinessName(request.businessName());
            provider.setDescription(request.description());
            provider.setPhone(request.phone());
            provider.setWebsite(request.website());
            provider.setAuthProvider(AuthProvider.LOCAL);
            provider.setEmailVerified(false);
            provider.setSubscriptionStatus(SubscriptionStatus.TRIALING);

            ProviderEntity saved = providerRepository.save(provider);

            String token = jwtService.generateToken(saved.getId().toString(), "PROVIDER");
            return new AuthResponse(token, "PROVIDER", saved.getId().toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error registering provider: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        ProviderEntity provider = providerRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), provider.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(provider.getId().toString(), "PROVIDER");
        return new AuthResponse(token, "PROVIDER", provider.getId().toString());
    }
}
