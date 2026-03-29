package com.nexa.cda.authapp.auth.mapper;

import com.nexa.cda.authapp.auth.view.RegisterRequest;
import com.nexa.cda.authapp.auth.view.RegisterResponse;
import com.nexa.cda.authapp.user.model.AppUser;
import com.nexa.cda.authapp.user.model.UserRole;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class AuthViewMapper {

    public AppUser toNewUser(RegisterRequest request, String normalizedEmail, String encodedPassword) {
        AppUser user = new AppUser();
        user.setUsername(request.username().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(encodedPassword);
        user.setRole(UserRole.USER);
        user.setCreatedAt(Instant.now());
        return user;
    }

    public RegisterResponse toRegisterResponse(AppUser user) {
        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
