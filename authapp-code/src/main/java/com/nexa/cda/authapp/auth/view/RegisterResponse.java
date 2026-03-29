package com.nexa.cda.authapp.auth.view;

import java.time.Instant;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        String role,
        Instant createdAt
) {
}
