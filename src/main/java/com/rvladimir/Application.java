package com.rvladimir;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@OpenAPIDefinition(
    info = @Info(
        title = "ttrack-be",
        version = "0.3.5",
        description = "Time tracking API",
        contact = @Contact(
            name = "vladimirvaca",
            email = "ramvlay@gmail.com",
            url = "https://rwcoder.com"
        )
    ),
    security = {
        @SecurityRequirement(name = "cookieAuth"),
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecuritySchemes(
    {
        @SecurityScheme(
            name = "cookieAuth",
            type = SecuritySchemeType.APIKEY,
            in = SecuritySchemeIn.COOKIE,
            paramName = "access_token",
            description = "JWT stored in an HttpOnly cookie (web clients)."
        ),
        @SecurityScheme(
            name = "bearerAuth",
            type = SecuritySchemeType.HTTP,
            scheme = "bearer",
            bearerFormat = "JWT",
            description = "JWT Bearer token (mobile clients). Use the token returned by /auth/mobile-login."
        )
    }
)
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
