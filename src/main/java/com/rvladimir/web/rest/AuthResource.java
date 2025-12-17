package com.rvladimir.web.rest;

import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.JwtDTO;
import com.rvladimir.service.dto.LoginDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "Auth")
@Controller("/auth")
public class AuthResource {

    private final AuthService authService;

    public AuthResource(AuthService authService) {
        this.authService = authService;
    }

    @ApiResponse(responseCode = "200", description = "Successful login.")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials.")
    @Operation(summary = "User Login", description = "Authenticates a user and returns a login response.")
    @Post(uri = "/login", produces = "application/json", consumes = "application/json")
    public HttpResponse<JwtDTO> login(@Body @Valid LoginDTO loginDTO) {
        JwtDTO jwtDTO = authService.login(loginDTO);
        return HttpResponse.ok().body(jwtDTO);
    }
}
