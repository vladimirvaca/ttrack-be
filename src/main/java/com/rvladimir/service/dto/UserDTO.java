package com.rvladimir.service.dto;

import com.rvladimir.domain.User;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Schema(description = "The user ID", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "The user name", example = "John")
    private String name;

    @NotNull
    @Schema(description = "The user lastname", example = "Doe")
    private String lastname;

    @NotNull
    @Schema(description = "The user nickname", example = "JohnD")
    private String nickname;

    @NotNull
    @Schema(description = "The user date of birth", example = "1990-01-01")
    private LocalDate dateBirth;

    @NotNull
    @Email
    @Schema(description = "The user email", example = "john.doe@example.com")
    private String email;

    @NotNull
    @Schema(description = "The user role", example = "USER")
    private User.Role role;
}
