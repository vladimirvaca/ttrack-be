package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new user")
public class CreateUserDTO {
    @NotNull
    @NotBlank
    @Schema(description = "The user's first name", example = "Tony")
    private String name;

    @NotNull
    @NotBlank
    @Schema(description = "The user's last name", example = "Stark")
    private String lastname;

    @NotNull
    @NotBlank
    @Schema(description = "The user's unique nickname", example = "IronMan")
    private String nickname;

    @NotNull
    @Schema(description = "The user's date of birth", example = "1991-01-01")
    private LocalDate dateBirth;

    @NotNull
    @NotBlank
    @Email
    @Schema(description = "The user's email address", example = "tony.stark@gmail.com")
    private String email;

    @NotNull
    @NotBlank
    @Schema(description = "The user's password (min 6 characters recommended)", example = "MyS3cretP@ss")
    private String password;
}
