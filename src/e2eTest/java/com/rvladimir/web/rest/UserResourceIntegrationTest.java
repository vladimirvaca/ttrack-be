package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.domain.User;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateUserDTO;
import com.rvladimir.service.dto.UserDTO;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration test for UserResource.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserResourceIntegrationTest implements TestPropertyProvider {

    private static final String TEST_JOHN = "John";
    private static final String TEST_DOE = "Doe";
    private static final String TEST_JANE = "Jane";
    private static final String TEST_SMITH = "Smith";
    private static final String TEST_FIRST = "First";
    private static final String TEST_SECOND = "Second";
    private static final String TEST_USER = "User";
    private static final String TEST_INVALID = "Invalid";
    private static final String TEST_EMAIL_TEXT = "Email";
    private static final String TEST_ONE = "One";
    private static final String TEST_TWO = "Two";
    private static final String TEST_THREE = "Three";
    private static final String TEST_ADMIN = "Admin";
    private static final String TEST_LASTNAME = "Lastname";
    private static final String TEST_EMAIL_JOHN = "john.doe@example.com";
    private static final String TEST_EMAIL_JANE = "jane.smith@example.com";
    private static final String TEST_EMAIL_DUPLICATE = "duplicate@example.com";
    private static final String TEST_EMAIL_INVALID = "not-an-email";
    private static final String TEST_EMAIL_TEST = "test@example.com";
    private static final String TEST_EMAIL_ADMIN = "admin@example.com";
    private static final String TEST_EMAIL_USER1 = "user1@example.com";
    private static final String TEST_EMAIL_USER2 = "user2@example.com";
    private static final String TEST_EMAIL_USER3 = "user3@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_PASSWORD_1 = "password1";
    private static final String TEST_PASSWORD_2 = "password2";
    private static final String TEST_PASSWORD_3 = "password3";
    private static final String TEST_PASSWORD_GENERIC = "password";
    private static final String TEST_PLAIN_PASSWORD = "plainPassword";
    private static final String TEST_ADMIN_PASSWORD = "adminPassword";
    private static final String BCRYPT_HASH_PREFIX = "$2a$";
    private static final String ENDPOINT_USER_CREATE = "/user/create";
    private static final int BIRTH_YEAR_1980 = 1980;
    private static final int BIRTH_YEAR_1985 = 1985;
    private static final int BIRTH_YEAR_1990 = 1990;
    private static final int BIRTH_YEAR_1991 = 1991;
    private static final int BIRTH_YEAR_1992 = 1992;
    private static final int BIRTH_MONTH_2 = 2;
    private static final int BIRTH_MONTH_3 = 3;
    private static final int BIRTH_MONTH_5 = 5;
    private static final int BIRTH_MONTH_10 = 10;
    private static final int BIRTH_DAY_15 = 15;
    private static final int BIRTH_DAY_20 = 20;
    private static final long EXPECTED_USER_COUNT_ONE = 1L;
    private static final long EXPECTED_USER_COUNT_THREE = 3L;

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    UserRepository userRepository;

    @Override
    public Map<String, String> getProperties() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
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

    @Test
    void testCreateUserEndToEndSuccess() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_JOHN,
            TEST_DOE,
            LocalDate.of(BIRTH_YEAR_1990, BIRTH_MONTH_5, BIRTH_DAY_15),
            TEST_EMAIL_JOHN,
            TEST_PASSWORD,
            User.Role.USER
        );

        // When
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        HttpResponse<UserDTO> response = client.toBlocking().exchange(request, UserDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getName()).isEqualTo(TEST_JOHN);
        assertThat(response.body().getLastname()).isEqualTo(TEST_DOE);
        assertThat(response.body().getEmail()).isEqualTo(TEST_EMAIL_JOHN);
        assertThat(response.body().getRole()).isEqualTo(User.Role.USER);

        // Verify user was actually saved to database
        boolean exists = userRepository.existsByEmail(TEST_EMAIL_JOHN);
        assertThat(exists).isTrue();
    }

    @Test
    void testCreateUserPasswordIsHashed() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_JANE,
            TEST_SMITH,
            LocalDate.of(BIRTH_YEAR_1985, BIRTH_MONTH_10, BIRTH_DAY_20),
            TEST_EMAIL_JANE,
            TEST_PLAIN_PASSWORD,
            User.Role.ADMIN
        );

        // When
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        client.toBlocking().exchange(request, UserDTO.class);

        // Then
        User savedUser = userRepository.findByEmail(TEST_EMAIL_JANE).orElseThrow();
        assertThat(savedUser.getPassword()).isNotEqualTo(TEST_PLAIN_PASSWORD);
        // BCrypt hash prefix
        assertThat(savedUser.getPassword()).startsWith(BCRYPT_HASH_PREFIX);
    }

    @Test
    void testCreateUserDuplicateEmailReturns400() {
        // Given
        CreateUserDTO firstUser = new CreateUserDTO(
            TEST_FIRST,
            TEST_USER,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_DUPLICATE,
            TEST_PASSWORD_1,
            User.Role.USER
        );

        CreateUserDTO secondUser = new CreateUserDTO(
            TEST_SECOND,
            TEST_USER,
            LocalDate.of(BIRTH_YEAR_1991, BIRTH_MONTH_2, BIRTH_MONTH_2),
            TEST_EMAIL_DUPLICATE,
            TEST_PASSWORD_2,
            User.Role.USER
        );

        // Create first user
        HttpRequest<CreateUserDTO> firstRequest = HttpRequest.POST(ENDPOINT_USER_CREATE, firstUser);
        client.toBlocking().exchange(firstRequest, UserDTO.class);

        // When & Then - Try to create second user with same email
        HttpRequest<CreateUserDTO> secondRequest = HttpRequest.POST(ENDPOINT_USER_CREATE, secondUser);
        assertThatThrownBy(() -> client.toBlocking().exchange(secondRequest, UserDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        // Verify only one user exists
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(EXPECTED_USER_COUNT_ONE);
    }

    @Test
    void testCreateUserInvalidEmailReturns400() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_INVALID,
            TEST_EMAIL_TEXT,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_INVALID,
            TEST_PASSWORD_GENERIC,
            User.Role.USER
        );

        // When & Then
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, UserDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        // Verify no user was created
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(0);
    }

    @Test
    void testCreateUserMissingRequiredFieldsReturns400() {
        // Given - null name
        CreateUserDTO createUserDTO = new CreateUserDTO(
            null,
            TEST_LASTNAME,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_TEST,
            TEST_PASSWORD_GENERIC,
            User.Role.USER
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
    void testCreateUserWithAdminRole() {
        // Given
        CreateUserDTO createUserDTO = new CreateUserDTO(
            TEST_ADMIN,
            TEST_USER,
            LocalDate.of(BIRTH_YEAR_1980, 1, 1),
            TEST_EMAIL_ADMIN,
            TEST_ADMIN_PASSWORD,
            User.Role.ADMIN
        );

        // When
        HttpRequest<CreateUserDTO> request = HttpRequest.POST(ENDPOINT_USER_CREATE, createUserDTO);
        HttpResponse<UserDTO> response = client.toBlocking().exchange(request, UserDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getRole()).isEqualTo(User.Role.ADMIN);

        // Verify role in database
        User savedUser = userRepository.findByEmail(TEST_EMAIL_ADMIN).orElseThrow();
        assertThat(savedUser.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void testMultipleUsersCanBeCreated() {
        // Given
        CreateUserDTO user1 = new CreateUserDTO(
            TEST_USER,
            TEST_ONE,
            LocalDate.of(BIRTH_YEAR_1990, 1, 1),
            TEST_EMAIL_USER1,
            TEST_PASSWORD_1,
            User.Role.USER
        );

        CreateUserDTO user2 = new CreateUserDTO(
            TEST_USER,
            TEST_TWO,
            LocalDate.of(BIRTH_YEAR_1991, BIRTH_MONTH_2, BIRTH_MONTH_2),
            TEST_EMAIL_USER2,
            TEST_PASSWORD_2,
            User.Role.USER
        );

        CreateUserDTO user3 = new CreateUserDTO(
            TEST_USER,
            TEST_THREE,
            LocalDate.of(BIRTH_YEAR_1992, BIRTH_MONTH_3, BIRTH_MONTH_3),
            TEST_EMAIL_USER3,
            TEST_PASSWORD_3,
            User.Role.ADMIN
        );

        // When
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_USER_CREATE, user1), UserDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_USER_CREATE, user2), UserDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(ENDPOINT_USER_CREATE, user3), UserDTO.class);

        // Then
        long userCount = userRepository.count();
        assertThat(userCount).isEqualTo(EXPECTED_USER_COUNT_THREE);
    }
}
