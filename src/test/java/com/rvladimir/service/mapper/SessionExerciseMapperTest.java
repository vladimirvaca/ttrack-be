package com.rvladimir.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
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
    private static final LocalTime DURATION = LocalTime.of(0, 45);
    private static final double WEIGHT = 80.0;
    private static final double DISTANCE = 200.0;
    private static final int REST_TIME = 90;
    private static final SessionExercise.Status STATUS = SessionExercise.Status.STARTED;
    private static final int EXERCISE_ORDER = 2;
    private static final long EXERCISE_ID = 10L;
    private static final long TRAINING_SESSION_ID = 20L;
    private static final SessionExercise.UnitOfMeasurement UNIT_KILOMETERS =
        SessionExercise.UnitOfMeasurement.KILOMETERS;
    private static final TypeOfExercise TYPE_OF_EXERCISE = TypeOfExercise.BOXING_BAG;
    private static final String NOTES = "Felt strong today";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final long USER_ID = 100L;
    private static final String EXERCISE_NAME = "Bench Press";
    private static final TypeOfExercise EXERCISE_TYPE = TypeOfExercise.STRENGTH;
    private static final String EXERCISE_IMAGE = "img.png";
    private static final String USER_FIRSTNAME = "John";
    private static final String USER_LASTNAME = "Doe";
    private static final String USER_NICKNAME = "johnd";
    private static final LocalDate USER_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String USER_EMAIL = "john@example.com";
    private static final String USER_PASSWORD = "pass";
    private static final User.Role USER_ROLE = User.Role.USER;
    private static final String TRAINING_SESSION_NAME = "Morning Session";
    private static final String DESC = "desc";
    private static final TrainingSession.Status TRAINING_SESSION_STATUS = TrainingSession.Status.STARTED;

    private final SessionExerciseMapper mapper = new SessionExerciseMapper();

    private Exercise buildExercise() {
        return new Exercise(EXERCISE_ID, EXERCISE_NAME, DESC, EXERCISE_TYPE, EXERCISE_IMAGE, NOW);
    }

    private TrainingSession buildTrainingSession() {
        User user = new User(
            USER_ID, USER_FIRSTNAME, USER_LASTNAME, USER_NICKNAME, USER_BIRTH, USER_EMAIL, USER_PASSWORD, USER_ROLE);
        return new TrainingSession(
            TRAINING_SESSION_ID, TRAINING_SESSION_NAME, DESC, TRAINING_SESSION_STATUS, user, NOW);
    }

    @Test
    void toEntityShouldMapDtoToEntity() {
        // Given
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, UNIT_KILOMETERS, TYPE_OF_EXERCISE, NOTES
        );
        Exercise exercise = buildExercise();
        TrainingSession trainingSession = buildTrainingSession();

        // When
        SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

        // Then
        assertThat(entity.getRounds()).isEqualTo(ROUNDS);
        assertThat(entity.getSets()).isEqualTo(SETS);
        assertThat(entity.getRepetitions()).isEqualTo(REPETITIONS);
        assertThat(entity.getSprints()).isEqualTo(SPRINTS);
        assertThat(entity.getDuration()).isEqualTo(DURATION);
        assertThat(entity.getWeight()).isEqualTo(WEIGHT);
        assertThat(entity.getDistance()).isEqualTo(DISTANCE);
        assertThat(entity.getStartTime()).isEqualTo(NOW);
        assertThat(entity.getEndTime()).isEqualTo(NOW.plusHours(1));
        assertThat(entity.getRestTime()).isEqualTo(REST_TIME);
        assertThat(entity.getStatus()).isEqualTo(STATUS);
        assertThat(entity.getExerciseOrder()).isEqualTo(EXERCISE_ORDER);
        assertThat(entity.getExercise()).isEqualTo(exercise);
        assertThat(entity.getTrainingSession()).isEqualTo(trainingSession);
        assertThat(entity.getCreatedAt()).isNotNull().isBeforeOrEqualTo(LocalDateTime.now());
        assertThat(entity.getUnitOfMeasurement()).isEqualTo(UNIT_KILOMETERS);
        assertThat(entity.getTypeOfExercise()).isEqualTo(TYPE_OF_EXERCISE);
        assertThat(entity.getNotes()).isEqualTo(NOTES);
    }

    @Test
    void toEntityShouldSetCreatedAtServerSide() {
        // Given
        LocalDateTime before = LocalDateTime.now();
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, UNIT_KILOMETERS, TYPE_OF_EXERCISE, null
        );

        // When
        SessionExercise entity = mapper.toEntity(dto, buildExercise(), buildTrainingSession());
        LocalDateTime after = LocalDateTime.now();

        // Then
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
    }

    @Test
    void toDtoShouldMapEntityToDto() {
        // Given
        Exercise exercise = buildExercise();
        TrainingSession trainingSession = buildTrainingSession();
        SessionExercise entity = new SessionExercise(
            1L, ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, exercise, trainingSession, NOW,
            UNIT_KILOMETERS, TYPE_OF_EXERCISE, NOTES
        );

        // When
        SessionExerciseDTO dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getRounds()).isEqualTo(ROUNDS);
        assertThat(dto.getSets()).isEqualTo(SETS);
        assertThat(dto.getRepetitions()).isEqualTo(REPETITIONS);
        assertThat(dto.getSprints()).isEqualTo(SPRINTS);
        assertThat(dto.getDuration()).isEqualTo(DURATION);
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
        assertThat(dto.getTypeOfExercise()).isEqualTo(TYPE_OF_EXERCISE);
        assertThat(dto.getNotes()).isEqualTo(NOTES);
    }

    @Test
    void toEntityShouldHandleNullOptionalFields() {
        // Given
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, null, null, null
        );
        Exercise exercise = buildExercise();
        TrainingSession trainingSession = buildTrainingSession();

        // When
        SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

        // Then
        assertThat(entity.getUnitOfMeasurement()).isNull();
        assertThat(entity.getTypeOfExercise()).isNull();
        assertThat(entity.getNotes()).isNull();
    }

    @Test
    void toEntityShouldHandleNullStatus() {
        // Given
        CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
            ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, null, EXERCISE_ORDER, EXERCISE_ID, UNIT_KILOMETERS, TYPE_OF_EXERCISE, null
        );
        Exercise exercise = buildExercise();
        TrainingSession trainingSession = buildTrainingSession();

        // When
        SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

        // Then
        assertThat(entity.getStatus()).isNull();
    }

    @Test
    void toDtoShouldHandleNullStatusAndNullTypeOfExercise() {
        // Given
        Exercise exercise = buildExercise();
        TrainingSession trainingSession = buildTrainingSession();
        SessionExercise entity = new SessionExercise(
            1L, ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, null, EXERCISE_ORDER, exercise, trainingSession, NOW, null, null, null
        );

        // When
        SessionExerciseDTO dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getStatus()).isNull();
        assertThat(dto.getTypeOfExercise()).isNull();
        assertThat(dto.getUnitOfMeasurement()).isNull();
        assertThat(dto.getNotes()).isNull();
    }

    @Test
    void toDtoShouldHandleNullExerciseAndTrainingSession() {
        // Given
        SessionExercise entity = new SessionExercise(
            1L, ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
            REST_TIME, SessionExercise.Status.STARTED, EXERCISE_ORDER, null, null, NOW, null, null, null
        );

        // When
        SessionExerciseDTO dto = mapper.toDto(entity);

        // Then
        assertThat(dto.getExerciseId()).isNull();
        assertThat(dto.getTrainingSessionId()).isNull();
    }

    @Test
    void toEntityShouldMapAllTypeOfExerciseValues() {
        for (TypeOfExercise type : TypeOfExercise.values()) {
            // Given
            CreateSessionExerciseDTO dto = new CreateSessionExerciseDTO(
                ROUNDS, SETS, REPETITIONS, SPRINTS, DURATION, WEIGHT, DISTANCE, NOW, NOW.plusHours(1),
                REST_TIME, STATUS, EXERCISE_ORDER, EXERCISE_ID, UNIT_KILOMETERS, type, null
            );
            Exercise exercise = buildExercise();
            TrainingSession trainingSession = buildTrainingSession();

            // When
            SessionExercise entity = mapper.toEntity(dto, exercise, trainingSession);

            // Then
            assertThat(entity.getTypeOfExercise()).isEqualTo(type);
        }
    }
}
