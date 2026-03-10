package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request payload")
public class LoginDTO {
    @NotNull
    @NotBlank
    @Email
    @Schema(description = "The user's email address", example = "tony.stark@gmail.com")
    private String email;

    @NotNull
    @NotBlank
    @Schema(description = "The user's password", example = "MyS3cretP@ss")
    private String password;
}
