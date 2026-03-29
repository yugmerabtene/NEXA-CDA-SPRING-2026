package com.nexa.cda.authapp.common.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String email) {
        super("User not found", HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }
}
