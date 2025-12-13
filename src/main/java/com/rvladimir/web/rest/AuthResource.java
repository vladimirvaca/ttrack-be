package com.rvladimir.web.rest;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth")
@Controller("/auth")
public final class AuthResource {

    @Get(produces = "application/json")
    public String index()  {
        return "{\"message\": \"Hello World\"}";
    }
}
