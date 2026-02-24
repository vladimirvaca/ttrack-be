package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.TokenResponseDTO;
import com.rvladimir.test.PostgresTestContainer;
import com.rvladimir.test.TestDataFactory;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for AuthResource.
 * Verifies the browser login (cookie) and mobile login (JWT body) flows against a real database.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthResourceE2eTest implements TestPropertyProvider {

    private static final String ENDPOINT_AUTH_LOGIN = "/auth/login";
    private static final String ENDPOINT_AUTH_MOBILE_LOGIN = "/auth/mobile-login";
    private static final String COOKIE_NAME = "access_token";
    private static final String BEARER_TOKEN_TYPE = "Bearer";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_WRONG_PASSWORD = "wrongPassword";

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    UserRepository userRepository;

    @Override
    public Map<String, String> getProperties() {
        return Map.of(
            "datasources.default.url", postgres.getJdbcUrl(),
            "datasources.default.username", postgres.getUsername(),
            "datasources.default.password", postgres.getPassword(),
            "datasources.default.driverClassName", postgres.getDriverClassName()
        );
    }

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    // --- Browser login (cookie) ---

    @Test
    void testLoginEndToEndSuccessSetsHttpOnlyCookie() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_LOGIN, loginDTO);
        HttpResponse<Void> response = client.toBlocking().exchange(request);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Cookie cookie = response.getCookies().get(COOKIE_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotBlank();
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void testLoginEndToEndInvalidPasswordReturns401() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_WRONG_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_LOGIN, loginDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testLoginEndToEndUserNotFoundReturns401() {
        // Given - no user saved
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_LOGIN, loginDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testLoginEndToEndEmptyEmailReturns400() {
        // Given
        LoginDTO loginDTO = new LoginDTO("", TEST_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_LOGIN, loginDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    // --- Mobile login (JWT body) ---

    @Test
    void testMobileLoginEndToEndSuccessReturnsToken() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> response =
            client.toBlocking().exchange(request, TokenResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getAccessToken()).isNotBlank();
        assertThat(response.body().getTokenType()).isEqualTo(BEARER_TOKEN_TYPE);
    }

    @Test
    void testMobileLoginEndToEndDoesNotSetCookie() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> response =
            client.toBlocking().exchange(request, TokenResponseDTO.class);

        // Then
        assertThat(response.getCookies().get(COOKIE_NAME)).isNull();
    }

    @Test
    void testMobileLoginEndToEndTokenIsUsableForAuthenticatedRequests() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When - obtain token
        HttpRequest<LoginDTO> loginRequest = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> loginResponse =
            client.toBlocking().exchange(loginRequest, TokenResponseDTO.class);

        assertThat(loginResponse.body()).isNotNull();
        String token = loginResponse.body().getAccessToken();

        // Then - use Bearer token on a protected endpoint
        MutableHttpRequest<Object> protectedRequest = HttpRequest.GET("/exercise")
            .bearerAuth(token);
        HttpResponse<Object> protectedResponse =
            client.toBlocking().exchange(protectedRequest, Object.class);
        assertThat(protectedResponse.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
    }

    @Test
    void testMobileLoginEndToEndInvalidPasswordReturns401() {
        // Given
        userRepository.save(TestDataFactory.createUserWithPassword(TEST_EMAIL, TEST_PASSWORD));
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_WRONG_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testMobileLoginEndToEndUserNotFoundReturns401() {
        // Given - no user saved
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testMobileLoginEndToEndEmptyEmailReturns400() {
        // Given
        LoginDTO loginDTO = new LoginDTO("", TEST_PASSWORD);

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testMobileLoginEndToEndEmptyPasswordReturns400() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, "");

        // When & Then
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }
}

