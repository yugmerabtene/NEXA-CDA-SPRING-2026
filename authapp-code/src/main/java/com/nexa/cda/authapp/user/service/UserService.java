package com.nexa.cda.authapp.user.service;

import com.nexa.cda.authapp.common.exception.UserNotFoundException;
import com.nexa.cda.authapp.user.dao.UserDao;
import com.nexa.cda.authapp.user.dto.MeResponseDto;
import com.nexa.cda.authapp.user.mapper.UserDtoMapper;
import com.nexa.cda.authapp.user.model.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserDao userDao;
    private final UserDtoMapper userDtoMapper;

    public UserService(UserDao userDao, UserDtoMapper userDtoMapper) {
        this.userDao = userDao;
        this.userDtoMapper = userDtoMapper;
    }

    @Transactional(readOnly = true)
    public MeResponseDto getCurrentUser(String email) {
        AppUser user = userDao.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return userDtoMapper.toMeResponse(user);
    }
}
