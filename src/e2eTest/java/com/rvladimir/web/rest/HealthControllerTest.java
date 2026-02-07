package com.rvladimir.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rvladimir.test.PostgresTestContainer;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.util.Map;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@MicronautTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthControllerTest implements TestPropertyProvider {

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driverClassName", postgres.getDriverClassName()
        );
    }

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

