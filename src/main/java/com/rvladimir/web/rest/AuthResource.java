package com.rvladimir.web.rest;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth")
@Controller("/auth")
public class AuthResource {

    @Get(produces = "application/json")
    public String index() {
        return "{\"message\": \"Hello World\"}";
    }

    @Operation(summary = "User Login", description = "Authenticates a user and returns a login response")
    @ApiResponse(responseCode = "200", description = "Successful login")
    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    @Post(uri = "/login", produces = "application/json")
    public String login() {
        return "{\"message\": \"Login endpoint\"}";
    }
}
