package dev.babat.spring.backend.auth.service;

import dev.babat.spring.backend.auth.dto.AuthResponse;
import dev.babat.spring.backend.auth.dto.LoginRequest;
import dev.babat.spring.backend.auth.dto.RegisterUserRequest;
import dev.babat.spring.backend.common.enums.AuthProvider;
import dev.babat.spring.backend.common.security.JWTService;
import dev.babat.spring.backend.user.entity.UserEntity;
import dev.babat.spring.backend.user.entity.UserStatus;
import dev.babat.spring.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    @Transactional
    public AuthResponse register(RegisterUserRequest request) {
        try {
            UserEntity user = new UserEntity();
            user.setEmail(request.email().toLowerCase());
            user.setPasswordHash(passwordEncoder.encode(request.password()));
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setPhone(request.phone());
            user.setAuthProvider(AuthProvider.LOCAL);
            user.setEmailVerified(false);
            user.setStatus(UserStatus.ACTIVE);

            UserEntity saved = userRepository.save(user);

            String token = jwtService.generateToken(saved.getId().toString(), "USER");
            return new AuthResponse(token, "USER", saved.getId().toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("Error registering user: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        UserEntity user = userRepository.findByEmail(loginRequest.email().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        String token = jwtService.generateToken(user.getId().toString(), "USER");
        return new AuthResponse(token, "USER", user.getId().toString());
    }
}
