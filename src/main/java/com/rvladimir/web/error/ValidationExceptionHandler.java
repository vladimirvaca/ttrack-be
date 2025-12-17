package com.rvladimir.web.error;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import jakarta.inject.Singleton;

@Singleton
@Produces
public class ValidationExceptionHandler
    implements ExceptionHandler<ValidationException, HttpResponse<ValidationErrorResponse>> {

    @Override
    public HttpResponse<ValidationErrorResponse> handle(HttpRequest request, ValidationException exception) {
        ValidationErrorResponse body = new ValidationErrorResponse("Validation failed");
        if (exception.getField() != null || exception.getCode() != null || exception.getMessage() != null) {
            ValidationErrorResponse.ValidationItem item =
                new ValidationErrorResponse.ValidationItem(
                    exception.getField(),
                    exception.getCode(),
                    exception.getMessage()
                );
            body.getErrors().add(item);
        }
        return HttpResponse.badRequest(body);
    }
}
