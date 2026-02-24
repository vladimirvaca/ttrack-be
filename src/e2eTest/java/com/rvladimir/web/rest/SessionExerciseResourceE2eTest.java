package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;
import com.rvladimir.test.PostgresTestContainer;
import com.rvladimir.test.TestDataFactory;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for SessionExerciseResource.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionExerciseResourceE2eTest implements TestPropertyProvider {
    private static final String ENDPOINT_CREATE = "/session-exercise/training-sessions/%d/session-exercise";
    private static final String ENDPOINT_GET_BY_SESSION = "/session-exercise/training-session/%d";
    private static final long EXPECTED_SESSION_EXERCISE_COUNT_ONE = 1L;
    private static final long EXPECTED_SESSION_EXERCISE_COUNT_TWO = 2L;
    private static final String TEST_EXERCISE_NAME = "Push-ups";
    private static final String TEST_EXERCISE_DESC = "desc";
    private static final String TEST_EXERCISE_IMG = "img";
    private static final String TEST_USER_EMAIL = "user@e2e.com";
    private static final int TEST_ROUNDS = 1;
    private static final int TEST_SETS = 2;
    private static final int TEST_REPS = 10;
    private static final int TEST_EXERCISE_ORDER_1 = 1;
    private static final int TEST_EXERCISE_ORDER_2 = 2;
    private static final int TEST_REST_TIME_1 = 60;
    private static final int TEST_REST_TIME_2 = 90;
    private static final double TEST_WEIGHT_1 = 50.0;
    private static final double TEST_WEIGHT_2 = 60.0;
    private static final int TEST_SETS_2 = 3;
    private static final int TEST_REPS_2 = 15;
    private static final String STATUS_STARTED = "STARTED";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String UNIT_KILOMETERS = "KILOMETERS";
    private static final String UNIT_MILES = "MILES";
    private static final int TEST_TIME_MIN_1 = 30;
    private static final int TEST_TIME_MIN_2 = 45;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    SessionExerciseRepository sessionExerciseRepository;

    @Inject
    TrainingSessionRepository trainingSessionRepository;

    @Inject
    ExerciseRepository exerciseRepository;

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
        sessionExerciseRepository.deleteAll();
        trainingSessionRepository.deleteAll();
        exerciseRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createAndSaveUser(String email) {
        return userRepository.save(TestDataFactory.createUser(email));
    }

    @Test
    void testCreateSessionExerciseEndToEndSuccess() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        Exercise exercise = exerciseRepository.save(
            new Exercise(null, TEST_EXERCISE_NAME, TEST_EXERCISE_DESC,
                Exercise.Type.STRENGTH, TEST_EXERCISE_IMG, LocalDateTime.now())
        );
        TrainingSession session = trainingSessionRepository.save(
            new TrainingSession(null, null, null, TrainingSession.Status.STARTED,
                user, LocalDateTime.now())
        );
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            TEST_ROUNDS, TEST_SETS, TEST_REPS, null,
            LocalTime.of(0, TEST_TIME_MIN_1), TEST_WEIGHT_1, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_1),
            TEST_REST_TIME_1, STATUS_STARTED, TEST_EXERCISE_ORDER_1,
            exercise.getId(), LocalDateTime.now(), UNIT_KILOMETERS
        );
        String endpoint = String.format(ENDPOINT_CREATE, session.getId());

        // When
        HttpRequest<CreateSessionExerciseDTO> request = HttpRequest.POST(endpoint, dto);
        HttpResponse<SessionExerciseDTO> response = client.toBlocking().exchange(request, SessionExerciseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().getId()).isNotNull();
        assertThat(response.body().getTrainingSessionId()).isEqualTo(session.getId());
        assertThat(response.body().getExerciseId()).isEqualTo(exercise.getId());
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_SESSION_EXERCISE_COUNT_ONE);
    }

    @Test
    void testCreateSessionExerciseMissingRequiredFieldsReturns400() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        TrainingSession session = trainingSessionRepository.save(
            new TrainingSession(null, null, null, TrainingSession.Status.STARTED,
                user, LocalDateTime.now())
        );
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            null, TEST_SETS, TEST_REPS, null,
            LocalTime.of(0, TEST_TIME_MIN_1), TEST_WEIGHT_1, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_1),
            TEST_REST_TIME_1, STATUS_STARTED, TEST_EXERCISE_ORDER_1,
            null, LocalDateTime.now(), UNIT_KILOMETERS
        );
        String endpoint = String.format(ENDPOINT_CREATE, session.getId());

        // When & Then
        HttpRequest<CreateSessionExerciseDTO> request = HttpRequest.POST(endpoint, dto);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, SessionExerciseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });
    }

    @Test
    void testMultipleSessionExercisesCanBeCreated() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        Exercise exercise = exerciseRepository.save(
            new Exercise(null, TEST_EXERCISE_NAME, TEST_EXERCISE_DESC,
                Exercise.Type.STRENGTH, TEST_EXERCISE_IMG, LocalDateTime.now())
        );
        TrainingSession session = trainingSessionRepository.save(
            new TrainingSession(null, null, null, TrainingSession.Status.STARTED,
                user, LocalDateTime.now())
        );
        CreateSessionExerciseDTO dto1 = new CreateSessionExerciseDTO(
            TEST_ROUNDS, TEST_SETS, TEST_REPS, null,
            LocalTime.of(0, TEST_TIME_MIN_1), TEST_WEIGHT_1, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_1),
            TEST_REST_TIME_1, STATUS_STARTED, TEST_EXERCISE_ORDER_1,
            exercise.getId(), LocalDateTime.now(), UNIT_KILOMETERS
        );
        CreateSessionExerciseDTO dto2 = new CreateSessionExerciseDTO(
            TEST_ROUNDS + 1, TEST_SETS_2, TEST_REPS_2, null,
            LocalTime.of(0, TEST_TIME_MIN_2), TEST_WEIGHT_2, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_2),
            TEST_REST_TIME_2, STATUS_IN_PROGRESS, TEST_EXERCISE_ORDER_2,
            exercise.getId(), LocalDateTime.now(), UNIT_MILES
        );
        String endpoint = String.format(ENDPOINT_CREATE, session.getId());

        // When
        client.toBlocking().exchange(HttpRequest.POST(endpoint, dto1), SessionExerciseDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(endpoint, dto2), SessionExerciseDTO.class);

        // Then
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_SESSION_EXERCISE_COUNT_TWO);
    }

    @Test
    void testGetSessionExercisesByTrainingSessionReturnsList() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        Exercise exercise = exerciseRepository.save(
            new Exercise(null, TEST_EXERCISE_NAME, TEST_EXERCISE_DESC,
                Exercise.Type.STRENGTH, TEST_EXERCISE_IMG, LocalDateTime.now())
        );
        TrainingSession session = trainingSessionRepository.save(
            new TrainingSession(null, null, null, TrainingSession.Status.STARTED,
                user, LocalDateTime.now())
        );
        CreateSessionExerciseDTO dto1 = new CreateSessionExerciseDTO(
            TEST_ROUNDS, TEST_SETS, TEST_REPS, null,
            LocalTime.of(0, TEST_TIME_MIN_1), TEST_WEIGHT_1, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_1),
            TEST_REST_TIME_1, STATUS_STARTED, TEST_EXERCISE_ORDER_1,
            exercise.getId(), LocalDateTime.now(), UNIT_KILOMETERS
        );
        CreateSessionExerciseDTO dto2 = new CreateSessionExerciseDTO(
            TEST_ROUNDS + 1, TEST_SETS_2, TEST_REPS_2, null,
            LocalTime.of(0, TEST_TIME_MIN_2), TEST_WEIGHT_2, null,
            LocalDateTime.now(), LocalDateTime.now().plusMinutes(TEST_TIME_MIN_2),
            TEST_REST_TIME_2, STATUS_IN_PROGRESS, TEST_EXERCISE_ORDER_2,
            exercise.getId(), LocalDateTime.now(), UNIT_MILES
        );
        String endpoint = String.format(ENDPOINT_CREATE, session.getId());
        client.toBlocking().exchange(HttpRequest.POST(endpoint, dto1), SessionExerciseDTO.class);
        client.toBlocking().exchange(HttpRequest.POST(endpoint, dto2), SessionExerciseDTO.class);
        String getEndpoint = String.format(ENDPOINT_GET_BY_SESSION, session.getId());

        // When
        HttpRequest<?> request = HttpRequest.GET(getEndpoint);
        HttpResponse<List<SessionExerciseDTO>> response = client.toBlocking().exchange(
            request, Argument.listOf(SessionExerciseDTO.class)
        );
        List<SessionExerciseDTO> result;
        if (response.body() != null) {
            result = response.body();
        } else {
            result = Collections.emptyList();
        }

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.OK.getCode());
        assertThat(result).isNotNull();
        assertThat(result.size()).isGreaterThanOrEqualTo(2);
    }
}

