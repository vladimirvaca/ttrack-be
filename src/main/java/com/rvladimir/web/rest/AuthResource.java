package com.rvladimir.web.rest;

import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.LoginDTO;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpResponse;
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

@Tag(name = "Auth")
@Controller("/auth")
@Requires(bean = AccessTokenCookieConfiguration.class)
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
    @Post(uri = "/login", consumes = "application/json")
    public HttpResponse<Void> login(@Body @Valid LoginDTO loginDTO) {
        String token = authService.login(loginDTO);
        Cookie authCookie = Cookie.of(cookieConfig.getCookieName(), token)
            .httpOnly(cookieConfig.isCookieHttpOnly().orElse(true));

        cookieConfig.isCookieSecure().ifPresent(authCookie::secure);
        cookieConfig.getCookieDomain().ifPresent(authCookie::domain);
        cookieConfig.getCookiePath().ifPresent(authCookie::path);
        cookieConfig.getCookieMaxAge().ifPresent(authCookie::maxAge);
        cookieConfig.getCookieSameSite().ifPresent(authCookie::sameSite);

        return HttpResponse.<Void>noContent().cookie(authCookie);
    }
}
