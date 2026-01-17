package com.rvladimir.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.test.PostgresTestContainer;
import com.rvladimir.test.TestDataFactory;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
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
class SessionExerciseRepositoryIntegrationTest implements TestPropertyProvider {
    private static final String TEST_USER_EMAIL = "session.exercise.user@example.com";
    private static final String TEST_EXERCISE_NAME = "Burpee";
    private static final String TEST_EXERCISE_DESCRIPTION = "Full body exercise";
    private static final Exercise.Type TEST_EXERCISE_TYPE = Exercise.Type.HIIT;
    private static final String TEST_EXERCISE_IMAGE = "burpee.png";
    private static final int TEST_ROUNDS = 3;
    private static final int TEST_SETS = 5;
    private static final int TEST_REPETITIONS = 12;
    private static final int TEST_SPRINTS = 0;
    private static final double TEST_WEIGHT = 0.0;
    private static final double TEST_DISTANCE = 0.0;
    private static final LocalTime TEST_TIME = LocalTime.of(0, 30, 0);
    private static final int TEST_REST_TIME = 60;
    private static final int TEST_ORDER = 1;
    private static final SessionExercise.Status TEST_STATUS = SessionExercise.Status.STARTED;
    private static final SessionExercise.UnitOfMeasurement TEST_UNIT = SessionExercise.UnitOfMeasurement.KILOMETERS;
    private static final long NON_EXISTING_ID = 999L;
    private static final int TEST_PLUS_MINUTES = 30;
    private static final int TEST_UPDATED_ROUNDS = 10;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

    @Inject
    SessionExerciseRepository sessionExerciseRepository;
    @Inject
    TrainingSessionRepository trainingSessionRepository;
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

    private User createAndSaveUser() {
        return userRepository.save(TestDataFactory.createUser(TEST_USER_EMAIL));
    }

    private Exercise createAndSaveExercise() {
        Exercise exercise = new Exercise(
            null,
            TEST_EXERCISE_NAME,
            TEST_EXERCISE_DESCRIPTION,
            TEST_EXERCISE_TYPE,
            TEST_EXERCISE_IMAGE,
            LocalDateTime.now());
        return exerciseRepository.save(exercise);
    }

    private TrainingSession createAndSaveTrainingSession(User user) {
        TrainingSession session = new TrainingSession(
            null,
            "Session 1",
            "Test session",
            TrainingSession.Status.STARTED,
            user, LocalDateTime.now());
        return trainingSessionRepository.save(session);
    }

    private SessionExercise buildSessionExercise(Exercise exercise, TrainingSession session) {
        return new SessionExercise(
            null,
            TEST_ROUNDS,
            TEST_SETS,
            TEST_REPETITIONS,
            TEST_SPRINTS,
            TEST_TIME,
            TEST_WEIGHT,
            TEST_DISTANCE,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(TEST_PLUS_MINUTES),
            TEST_REST_TIME,
            TEST_STATUS,
            TEST_ORDER,
            exercise,
            session,
            LocalDateTime.now(),
            TEST_UNIT
        );
    }

    @Test
    void testSaveSessionExercise() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = createAndSaveTrainingSession(user);
        Exercise exercise = createAndSaveExercise();
        SessionExercise sessionExercise = buildSessionExercise(exercise, session);

        // When
        SessionExercise saved = sessionExerciseRepository.save(sessionExercise);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getRounds()).isEqualTo(TEST_ROUNDS);
        assertThat(saved.getExercise().getName()).isEqualTo(TEST_EXERCISE_NAME);
        assertThat(saved.getTrainingSession().getId()).isEqualTo(session.getId());
    }

    @Test
    void testFindById() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = createAndSaveTrainingSession(user);
        Exercise exercise = createAndSaveExercise();
        SessionExercise sessionExercise = buildSessionExercise(exercise, session);
        SessionExercise saved = sessionExerciseRepository.save(sessionExercise);

        // When
        Optional<SessionExercise> found = sessionExerciseRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRounds()).isEqualTo(TEST_ROUNDS);
    }

    @Test
    void testFindByIdNotFound() {
        // When
        Optional<SessionExercise> found = sessionExerciseRepository.findById(NON_EXISTING_ID);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void testFindByTrainingSessionId() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = createAndSaveTrainingSession(user);
        Exercise exercise = createAndSaveExercise();
        SessionExercise sessionExercise = buildSessionExercise(exercise, session);
        sessionExerciseRepository.save(sessionExercise);

        // When
        List<SessionExercise> found = sessionExerciseRepository.findByTrainingSessionId(session.getId());

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.getFirst().getTrainingSession().getId()).isEqualTo(session.getId());
    }

    @Test
    void testUpdateSessionExercise() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = createAndSaveTrainingSession(user);
        Exercise exercise = createAndSaveExercise();
        SessionExercise sessionExercise = buildSessionExercise(exercise, session);
        SessionExercise saved = sessionExerciseRepository.save(sessionExercise);

        // When
        saved.setRounds(TEST_UPDATED_ROUNDS);
        saved.setStatus(SessionExercise.Status.FINISHED);
        SessionExercise updated = sessionExerciseRepository.update(saved);

        // Then
        assertThat(updated.getRounds()).isEqualTo(TEST_UPDATED_ROUNDS);
        assertThat(updated.getStatus()).isEqualTo(SessionExercise.Status.FINISHED);
    }

    @Test
    void testDeleteSessionExercise() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = createAndSaveTrainingSession(user);
        Exercise exercise = createAndSaveExercise();
        SessionExercise sessionExercise = buildSessionExercise(exercise, session);
        SessionExercise saved = sessionExerciseRepository.save(sessionExercise);
        Long id = saved.getId();

        // When
        sessionExerciseRepository.deleteById(id);
        Optional<SessionExercise> found = sessionExerciseRepository.findById(id);

        // Then
        assertThat(found).isEmpty();
    }
}
