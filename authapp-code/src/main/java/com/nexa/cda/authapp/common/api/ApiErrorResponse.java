package com.nexa.cda.authapp.common.api;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        Instant timestamp,
        String message,
        String errorCode,
        List<FieldValidationError> errors
) {
}
