package com.nexa.cda.authapp.auth.dto;

import java.time.Instant;

public record RegisterResponseDto(
        Long id,
        String username,
        String email,
        String role,
        Instant createdAt
) {
}
