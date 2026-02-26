package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.User;
import com.rvladimir.service.UserService;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;
import com.rvladimir.web.error.ValidationException;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;

import jakarta.inject.Inject;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Test class for UserResource.
 */
@MicronautTest
class UserResourceTest {

    private static final String TEST_NAME = "John";
    private static final String TEST_LASTNAME = "Doe";
    private static final String TEST_NICKNAME = "johnd";
    private static final String TEST_EMAIL = "john.doe@example.com";
    private static final String TEST_EMAIL_INVALID = "invalid-email";
    private static final String TEST_EMAIL_EXISTING = "existing@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String VALIDATION_MESSAGE = "Duplicate value for email";
    private static final String VALIDATION_FIELD = "email";
    private static final String VALIDATION_CODE = "DUPLICATE";
    private static final String ENDPOINT_USER_CREATE = "/user/create";
    private static final int BIRTH_YEAR = 1990;
    private static final int BIRTH_MONTH = 5;
    private static final int BIRTH_DAY = 15;
    private static final long USER_ID_1 = 1L;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    UserService userService;

    @MockBean(UserService.class)
    UserService userService() {
        return mock(UserService.class);
    }

    @Test
    void testCreateUserSuccess() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_PASSWORD
        );

        UserDTO userDTO = new UserDTO(
            USER_ID_1,
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            User.Role.USER
        );

        when(userService.create(any(CreateUserDTO.class))).thenReturn(userDTO);

        // When
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        HttpResponse<UserDTO> response = client.toBlocking().exchange(request, UserDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getName()).isEqualTo(TEST_NAME);
        assertThat(response.body().getNickname()).isEqualTo(TEST_NICKNAME);
        assertThat(response.body().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(response.body().getRole()).isEqualTo(User.Role.USER);

        verify(userService).create(any(CreateUserDTO.class));
    }

    @Test
    void testCreateUserValidationErrorEmptyName() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            "",
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL,
            TEST_PASSWORD
        );

        // When & Then
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, UserDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testCreateUserValidationErrorInvalidEmail() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL_INVALID,
            TEST_PASSWORD
        );

        // When & Then
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, UserDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testCreateUserDuplicateEmail() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_NAME,
            TEST_LASTNAME,
            TEST_NICKNAME,
            LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, BIRTH_DAY),
            TEST_EMAIL_EXISTING,
            TEST_PASSWORD
        );

        when(userService.create(any(CreateUserDTO.class)))
            .thenThrow(new ValidationException(VALIDATION_MESSAGE, VALIDATION_FIELD, VALIDATION_CODE));

        // When & Then
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, UserDTO.class))
            .isInstanceOf(HttpClientResponseException.class);
    }
}
