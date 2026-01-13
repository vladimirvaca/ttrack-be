package com.rvladimir.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.test.PostgresTestContainer;
import com.rvladimir.test.TestDataFactory;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrainingSessionRepositoryIntegrationTest implements TestPropertyProvider {

    private static final String TEST_USER_EMAIL = "john.doe@example.com";
    private static final String TEST_USER_PASSWORD = "hashedPassword";
    private static final String TEST_SESSION_NAME = "Morning Workout";
    private static final String TEST_SESSION_DESCRIPTION = "A quick morning workout routine";
    private static final String TEST_SESSION_NAME_EVENING = "Evening Workout";
    private static final String TEST_SESSION_DESCRIPTION_EVENING = "An evening cardio session";
    private static final String TEST_SESSION_NAME_UPDATED = "Updated Workout";
    private static final String TEST_SESSION_DESCRIPTION_UPDATED = "Updated description";
    private static final long NON_EXISTING_ID = 999L;
    private static final int YEAR_2026 = 2026;
    private static final int MONTH_1 = 1;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int HOUR_18 = 18;
    private static final int MINUTE_30 = 30;
    private static final int MINUTE_0 = 0;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

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
    void testSaveTrainingSession() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession = new TrainingSession(
            null,
            TEST_SESSION_NAME,
            TEST_SESSION_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );

        // When
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);

        // Then
        assertThat(savedTrainingSession.getId()).isNotNull();
        assertThat(savedTrainingSession.getName()).isEqualTo(TEST_SESSION_NAME);
        assertThat(savedTrainingSession.getDescription()).isEqualTo(TEST_SESSION_DESCRIPTION);
        assertThat(savedTrainingSession.getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(savedTrainingSession.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedTrainingSession.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession = new TrainingSession(
            null,
            TEST_SESSION_NAME,
            TEST_SESSION_DESCRIPTION,
            TrainingSession.Status.IN_PROGRESS,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);

        // When
        Optional<TrainingSession> foundTrainingSession =
            trainingSessionRepository.findById(savedTrainingSession.getId());

        // Then
        assertThat(foundTrainingSession).isPresent();
        assertThat(foundTrainingSession.get().getName()).isEqualTo(TEST_SESSION_NAME);
        assertThat(foundTrainingSession.get().getStatus()).isEqualTo(TrainingSession.Status.IN_PROGRESS);
        assertThat(foundTrainingSession.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void testFindByIdNotFound() {
        // When
        Optional<TrainingSession> foundTrainingSession = trainingSessionRepository.findById(NON_EXISTING_ID);

        // Then
        assertThat(foundTrainingSession).isEmpty();
    }

    @Test
    void testSaveMultipleTrainingSessions() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession1 = new TrainingSession(
            null,
            TEST_SESSION_NAME,
            TEST_SESSION_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );

        TrainingSession trainingSession2 = new TrainingSession(
            null,
            TEST_SESSION_NAME_EVENING,
            TEST_SESSION_DESCRIPTION_EVENING,
            TrainingSession.Status.FINISHED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_18, MINUTE_0)
        );

        // When
        trainingSessionRepository.save(trainingSession1);
        trainingSessionRepository.save(trainingSession2);

        // Then
        long count = trainingSessionRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testUpdateTrainingSession() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession = new TrainingSession(
            null,
            TEST_SESSION_NAME,
            TEST_SESSION_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);

        // When
        savedTrainingSession.setName(TEST_SESSION_NAME_UPDATED);
        savedTrainingSession.setDescription(TEST_SESSION_DESCRIPTION_UPDATED);
        savedTrainingSession.setStatus(TrainingSession.Status.FINISHED);
        TrainingSession updatedTrainingSession = trainingSessionRepository.update(savedTrainingSession);

        // Then
        assertThat(updatedTrainingSession.getName()).isEqualTo(TEST_SESSION_NAME_UPDATED);
        assertThat(updatedTrainingSession.getDescription()).isEqualTo(TEST_SESSION_DESCRIPTION_UPDATED);
        assertThat(updatedTrainingSession.getStatus()).isEqualTo(TrainingSession.Status.FINISHED);

        Optional<TrainingSession> retrievedTrainingSession =
            trainingSessionRepository.findById(savedTrainingSession.getId());
        assertThat(retrievedTrainingSession).isPresent();
        assertThat(retrievedTrainingSession.get().getName()).isEqualTo(TEST_SESSION_NAME_UPDATED);
        assertThat(retrievedTrainingSession.get().getStatus()).isEqualTo(TrainingSession.Status.FINISHED);
    }

    @Test
    void testDeleteTrainingSession() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession = new TrainingSession(
            null,
            TEST_SESSION_NAME,
            TEST_SESSION_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);
        Long trainingSessionId = savedTrainingSession.getId();

        // When
        trainingSessionRepository.deleteById(trainingSessionId);

        // Then
        Optional<TrainingSession> foundTrainingSession = trainingSessionRepository.findById(trainingSessionId);
        assertThat(foundTrainingSession).isEmpty();
    }

    @Test
    void testSaveTrainingSessionWithNullNameAndDescription() {
        // Given
        User user = createAndSaveUser(TEST_USER_EMAIL);

        TrainingSession trainingSession = new TrainingSession(
            null,
            null,
            null,
            TrainingSession.Status.IS_TEMPLATE,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_1, DAY_13, HOUR_10, MINUTE_30)
        );

        // When
        TrainingSession savedTrainingSession = trainingSessionRepository.save(trainingSession);

        // Then
        assertThat(savedTrainingSession.getId()).isNotNull();
        assertThat(savedTrainingSession.getName()).isNull();
        assertThat(savedTrainingSession.getDescription()).isNull();
        assertThat(savedTrainingSession.getStatus()).isEqualTo(TrainingSession.Status.IS_TEMPLATE);
    }

    private User createAndSaveUser(String email) {
        return userRepository.save(TestDataFactory.createUser(email));
    }
}
