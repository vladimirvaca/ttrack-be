package com.rvladimir.service.dto;

import com.rvladimir.domain.User;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    @NotNull
    @NotEmpty
    @Schema(description = "The user's name", example = "Tony")
    private String name;

    @NotNull
    @NotEmpty
    @Schema(description = "The user's lastname", example = "Stark")
    private String lastname;

    @NotNull
    @NotEmpty
    @Schema(description = "The user's date of birth", example = "1991-01-029")
    private LocalDate dateBirth;

    @NotNull
    @Email
    @NotEmpty
    @Schema(description = "The user's email", example = "tony.stark@gmail.com")
    private String email;

    @NotNull
    @NotEmpty
    @Schema(description = "The user's password", example = "12345")
    private String password;

    @NotNull
    @NotEmpty
    @Schema(description = "The user's password", example = "ADMIN")
    private User.Role role;
}
