package com.nexa.cda.authapp.common.api;

import java.time.Instant;

public record ApiErrorResponse(Instant timestamp, String message, String errorCode) {
}
