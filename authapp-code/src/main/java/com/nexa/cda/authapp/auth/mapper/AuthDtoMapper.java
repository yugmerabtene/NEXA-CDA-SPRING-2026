package com.nexa.cda.authapp.auth.mapper;

import com.nexa.cda.authapp.auth.dto.RegisterRequestDto;
import com.nexa.cda.authapp.auth.dto.RegisterResponseDto;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.model.UserRole;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class AuthDtoMapper {

    public AppUser toNewUser(RegisterRequestDto request, String normalizedEmail, String encodedPassword) {
        AppUser user = new AppUser();
        user.setUsername(request.username().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(encodedPassword);
        user.setRole(UserRole.USER);
        user.setCreatedAt(Instant.now());
        return user;
    }

    public RegisterResponseDto toRegisterResponse(AppUser user) {
        return new RegisterResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
