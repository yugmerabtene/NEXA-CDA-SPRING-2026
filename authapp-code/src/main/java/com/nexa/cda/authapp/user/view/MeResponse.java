package com.nexa.cda.authapp.user.view;

import java.time.Instant;

public record MeResponse(Long id, String username, String email, String role, Instant createdAt) {
}
