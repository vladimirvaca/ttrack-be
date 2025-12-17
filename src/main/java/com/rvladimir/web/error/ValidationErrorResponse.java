package com.rvladimir.web.error;

import io.micronaut.serde.annotation.Serdeable;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Serdeable
public class ValidationErrorResponse {
    private String message;
    private List<ValidationItem> errors = new ArrayList<>();

    public ValidationErrorResponse(String message) {
        this.message = message;
    }

    @Data
    @NoArgsConstructor
    @Serdeable
    public static class ValidationItem {
        private String field;
        private String code;
        private String message;

        public ValidationItem(String field, String code, String message) {
            this.field = field;
            this.code = code;
            this.message = message;
        }


    }
}
