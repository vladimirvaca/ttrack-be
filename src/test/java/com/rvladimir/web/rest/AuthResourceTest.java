package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rvladimir.service.AuthService;
import com.rvladimir.service.dto.LoginDTO;
import com.rvladimir.service.dto.RefreshTokenRequestDTO;
import com.rvladimir.service.dto.TokenResponseDTO;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
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
    private static final String ENDPOINT_AUTH_MOBILE_LOGIN = "/auth/mobile-login";
    private static final String ENDPOINT_AUTH_MOBILE_REFRESH = "/auth/mobile-refresh";
    private static final String COOKIE_NAME = "access_token";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TOKEN_VALUE = "jwt-token";
    private static final String REFRESH_TOKEN_VALUE = "refresh-jwt-token";
    private static final String BEARER_TOKEN_TYPE = "Bearer";

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

    @Test
    void testMobileLoginSuccessReturnsToken() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        TokenResponseDTO tokenResponse = new TokenResponseDTO(TOKEN_VALUE, BEARER_TOKEN_TYPE, REFRESH_TOKEN_VALUE);
        when(authService.mobileLogin(any(LoginDTO.class))).thenReturn(tokenResponse);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> response =
            client.toBlocking().exchange(request, TokenResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getAccessToken()).isEqualTo(TOKEN_VALUE);
        assertThat(response.body().getTokenType()).isEqualTo(BEARER_TOKEN_TYPE);
        assertThat(response.body().getRefreshToken()).isEqualTo(REFRESH_TOKEN_VALUE);
    }

    @Test
    void testMobileLoginDoesNotSetCookie() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        TokenResponseDTO tokenResponse = new TokenResponseDTO(TOKEN_VALUE, BEARER_TOKEN_TYPE, REFRESH_TOKEN_VALUE);
        when(authService.mobileLogin(any(LoginDTO.class))).thenReturn(tokenResponse);

        // When
        HttpRequest<LoginDTO> request = HttpRequest.POST(ENDPOINT_AUTH_MOBILE_LOGIN, loginDTO)
            .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> response =
            client.toBlocking().exchange(request, TokenResponseDTO.class);

        // Then
        assertThat(response.getCookies().get(COOKIE_NAME)).isNull();
    }

    @Test
    void testMobileLoginInvalidCredentialsReturns401() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(authService.mobileLogin(any(LoginDTO.class)))
            .thenThrow(new io.micronaut.http.exceptions.HttpStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid email or password."));

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
    void testMobileLoginValidationErrorEmptyEmailReturns400() {
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
    void testMobileLoginValidationErrorEmptyPasswordReturns400() {
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

    @Test
    void testMobileRefreshSuccessReturnsNewTokenPair() {
        // Given
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(REFRESH_TOKEN_VALUE);
        TokenResponseDTO tokenResponse = new TokenResponseDTO(TOKEN_VALUE, BEARER_TOKEN_TYPE, REFRESH_TOKEN_VALUE);
        when(authService.refresh(REFRESH_TOKEN_VALUE)).thenReturn(tokenResponse);

        // When
        HttpRequest<RefreshTokenRequestDTO> request =
            HttpRequest.POST(ENDPOINT_AUTH_MOBILE_REFRESH, refreshRequest)
                .accept(MediaType.APPLICATION_JSON_TYPE);
        HttpResponse<TokenResponseDTO> response =
            client.toBlocking().exchange(request, TokenResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getAccessToken()).isEqualTo(TOKEN_VALUE);
        assertThat(response.body().getRefreshToken()).isEqualTo(REFRESH_TOKEN_VALUE);
        assertThat(response.body().getTokenType()).isEqualTo(BEARER_TOKEN_TYPE);
    }

    @Test
    void testMobileRefreshInvalidTokenReturns401() {
        // Given
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO(REFRESH_TOKEN_VALUE);
        when(authService.refresh(REFRESH_TOKEN_VALUE))
            .thenThrow(new io.micronaut.http.exceptions.HttpStatusException(
                HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token"));

        // When & Then
        HttpRequest<RefreshTokenRequestDTO> request =
            HttpRequest.POST(ENDPOINT_AUTH_MOBILE_REFRESH, refreshRequest)
                .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testMobileRefreshValidationErrorEmptyTokenReturns400() {
        // Given
        RefreshTokenRequestDTO refreshRequest = new RefreshTokenRequestDTO("");

        // When & Then
        HttpRequest<RefreshTokenRequestDTO> request =
            HttpRequest.POST(ENDPOINT_AUTH_MOBILE_REFRESH, refreshRequest)
                .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TokenResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }
}

