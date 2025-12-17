package com.rvladimir.web.error;

import io.micronaut.serde.annotation.Serdeable;

import lombok.Getter;

@Getter
@Serdeable
public class ValidationException extends RuntimeException {

    private final String field;
    private final String code;

    public ValidationException(String message, String field, String code) {
        super(message);
        this.field = field;
        this.code = code;
    }
}
