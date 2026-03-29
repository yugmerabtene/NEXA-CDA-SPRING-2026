package com.nexa.cda.authapp.auth.view;

public record LoginResponse(String token, long expiresInSeconds) {
}
