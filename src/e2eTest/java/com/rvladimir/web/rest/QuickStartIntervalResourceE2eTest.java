package com.rvladimir.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
import com.rvladimir.domain.User;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.QuickStartIntervalDTO;
import com.rvladimir.service.dto.QuickStartIntervalResponseDTO;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for the Quick Start Interval endpoint in TrainingSessionResource.
 * Verifies the full flow: HTTP request → service → repository → database → response.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuickStartIntervalResourceE2eTest implements TestPropertyProvider {

    private static final String ENDPOINT_QUICK_START_INTERVAL = "/training-session/quick-start/interval";
    private static final String TEST_USER_EMAIL = "quickstart.e2e@example.com";
    private static final String TEST_USER_EMAIL_2 = "quickstart.e2e.2@example.com";
    private static final String TEST_EXERCISE_NAME = "Heavy Bag";
    private static final String TEST_EXERCISE_DESC = "Boxing heavy bag";
    private static final String TEST_EXERCISE_IMG = "heavy_bag.png";
    private static final long NON_EXISTENT_USER_ID = 999999L;
    private static final long EXPECTED_COUNT_ONE = 1L;
    private static final long EXPECTED_COUNT_TWO = 2L;
    private static final int TEST_ROUNDS = 5;
    private static final int TEST_SPRINTS = 10;
    private static final int TEST_REST_TIME = 60;
    private static final int DURATION_MINUTES = 3;
    private static final String TYPE_HIIT = "HIIT";
    private static final String TYPE_BOXING_BAG = "BOXING_BAG";
    private static final String TYPE_SHADOW_BOXING = "SHADOW_BOXING";
    private static final String TEST_NOTES = "Focus on speed";
    private static final String QUICK_START_NAME_PREFIX = "Quick Start – ";

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    TrainingSessionRepository trainingSessionRepository;

    @Inject
    SessionExerciseRepository sessionExerciseRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    ExerciseRepository exerciseRepository;

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

    private Exercise createAndSaveExercise() {
        return exerciseRepository.save(
            new Exercise(null, TEST_EXERCISE_NAME, TEST_EXERCISE_DESC,
                TypeOfExercise.BOXING_BAG, TEST_EXERCISE_IMG, LocalDateTime.now())
        );
    }

    @Test
    void testQuickStartIntervalHiitSuccess() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), TYPE_HIIT, TEST_ROUNDS, TEST_SPRINTS,
            LocalTime.of(0, DURATION_MINUTES), TEST_REST_TIME, null, null
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then — HTTP layer
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();

        // Then — TrainingSession in response
        assertThat(response.body().getTrainingSession()).isNotNull();
        assertThat(response.body().getTrainingSession().getId()).isNotNull();
        assertThat(response.body().getTrainingSession().getUserId()).isEqualTo(user.getId());
        assertThat(response.body().getTrainingSession().getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(response.body().getTrainingSession().getName()).isEqualTo(QUICK_START_NAME_PREFIX + TYPE_HIIT);
        assertThat(response.body().getTrainingSession().getCreatedAt()).isNotNull();

        // Then — SessionExercise in response
        assertThat(response.body().getSessionExercise()).isNotNull();
        assertThat(response.body().getSessionExercise().getId()).isNotNull();
        assertThat(response.body().getSessionExercise().getRounds()).isEqualTo(TEST_ROUNDS);
        assertThat(response.body().getSessionExercise().getSprints()).isEqualTo(TEST_SPRINTS);
        assertThat(response.body().getSessionExercise().getDuration()).isEqualTo(LocalTime.of(0, DURATION_MINUTES));
        assertThat(response.body().getSessionExercise().getRestTime()).isEqualTo(TEST_REST_TIME);
        assertThat(response.body().getSessionExercise().getTypeOfExercise()).isEqualTo(TYPE_HIIT);
        assertThat(response.body().getSessionExercise().getTrainingSessionId())
            .isEqualTo(response.body().getTrainingSession().getId());

        // Then — verify database state
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
    }

    @Test
    void testQuickStartIntervalBoxingBagWithNotes() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), TYPE_BOXING_BAG, TEST_ROUNDS, null,
            LocalTime.of(0, DURATION_MINUTES), TEST_REST_TIME, null, TEST_NOTES
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body().getTrainingSession().getName())
            .isEqualTo(QUICK_START_NAME_PREFIX + TYPE_BOXING_BAG);
        assertThat(response.body().getSessionExercise().getTypeOfExercise()).isEqualTo(TYPE_BOXING_BAG);
        assertThat(response.body().getSessionExercise().getNotes()).isEqualTo(TEST_NOTES);

        // Verify persisted exercise has the notes
        List<SessionExercise> exercises = sessionExerciseRepository.findByTrainingSessionId(
            response.body().getTrainingSession().getId()
        );
        assertThat(exercises).hasSize(1);
        assertThat(exercises.getFirst().getNotes()).isEqualTo(TEST_NOTES);
    }

    @Test
    void testQuickStartIntervalWithNullTypeOfExerciseDefaultsToInterval() {
        // Given — typeOfExercise is null, session name should default to "INTERVAL"
        User user = createAndSaveUser(TEST_USER_EMAIL);
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), null, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body().getTrainingSession().getName())
            .isEqualTo(QUICK_START_NAME_PREFIX + "INTERVAL");
        assertThat(response.body().getSessionExercise().getTypeOfExercise()).isNull();
    }

    @Test
    void testQuickStartIntervalWithCatalogExercise() {
        // Given — exerciseId links the session exercise to a catalog entry
        User user = createAndSaveUser(TEST_USER_EMAIL);
        Exercise catalogExercise = createAndSaveExercise();

        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), TYPE_BOXING_BAG, TEST_ROUNDS, TEST_SPRINTS,
            LocalTime.of(0, DURATION_MINUTES), TEST_REST_TIME, catalogExercise.getId(), TEST_NOTES
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body().getSessionExercise().getExerciseId()).isEqualTo(catalogExercise.getId());
    }

    @Test
    void testQuickStartIntervalUserNotFoundReturns400() {
        // Given
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            NON_EXISTENT_USER_ID, TYPE_HIIT, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When & Then
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        // Verify nothing was persisted
        assertThat(trainingSessionRepository.count()).isEqualTo(0L);
        assertThat(sessionExerciseRepository.count()).isEqualTo(0L);
    }

    @Test
    void testQuickStartIntervalNullUserIdReturns400() {
        // Given
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            null, TYPE_HIIT, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When & Then
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        assertThatThrownBy(() -> client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class))
            .isInstanceOf(HttpClientResponseException.class)
            .satisfies(ex -> {
                HttpClientResponseException httpEx = (HttpClientResponseException) ex;
                assertThat(httpEx.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
            });

        assertThat(trainingSessionRepository.count()).isEqualTo(0L);
    }

    @Test
    void testQuickStartIntervalOnlyRequiredFieldsSucceeds() {
        // Given — only userId is provided (all exercise metric fields are optional)
        User user = createAndSaveUser(TEST_USER_EMAIL);
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), null, null, null, null, null, null, null
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body().getTrainingSession().getUserId()).isEqualTo(user.getId());
        assertThat(response.body().getSessionExercise()).isNotNull();
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
    }

    @Test
    void testMultipleQuickStartsForSameUserCreateSeparateSessions() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        QuickStartIntervalDTO dto1 = new QuickStartIntervalDTO(
            user.getId(), TYPE_HIIT, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );
        QuickStartIntervalDTO dto2 = new QuickStartIntervalDTO(
            user.getId(), TYPE_BOXING_BAG, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When
        HttpResponse<QuickStartIntervalResponseDTO> response1 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto1), QuickStartIntervalResponseDTO.class);
        HttpResponse<QuickStartIntervalResponseDTO> response2 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto2), QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response1.body().getTrainingSession().getId())
            .isNotEqualTo(response2.body().getTrainingSession().getId());
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);

        // Verify isolation — each session has its own exercise
        List<SessionExercise> exercises1 = sessionExerciseRepository
            .findByTrainingSessionId(response1.body().getTrainingSession().getId());
        List<SessionExercise> exercises2 = sessionExerciseRepository
            .findByTrainingSessionId(response2.body().getTrainingSession().getId());
        assertThat(exercises1).hasSize(1);
        assertThat(exercises1.getFirst().getTypeOfExercise()).isEqualTo(TypeOfExercise.HIIT);
        assertThat(exercises2).hasSize(1);
        assertThat(exercises2.getFirst().getTypeOfExercise()).isEqualTo(TypeOfExercise.BOXING_BAG);
    }

    @Test
    void testMultipleQuickStartsForDifferentUsers() {
        // Given
        User user1 = createAndSaveUser(TEST_USER_EMAIL);
        User user2 = createAndSaveUser(TEST_USER_EMAIL_2);

        QuickStartIntervalDTO dto1 = new QuickStartIntervalDTO(
            user1.getId(), TYPE_SHADOW_BOXING, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );
        QuickStartIntervalDTO dto2 = new QuickStartIntervalDTO(
            user2.getId(), TYPE_HIIT, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When
        HttpResponse<QuickStartIntervalResponseDTO> response1 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto1), QuickStartIntervalResponseDTO.class);
        HttpResponse<QuickStartIntervalResponseDTO> response2 = client.toBlocking().exchange(
            HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto2), QuickStartIntervalResponseDTO.class);

        // Then
        assertThat(response1.body().getTrainingSession().getUserId()).isEqualTo(user1.getId());
        assertThat(response2.body().getTrainingSession().getUserId()).isEqualTo(user2.getId());
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);
    }

    @Test
    void testQuickStartSessionExerciseHasCorrectOrder() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            user.getId(), TYPE_HIIT, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );

        // When
        HttpRequest<QuickStartIntervalDTO> request = HttpRequest.POST(ENDPOINT_QUICK_START_INTERVAL, dto);
        HttpResponse<QuickStartIntervalResponseDTO> response =
            client.toBlocking().exchange(request, QuickStartIntervalResponseDTO.class);

        // Then — exercise order must always be 1 for a quick start
        assertThat(response.body().getSessionExercise().getExerciseOrder()).isEqualTo(1);

        SessionExercise persisted = sessionExerciseRepository
            .findById(response.body().getSessionExercise().getId())
            .orElseThrow();
        assertThat(persisted.getExerciseOrder()).isEqualTo(1);
        assertThat(persisted.getStatus()).isEqualTo(SessionExercise.Status.STARTED);
    }
}


