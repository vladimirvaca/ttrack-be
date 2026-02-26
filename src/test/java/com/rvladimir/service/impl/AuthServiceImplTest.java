package com.rvladimir.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.LoginDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.security.token.validator.RefreshTokenValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for AuthServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_EMAIL_WITH_SPACES = "  john.doe@example.com  ";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_WRONG_PASSWORD = "wrongPassword";
    private static final String TOKEN_VALUE = "jwt-token";
    private static final String TEST_NICKNAME = "johnd";
    private static final long USER_ID = 42L;
    private static final int TEST_BIRTH_YEAR = 1990;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private RefreshTokenValidator refreshTokenValidator;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
            USER_ID,
            "John",
            "Doe",
            TEST_NICKNAME,
            java.time.LocalDate.of(TEST_BIRTH_YEAR, 1, 1),
            TEST_EMAIL,
            BCrypt.hashpw(TEST_PASSWORD, BCrypt.gensalt()),
            User.Role.USER
        );
    }

    @Test
    void testLoginSuccess() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(any(Map.class))).thenReturn(Optional.of(TOKEN_VALUE));

        // When
        String token = authService.login(loginDTO);

        // Then
        assertThat(token).isEqualTo(TOKEN_VALUE);

        ArgumentCaptor<Map<String, Object>> claimsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(tokenGenerator).generateToken(claimsCaptor.capture());
        Map<String, Object> claims = claimsCaptor.getValue();
        assertThat(claims.get("sub")).isEqualTo(TEST_EMAIL);
        assertThat(claims.get("iat")).isNotNull();
        assertThat(claims.get("userId")).isEqualTo(USER_ID);
        assertThat(claims.get("roles")).isEqualTo(List.of(User.Role.USER.name()));
    }

    @Test
    void testLoginTrimsEmail() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL_WITH_SPACES, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(any(Map.class))).thenReturn(Optional.of(TOKEN_VALUE));

        // When
        String token = authService.login(loginDTO);

        // Then
        assertThat(token).isEqualTo(TOKEN_VALUE);
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void testLoginBlankEmailRejected() {
        // Given
        LoginDTO loginDTO = new LoginDTO("   ", TEST_PASSWORD);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginDTO))
            .isInstanceOf(HttpStatusException.class)
            .satisfies(ex -> {
                HttpStatusException statusEx = (HttpStatusException) ex;
                assertThat(statusEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
        verify(userRepository, never()).findByEmail(any(String.class));
    }

    @Test
    void testLoginUserNotFound() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginDTO))
            .isInstanceOf(HttpStatusException.class)
            .satisfies(ex -> {
                HttpStatusException statusEx = (HttpStatusException) ex;
                assertThat(statusEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testLoginInvalidPassword() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_WRONG_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> authService.login(loginDTO))
            .isInstanceOf(HttpStatusException.class)
            .satisfies(ex -> {
                HttpStatusException statusEx = (HttpStatusException) ex;
                assertThat(statusEx.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
            });
    }

    @Test
    void testLoginTokenGenerationFailure() {
        // Given
        LoginDTO loginDTO = new LoginDTO(TEST_EMAIL, TEST_PASSWORD);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(tokenGenerator.generateToken(any(Map.class))).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.login(loginDTO))
            .isInstanceOf(HttpStatusException.class)
            .satisfies(ex -> {
                HttpStatusException statusEx = (HttpStatusException) ex;
                assertThat(statusEx.getStatus().getCode())
                    .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode());
            });
    }
}
