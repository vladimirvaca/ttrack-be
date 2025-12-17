package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotNull
    @NotBlank
    @NotEmpty
    @Schema(description = "The user's email", example = "tony.stark@gmail.com")
    private String email;

    @NotNull
    @NotBlank
    @NotEmpty
    @Schema(description = "The user's password", example = "12345")
    private String password;
}
