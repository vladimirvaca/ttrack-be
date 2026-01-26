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
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

/**
 * Test class for AuthResource.
 */
@MicronautTest
class AuthResourceTest {

    private static final String ENDPOINT_AUTH_LOGIN = "/auth/login";
    private static final String COOKIE_NAME = "access_token";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TOKEN_VALUE = "jwt-token";

    @Inject
    @Client("/")
    HttpClient client;

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
}
