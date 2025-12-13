package com.rvladimir;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
    info = @Info(
        title = "ttrack-be",
        version = "1.0",
        description = "Time tracking API",
        contact = @Contact(
            name = "rvladimir",
            email = "ramvlay@gmail.com",
            url = "https://rvladimir.com"
        )
    )
)
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
