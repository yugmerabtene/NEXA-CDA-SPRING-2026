package com.nexa.cda.authapp.auth.dto;

public record LoginResponseDto(String token, long expiresInSeconds) {
}
