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
@Schema(description = "JWT token response for mobile authentication")
public class TokenResponseDTO {

    @Schema(description = "The JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "The token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "The JWT refresh token used to obtain a new access token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    /**
     * Constructor for backward compatibility (access token + token type, no refresh token).
     *
     * @param accessToken the JWT access token
     * @param tokenType   the token type (e.g. "Bearer")
     */
    public TokenResponseDTO(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.refreshToken = null;
    }
}

