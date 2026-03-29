package com.nexa.cda.authapp.auth.view;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 3, max = 100, message = "username must contain between 3 and 100 characters")
        String username,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 150, message = "email too long")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password must contain between 8 and 72 characters")
        String password
) {
}
