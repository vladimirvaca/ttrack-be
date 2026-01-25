package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.LoginDTO;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for AuthResource.
 */
@MicronautTest
class AuthResourceTest {

    private static final String ENDPOINT_AUTH_LOGIN = "/auth/login";
    private static final String ENDPOINT_AUTH_REFRESH = "/auth/refresh";
    private static final String COOKIE_NAME = "access_token";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TOKEN_VALUE = "jwt-token";
    private static final String REFRESHED_TOKEN_VALUE = "jwt-token-refreshed";
    private static final long USER_ID = 42L;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TokenGenerator tokenGenerator;

    @Inject
    AuthService authService;

    @MockBean(AuthService.class)
    AuthService authService() {
        return mock(AuthService.class);
    }

    @Test
    void testLoginSuccessSetsCookie() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(authService.login(any(LoginDTO.class))).thenReturn(TOKEN_VALUE);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_LOGIN, loginDTO);
        HttpResponse<Void> response = client.toBlocking().exchange(request);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Cookie cookie = response.getCookies().get(COOKIE_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(TOKEN_VALUE);
        assertThat(cookie.isHttpOnly()).isTrue();
    }

    @Test
    void testLoginValidationErrorEmptyEmail() {
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

    @Test
    void testRefreshLoginSuccessSetsCookie() {
        // Given
        String existingToken = createJwtToken(TEST_EMAIL, USER_ID);
        when(authService.refreshLogin(TEST_EMAIL)).thenReturn(REFRESHED_TOKEN_VALUE);

        // When
        HttpRequest<?> request = HttpRequest.POST(ENDPOINT_AUTH_REFRESH, null)
            .cookie(Cookie.of(COOKIE_NAME, existingToken));
        HttpResponse<Void> response = client.toBlocking().exchange(request);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Cookie cookie = response.getCookies().get(COOKIE_NAME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo(REFRESHED_TOKEN_VALUE);
    }

    @Test
    void testRefreshLoginUnauthorizedWithoutCookie() {
        // Given
        HttpRequest<?> request = HttpRequest.POST(ENDPOINT_AUTH_REFRESH, null);

        // When & Then
        assertThatThrownBy(() -> client.toBlocking().exchange(request))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    private String createJwtToken(String email, long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("userId", userId);
        claims.put("roles", List.of("USER"));
        claims.put("iat", Instant.now().getEpochSecond());

        return tokenGenerator.generateToken(claims)
            .orElseThrow(() -> new IllegalStateException("Failed to generate test token"));
    }
}
