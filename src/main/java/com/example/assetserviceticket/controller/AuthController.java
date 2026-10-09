package com.example.assetserviceticket.controller;

import com.example.assetserviceticket.entity.User;
import com.example.assetserviceticket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken());
    }

    @GetMapping("/me")
    public Map<String, Object> currentUser(Authentication authentication) {
        User user = userRepository.findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Oturum kullanıcısı bulunamadı."));
        return Map.of("id", user.getId(), "fullName", user.getFullName(), "email", user.getEmail(),
                "role", user.getRole().name(), "authorities", authentication.getAuthorities().stream()
                        .map(authority -> authority.getAuthority()).toList());
    }
}
