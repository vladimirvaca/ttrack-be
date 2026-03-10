package com.rvladimir.service.dto;

import com.rvladimir.domain.User;

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
@Schema(description = "User response payload")
public class UserDTO {
    @Schema(description = "The user's unique identifier", example = "1",
        accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull
    @NotBlank
    @Schema(description = "The user's first name", example = "John")
    private String name;

    @NotNull
    @NotBlank
    @Schema(description = "The user's last name", example = "Doe")
    private String lastname;

    @NotNull
    @NotBlank
    @Schema(description = "The user's unique nickname", example = "JohnD")
    private String nickname;

    @NotNull
    @Schema(description = "The user's date of birth", example = "1990-01-01")
    private LocalDate dateBirth;

    @NotNull
    @NotBlank
    @Email
    @Schema(description = "The user's email address", example = "john.doe@example.com")
    private String email;

    @NotNull
    @Schema(description = "The user's role in the system", example = "USER")
    private User.Role role;
}
