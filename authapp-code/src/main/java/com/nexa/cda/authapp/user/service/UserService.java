package com.nexa.cda.authapp.user.service;

import com.nexa.cda.authapp.common.exception.UserNotFoundException;
import com.nexa.cda.authapp.user.dao.UserDao;
import com.nexa.cda.authapp.user.dto.MeResponseDto;
import com.nexa.cda.authapp.user.model.AppUser;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public MeResponseDto getCurrentUser(String email) {
        AppUser user = userDao.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return new MeResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
