package dev.babat.spring.backend.auth.controller;

import dev.babat.spring.backend.auth.dto.AuthResponse;
import dev.babat.spring.backend.auth.dto.LoginRequest;
import dev.babat.spring.backend.auth.dto.RegisterProviderRequest;
import dev.babat.spring.backend.auth.dto.RegisterUserRequest;
import dev.babat.spring.backend.auth.service.ProviderAuthService;
import dev.babat.spring.backend.auth.service.UserAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserAuthService userAuthService;
    private final ProviderAuthService providerAuthService;

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return userAuthService.register(request);
    }

    @PostMapping("/users/login")
    public AuthResponse loginUser(@Valid @RequestBody LoginRequest request) {
        return userAuthService.login(request);
    }

    @PostMapping("/providers/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registerProvider(@Valid @RequestBody RegisterProviderRequest request) {
        return providerAuthService.register(request);
    }

    @PostMapping("/providers/login")
    public AuthResponse loginProvider(@Valid @RequestBody LoginRequest request) {
        return providerAuthService.login(request);
    }
}