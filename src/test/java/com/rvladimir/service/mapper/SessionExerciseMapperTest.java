package com.rvladimir.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SessionExerciseMapper}.
 */
class SessionExerciseMapperTest {

    private static final int ROUNDS = 3;
    private static final int SETS = 4;
    private static final int REPETITIONS = 12;
    private static final int SPRINTS = 2;
    private static final LocalTime TIME = LocalTime.of(0, 45);
    private static final double WEIGHT = 80.0;
    private static final double DISTANCE = 200.0;
    private static final int REST_TIME = 90;
    private static final String STATUS = "STARTED";
    private static final int EXERCISE_ORDER = 2;
    private static final long EXERCISE_ID = 10L;
    private static final long TRAINING_SESSION_ID = 20L;
    private static final String UNIT_KILOMETERS = "KILOMETERS";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final long USER_ID = 100L;
    private static final String EXERCISE_NAME = "Bench Press";
    private static final Exercise.Type EXERCISE_TYPE = Exercise.Type.STRENGTH;
    private static final String EXERCISE_IMAGE = "img.png";
    private static final String USER_FIRSTNAME = "John";
    private static final String USER_LASTNAME = "Doe";
    private static final LocalDate USER_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String USER_EMAIL = "john@example.com";
    private static final String USER_PASSWORD = "pass";
    private static final User.Role USER_ROLE = User.Role.USER;
    private static final String TRAINING_SESSION_NAME = "Morning Session";
    private static final String DESC = "desc";
    private static final TrainingSession.Status TRAINING_SESSION_STATUS = TrainingSession.Status.STARTED;

    private final SessionExerciseMapper mapper = new SessionExerciseMapper();

    @Test
    void toEntityShouldMapDtoToEntity() {
        // Given
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, TIME, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, NOW, UNIT_KILOMETERS
        );
        Exercise exercise = new Exercise(EXERCISE_ID, EXERCISE_NAME, DESC, EXERCISE_TYPE, EXERCISE_IMAGE, NOW);
        User user = new User(USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_BIRTH, USER_EMAIL, USER_PASSWORD, USER_ROLE);
        TrainingSession trainingSession = new TrainingSession(
            TRAINING_SESSION_ID, TRAINING_SESSION_NAME, DESC, TRAINING_SESSION_STATUS, user, NOW
        );

        // When
        SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

        // Then
        assertThat(entity.getRounds()).isEqualTo(ROUNDS);
        assertThat(entity.getSets()).isEqualTo(SETS);
        assertThat(entity.getRepetitions()).isEqualTo(REPETITIONS);
        assertThat(entity.getSprints()).isEqualTo(SPRINTS);
        assertThat(entity.getTime()).isEqualTo(TIME);
        assertThat(entity.getWeight()).isEqualTo(WEIGHT);
        assertThat(entity.getDistance()).isEqualTo(DISTANCE);
        assertThat(entity.getStartTime()).isEqualTo(NOW);
        assertThat(entity.getEndTime()).isEqualTo(NOW.plusHours(1));
        assertThat(entity.getRestTime()).isEqualTo(REST_TIME);
        assertThat(entity.getStatus().name()).isEqualTo(STATUS);
        assertThat(entity.getExerciseOrder()).isEqualTo(EXERCISE_ORDER);
        assertThat(entity.getExercise()).isEqualTo(exercise);
        assertThat(entity.getTrainingSession()).isEqualTo(trainingSession);
        assertThat(entity.getCreatedAt()).isEqualTo(NOW);
        assertThat(entity.getUnitOfMeasurement().name()).isEqualTo(UNIT_KILOMETERS);
    }

    @Test
    void toDtoShouldMapEntityToDto() {
        // Given
        Exercise exercise = new Exercise(EXERCISE_ID, EXERCISE_NAME, DESC, EXERCISE_TYPE, EXERCISE_IMAGE, NOW);
        User user = new User(USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_BIRTH, USER_EMAIL, USER_PASSWORD, USER_ROLE);
        TrainingSession trainingSession = new TrainingSession(
            TRAINING_SESSION_ID, TRAINING_SESSION_NAME, DESC, TRAINING_SESSION_STATUS, user, NOW
        );
        SessionExercise entity = new SessionExercise(
            1L, ROUNDS, SETS, REPETITIONS, SPRINTS, TIME, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, SessionExercise.Status.valueOf(STATUS), EXERCISE_ORDER, exercise, trainingSession, NOW,
            SessionExercise.UnitOfMeasurement.valueOf(UNIT_KILOMETERS)
        );

        // When
        SessionExerciseDTO dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRounds()).isEqualTo(ROUNDS);
        assertThat(dto.getSets()).isEqualTo(SETS);
        assertThat(dto.getRepetitions()).isEqualTo(REPETITIONS);
        assertThat(dto.getSprints()).isEqualTo(SPRINTS);
        assertThat(dto.getTime()).isEqualTo(TIME);
        assertThat(dto.getWeight()).isEqualTo(WEIGHT);
        assertThat(dto.getDistance()).isEqualTo(DISTANCE);
        assertThat(dto.getStartTime()).isEqualTo(NOW);
        assertThat(dto.getEndTime()).isEqualTo(NOW.plusHours(1));
        assertThat(dto.getRestTime()).isEqualTo(REST_TIME);
        assertThat(dto.getStatus()).isEqualTo(STATUS);
        assertThat(dto.getExerciseOrder()).isEqualTo(EXERCISE_ORDER);
        assertThat(dto.getExerciseId()).isEqualTo(EXERCISE_ID);
        assertThat(dto.getTrainingSessionId()).isEqualTo(TRAINING_SESSION_ID);
        assertThat(dto.getCreatedAt()).isEqualTo(NOW);
        assertThat(dto.getUnitOfMeasurement()).isEqualTo(UNIT_KILOMETERS);
    }

    @Test
    void toEntityShouldHandleNullUnitOfMeasurement() {
        // Given
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, TIME, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, NOW, null
        );
        Exercise exercise = new Exercise(EXERCISE_ID, EXERCISE_NAME, DESC, EXERCISE_TYPE, EXERCISE_IMAGE, NOW);
        User user = new User(USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_BIRTH, USER_EMAIL, USER_PASSWORD, USER_ROLE);
        TrainingSession trainingSession = new TrainingSession(
            TRAINING_SESSION_ID, TRAINING_SESSION_NAME, DESC, TRAINING_SESSION_STATUS, user, NOW
        );

        // When
        SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

        // Then
        assertThat(entity.getUnitOfMeasurement()).isNull();
    }
}
