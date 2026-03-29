package com.nexa.cda.authapp.user.dto;

import java.time.Instant;

public record MeResponseDto(Long id, String username, String email, String role, Instant createdAt) {
}
