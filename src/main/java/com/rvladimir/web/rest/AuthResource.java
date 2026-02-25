package com.rvladimir.web.rest;

import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.RefreshTokenRequestDTO;
import com.rvladimir.service.dto.TokenResponseDTO;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.token.cookie.AccessTokenCookieConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Tag(name = "Auth")
@Controller("/auth")
@Requires(bean = AccessTokenCookieConfiguration.class)
@Slf4j
public class AuthResource {

    private final AuthService authService;
    private final AccessTokenCookieConfiguration cookieConfig;

    public AuthResource(AuthService authService, AccessTokenCookieConfiguration cookieConfig) {
        this.authService = authService;
        this.cookieConfig = cookieConfig;
    }

    @ApiResponse(
        responseCode = "204",
        description = "Successful login. JWT is stored in an HttpOnly cookie.",
        headers = @Header(name = "Set-Cookie", description = "Auth cookie with the JWT.")
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials.")
    @Operation(summary = "User Login", description = "Authenticates a user and sets a JWT cookie.")
    @Post(uri = "/login", consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Void> login(@Body @Valid LoginDTO loginDTO) {
        log.info("Login attempt for user: {}", loginDTO.getEmail());

        String token;
        try {
            token = authService.login(loginDTO);
        } catch (Exception ex) {
            log.warn("Login failed for user: {} - {}", loginDTO.getEmail(), ex.getMessage());
            return HttpResponse.unauthorized();
        }

        Cookie authCookie = buildAuthCookie(token);
        log.info("Login successful for user: {}. JWT cookie set.", loginDTO.getEmail());
        return HttpResponse.<Void>noContent().cookie(authCookie);
    }

    @ApiResponse(
        responseCode = "200",
        description = "Successful login. JWT is returned in the response body."
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials.")
    @Operation(
        summary = "Mobile User Login",
        description = "Authenticates a user and returns a JWT access token and a refresh token in the response body."
    )
    @Post(uri = "/mobile-login", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponseDTO> mobileLogin(@Body @Valid LoginDTO loginDTO) {
        log.info("Mobile login attempt for user: {}", loginDTO.getEmail());

        TokenResponseDTO tokenResponse;
        try {
            tokenResponse = authService.mobileLogin(loginDTO);
        } catch (Exception ex) {
            log.warn("Mobile login failed for user: {} - {}", loginDTO.getEmail(), ex.getMessage());
            return HttpResponse.unauthorized();
        }

        log.info("Mobile login successful for user: {}. JWT token returned.", loginDTO.getEmail());
        return HttpResponse.ok(tokenResponse);
    }

    @ApiResponse(
        responseCode = "200",
        description = "Token refreshed successfully. New access and refresh tokens returned."
    )
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or expired refresh token.")
    @Operation(
        summary = "Mobile Token Refresh",
        description = "Validates the provided refresh token and issues a new access token with a rotated refresh token."
    )
    @Post(uri = "/mobile-refresh", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public HttpResponse<TokenResponseDTO> mobileRefresh(@Body @Valid RefreshTokenRequestDTO request) {
        log.info("Token refresh requested.");

        TokenResponseDTO tokenResponse;
        try {
            tokenResponse = authService.refresh(request.getRefreshToken());
        } catch (Exception ex) {
            log.warn("Token refresh failed: {}", ex.getMessage());
            return HttpResponse.unauthorized();
        }

        log.info("Token refresh successful.");
        return HttpResponse.ok(tokenResponse);
    }

    private Cookie buildAuthCookie(String token) {
        Cookie authCookie = Cookie.of(cookieConfig.getCookieName(), token)
            .httpOnly(cookieConfig.isCookieHttpOnly().orElse(true));

        cookieConfig.isCookieSecure().ifPresent(authCookie::secure);
        cookieConfig.getCookieDomain().ifPresent(authCookie::domain);
        cookieConfig.getCookiePath().ifPresent(authCookie::path);
        cookieConfig.getCookieMaxAge().ifPresent(authCookie::maxAge);
        cookieConfig.getCookieSameSite().ifPresent(authCookie::sameSite);

        return authCookie;
    }

}
