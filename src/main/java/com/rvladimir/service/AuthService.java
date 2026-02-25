package com.rvladimir.service;

import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.TokenResponseDTO;

public interface AuthService {

    String login(LoginDTO loginDTO);

    /**
     * Authenticates a user for mobile clients, returning both an access token and a refresh token.
     *
     * @param loginDTO the login credentials
     * @return a {@link TokenResponseDTO} containing the access token, token type, and refresh token
     */
    TokenResponseDTO mobileLogin(LoginDTO loginDTO);

    /**
     * Validates the given refresh token and returns a new access token along with a rotated refresh token.
     *
     * @param refreshToken the refresh token issued during a previous login or refresh
     * @return a {@link TokenResponseDTO} containing the new access token and a new refresh token
     */
    TokenResponseDTO refresh(String refreshToken);
}
