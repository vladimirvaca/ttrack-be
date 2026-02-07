package com.rvladimir.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

@MicronautTest
class HealthControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void healthEndpointReturnsOk() {
        HttpRequest<String> request = HttpRequest.GET("/health");
        HttpResponse<String> response = client.toBlocking().exchange(request, String.class);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("{\"status\":\"UP\"}", response.body());
    }
}

