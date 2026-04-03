package com.rvladimir.service;

import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.MobileLoginResponseDTO;
import com.rvladimir.service.dto.TokenResponseDTO;

public interface AuthService {

    String login(LoginDTO loginDTO);

    /**
     * Authenticates a user for mobile clients, returning both tokens and basic user information.
     *
     * @param loginDTO the login credentials
     * @return a {@link MobileLoginResponseDTO} containing the access token, token type, refresh token,
     *         and the authenticated user's id, email, name and lastname
     */
    MobileLoginResponseDTO mobileLogin(LoginDTO loginDTO);

    /**
     * Validates the given refresh token and returns a new access token along with a rotated refresh token.
     *
     * @param refreshToken the refresh token issued during a previous login or refresh
     * @return a {@link TokenResponseDTO} containing the new access token and a new refresh token
     */
    TokenResponseDTO refresh(String refreshToken);
}
