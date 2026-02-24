package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.test.PostgresTestContainer;
import com.rvladimir.test.TestDataFactory;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
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
 * End-to-end tests for TrainingSessionResource.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingSessionResourceE2eTest implements TestPropertyProvider {

    private static final String TEST_USER_EMAIL = "john.doe@example.com";
    private static final String TEST_USER_EMAIL_2 = "jane.smith@example.com";
    private static final String ENDPOINT_TRAINING_SESSION_CREATE = "/training-session/create";
    private static final long NON_EXISTENT_USER_ID = 999L;
    private static final long EXPECTED_SESSION_COUNT_ONE = 1L;
    private static final long EXPECTED_SESSION_COUNT_TWO = 2L;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TrainingSessionRepository trainingSessionRepository;

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
        trainingSessionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateTrainingSessionEndToEndSuccess() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(user.getId());

        // When
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        HttpResponse<TrainingSessionDTO> response = client.toBlocking().exchange(request, TrainingSessionDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getId()).isNotNull();
        assertThat(response.body().getUserId()).isEqualTo(user.getId());
        assertThat(response.body().getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(response.body().getCreatedAt()).isNotNull();

        // Verify training session was actually saved to database
        long count = trainingSessionRepository.count();
        assertThat(count).isEqualTo(EXPECTED_SESSION_COUNT_ONE);
    }

    @Test
    void testCreateTrainingSessionUserNotFoundReturns400() {
        // Given
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(NON_EXISTENT_USER_ID);

        // When & Then
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TrainingSessionDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        // Verify no training session was created
        long count = trainingSessionRepository.count();
        assertThat(count).isEqualTo(0);
    }

    @Test
    void testCreateTrainingSessionMissingRequiredFieldsReturns400() {
        // Given - null userId
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(null);

        // When & Then
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, TrainingSessionDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testMultipleTrainingSessionsCanBeCreatedForSameUser() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        CreateTrainingSessionDTO session1 = new CreateTrainingSessionDTO(user.getId());
        CreateTrainingSessionDTO session2 = new CreateTrainingSessionDTO(user.getId());

        // When
        client.toBlocking()
            .exchange(HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, session1), TrainingSessionDTO.class);
        client.toBlocking()
            .exchange(HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, session2), TrainingSessionDTO.class);

        // Then
        long count = trainingSessionRepository.count();
        assertThat(count).isEqualTo(EXPECTED_SESSION_COUNT_TWO);
    }

    @Test
    void testTrainingSessionsCanBeCreatedForDifferentUsers() {
        // Given
        User user1 = createAndSaveUser(TEST_USER_EMAIL);
        User user2 = createAndSaveUser(TEST_USER_EMAIL_2);

        CreateTrainingSessionDTO session1 = new CreateTrainingSessionDTO(user1.getId());
        CreateTrainingSessionDTO session2 = new CreateTrainingSessionDTO(user2.getId());

        // When
        HttpResponse<TrainingSessionDTO> response1 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, session1), TrainingSessionDTO.class);
        HttpResponse<TrainingSessionDTO> response2 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, session2), TrainingSessionDTO.class);

        // Then
        assertThat(response1.body()).isNotNull();
        assertThat(response1.body().getUserId()).isEqualTo(user1.getId());
        assertThat(response2.body()).isNotNull();
        assertThat(response2.body().getUserId()).isEqualTo(user2.getId());

        long count = trainingSessionRepository.count();
        assertThat(count).isEqualTo(EXPECTED_SESSION_COUNT_TWO);
    }

    @Test
    void testCreateTrainingSessionSetsDefaultStatus() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(user.getId());

        // When
        HttpRequest<CreateTrainingSessionDTO> request =
            HttpRequest.POST(ENDPOINT_TRAINING_SESSION_CREATE, createTrainingSessionDTO);
        HttpResponse<TrainingSessionDTO> response = client.toBlocking().exchange(request, TrainingSessionDTO.class);

        // Then
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getStatus()).isEqualTo(TrainingSession.Status.STARTED);

        // Verify in database
        TrainingSession savedSession = trainingSessionRepository.findById(response.body().getId()).orElseThrow();
        assertThat(savedSession.getStatus()).isEqualTo(TrainingSession.Status.STARTED);
    }

    private User createAndSaveUser(String email) {
        return userRepository.save(TestDataFactory.createUser(email));
    }
}

