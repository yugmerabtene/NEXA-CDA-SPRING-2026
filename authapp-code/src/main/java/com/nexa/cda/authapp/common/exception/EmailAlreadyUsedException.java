package com.nexa.cda.authapp.common.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyUsedException extends BusinessException {

    public EmailAlreadyUsedException(String email) {
        super("Email already used: " + email, HttpStatus.CONFLICT, ErrorCode.EMAIL_ALREADY_USED);
    }
}
