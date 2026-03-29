package com.nexa.cda.authapp.user.mapper;

import com.nexa.cda.authapp.user.dto.MeResponseDto;
import com.nexa.cda.authapp.user.model.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public MeResponseDto toMeResponse(AppUser user) {
        return new MeResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
