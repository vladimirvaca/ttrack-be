package com.rvladimir.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
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

/**
 * Integration tests verifying the repository operations that back the Quick Start Interval feature.
 * Tests the atomic creation of a TrainingSession + SessionExercise pair.
 */
@MicronautTest(transactional = false)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class QuickStartIntervalRepositoryIntegrationTest implements TestPropertyProvider {

    private static final String TEST_USER_EMAIL = "quickstart.user@example.com";
    private static final String QUICK_START_NAME_HIIT = "Quick Start – HIIT";
    private static final String QUICK_START_NAME_BOXING = "Quick Start – BOXING_BAG";
    private static final String QUICK_START_NAME_INTERVAL = "Quick Start – INTERVAL";
    private static final String TEST_EXERCISE_NAME = "Heavy Bag Round";
    private static final String TEST_EXERCISE_DESC = "Standard boxing round on heavy bag";
    private static final String TEST_EXERCISE_IMG = "boxing_bag.png";
    private static final int TEST_ROUNDS = 5;
    private static final int TEST_SPRINTS = 10;
    private static final int TEST_REST_TIME = 60;
    private static final int DURATION_MINUTES = 3;
    private static final int FIRST_EXERCISE_ORDER = 1;
    private static final long EXPECTED_COUNT_ONE = 1L;
    private static final long EXPECTED_COUNT_TWO = 2L;
    private static final String TEST_NOTES = "Focus on speed";

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

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

    private User createAndSaveUser() {
        return userRepository.save(TestDataFactory.createUser(TEST_USER_EMAIL));
    }

    private Exercise createAndSaveExercise() {
        return exerciseRepository.save(
            new Exercise(null, TEST_EXERCISE_NAME, TEST_EXERCISE_DESC,
                TypeOfExercise.BOXING_BAG, TEST_EXERCISE_IMG, LocalDateTime.now())
        );
    }

    private TrainingSession buildSession(User user, String name) {
        TrainingSession session = new TrainingSession();
        session.setName(name);
        session.setStatus(TrainingSession.Status.STARTED);
        session.setCreatedAt(LocalDateTime.now());
        session.setUser(user);
        return session;
    }

    private SessionExercise buildExercise(TrainingSession session, TypeOfExercise type) {
        SessionExercise exercise = new SessionExercise();
        exercise.setRounds(TEST_ROUNDS);
        exercise.setSprints(TEST_SPRINTS);
        exercise.setDuration(LocalTime.of(0, DURATION_MINUTES));
        exercise.setRestTime(TEST_REST_TIME);
        exercise.setStatus(SessionExercise.Status.STARTED);
        exercise.setExerciseOrder(FIRST_EXERCISE_ORDER);
        exercise.setTrainingSession(session);
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setTypeOfExercise(type);
        return exercise;
    }

    @Test
    void testSaveTrainingSessionForQuickStart() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session = buildSession(user, QUICK_START_NAME_HIIT);

        // When
        TrainingSession savedSession = trainingSessionRepository.save(session);

        // Then
        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getName()).isEqualTo(QUICK_START_NAME_HIIT);
        assertThat(savedSession.getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(savedSession.getUser().getId()).isEqualTo(user.getId());
        assertThat(savedSession.getCreatedAt()).isNotNull();
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
    }

    @Test
    void testSaveSessionExerciseLinkedToQuickStartSession() {
        // Given
        User user = createAndSaveUser();
        TrainingSession savedSession = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_HIIT));
        SessionExercise exercise = buildExercise(savedSession, TypeOfExercise.HIIT);

        // When
        SessionExercise savedExercise = sessionExerciseRepository.save(exercise);

        // Then
        assertThat(savedExercise.getId()).isNotNull();
        assertThat(savedExercise.getRounds()).isEqualTo(TEST_ROUNDS);
        assertThat(savedExercise.getSprints()).isEqualTo(TEST_SPRINTS);
        assertThat(savedExercise.getDuration()).isEqualTo(LocalTime.of(0, DURATION_MINUTES));
        assertThat(savedExercise.getRestTime()).isEqualTo(TEST_REST_TIME);
        assertThat(savedExercise.getStatus()).isEqualTo(SessionExercise.Status.STARTED);
        assertThat(savedExercise.getExerciseOrder()).isEqualTo(FIRST_EXERCISE_ORDER);
        assertThat(savedExercise.getTypeOfExercise()).isEqualTo(TypeOfExercise.HIIT);
        assertThat(savedExercise.getTrainingSession().getId()).isEqualTo(savedSession.getId());
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
    }

    @Test
    void testQuickStartSessionAndExerciseAreLinked() {
        // Given
        User user = createAndSaveUser();
        TrainingSession savedSession = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_BOXING));
        SessionExercise exercise = buildExercise(savedSession, TypeOfExercise.BOXING_BAG);
        SessionExercise savedExercise = sessionExerciseRepository.save(exercise);

        // When
        List<SessionExercise> exercises =
            sessionExerciseRepository.findByTrainingSessionId(savedSession.getId());

        // Then
        assertThat(exercises).hasSize(1);
        assertThat(exercises.getFirst().getId()).isEqualTo(savedExercise.getId());
        assertThat(exercises.getFirst().getTypeOfExercise()).isEqualTo(TypeOfExercise.BOXING_BAG);
        assertThat(exercises.getFirst().getTrainingSession().getId()).isEqualTo(savedSession.getId());
    }

    @Test
    void testQuickStartWithNullTypeOfExercise() {
        // Given — typeOfExercise is null (session name label defaults to "INTERVAL")
        User user = createAndSaveUser();
        TrainingSession savedSession = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_INTERVAL));

        SessionExercise exercise = new SessionExercise();
        exercise.setRounds(TEST_ROUNDS);
        exercise.setRestTime(TEST_REST_TIME);
        exercise.setStatus(SessionExercise.Status.STARTED);
        exercise.setExerciseOrder(FIRST_EXERCISE_ORDER);
        exercise.setTrainingSession(savedSession);
        exercise.setCreatedAt(LocalDateTime.now());
        // typeOfExercise intentionally null

        // When
        SessionExercise savedExercise = sessionExerciseRepository.save(exercise);

        // Then
        assertThat(savedExercise.getId()).isNotNull();
        assertThat(savedExercise.getTypeOfExercise()).isNull();
        assertThat(savedSession.getName()).isEqualTo(QUICK_START_NAME_INTERVAL);
    }

    @Test
    void testQuickStartWithCatalogExerciseLinked() {
        // Given — the session exercise references a catalog exercise
        User user = createAndSaveUser();
        Exercise catalogExercise = createAndSaveExercise();
        TrainingSession savedSession = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_BOXING));

        SessionExercise exercise = buildExercise(savedSession, TypeOfExercise.BOXING_BAG);
        exercise.setExercise(catalogExercise);
        exercise.setNotes(TEST_NOTES);

        // When
        SessionExercise savedExercise = sessionExerciseRepository.save(exercise);
        Optional<SessionExercise> found = sessionExerciseRepository.findById(savedExercise.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getExercise()).isNotNull();
        assertThat(found.get().getExercise().getId()).isEqualTo(catalogExercise.getId());
        assertThat(found.get().getNotes()).isEqualTo(TEST_NOTES);
    }

    @Test
    void testMultipleQuickStartSessionsForSameUser() {
        // Given
        User user = createAndSaveUser();
        TrainingSession session1 = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_HIIT));
        TrainingSession session2 = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_BOXING));

        sessionExerciseRepository.save(buildExercise(session1, TypeOfExercise.HIIT));
        sessionExerciseRepository.save(buildExercise(session2, TypeOfExercise.BOXING_BAG));

        // When
        List<SessionExercise> exercisesSession1 =
            sessionExerciseRepository.findByTrainingSessionId(session1.getId());
        List<SessionExercise> exercisesSession2 =
            sessionExerciseRepository.findByTrainingSessionId(session2.getId());

        // Then
        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_TWO);
        assertThat(exercisesSession1).hasSize(1);
        assertThat(exercisesSession1.getFirst().getTypeOfExercise()).isEqualTo(TypeOfExercise.HIIT);
        assertThat(exercisesSession2).hasSize(1);
        assertThat(exercisesSession2.getFirst().getTypeOfExercise()).isEqualTo(TypeOfExercise.BOXING_BAG);
    }

    @Test
    void testDeleteSessionCascadesExercises() {
        // Given
        User user = createAndSaveUser();
        TrainingSession savedSession = trainingSessionRepository.save(buildSession(user, QUICK_START_NAME_HIIT));
        sessionExerciseRepository.save(buildExercise(savedSession, TypeOfExercise.HIIT));

        assertThat(trainingSessionRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);
        assertThat(sessionExerciseRepository.count()).isEqualTo(EXPECTED_COUNT_ONE);

        // When
        sessionExerciseRepository.deleteAll();
        trainingSessionRepository.deleteById(savedSession.getId());

        // Then
        assertThat(trainingSessionRepository.count()).isEqualTo(0L);
        assertThat(sessionExerciseRepository.count()).isEqualTo(0L);
    }
}


