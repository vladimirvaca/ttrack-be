package com.rvladimir.service.dto;

import io.micronaut.serde.annotation.Serdeable;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Serdeable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mobile login response containing JWT tokens and basic user information")
public class MobileLoginResponseDTO {

    @Schema(description = "The JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "The token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "The JWT refresh token used to obtain a new access token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "The authenticated user's unique identifier", example = "42")
    private Long userId;

    @Schema(description = "The authenticated user's email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The authenticated user's first name", example = "John")
    private String name;

    @Schema(description = "The authenticated user's last name", example = "Doe")
    private String lastname;
}

