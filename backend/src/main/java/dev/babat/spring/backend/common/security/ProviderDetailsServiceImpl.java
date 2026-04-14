package dev.babat.spring.backend.common.security;

import dev.babat.spring.backend.provider.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderDetailsServiceImpl implements UserDetailsService {

    private final ProviderRepository providerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return providerRepository.findByEmail(email)
                .map(user ->
                        new User(user.getId().toString(),
                                user.getPasswordHash() != null ? user.getPasswordHash() : "",
                                List.of(new SimpleGrantedAuthority("ROLE_PROVIDER")))
                )
                .orElseThrow(() -> new UsernameNotFoundException("Provider not found: " + email));
    }
}
