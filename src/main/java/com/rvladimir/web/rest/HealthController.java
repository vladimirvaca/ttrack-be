package com.rvladimir.web.rest;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

@Controller("/health")
@Secured(SecurityRule.IS_ANONYMOUS)
public class HealthController {

    @Get
    public HttpResponse<String> healthCheck() {
        return HttpResponse.ok("OK");
    }
}

