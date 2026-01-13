package com.rvladimir.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for TrainingSessionMapper.
 */
class TrainingSessionMapperTest {

    private static final String TEST_NAME = "Morning Workout";
    private static final String TEST_DESCRIPTION = "A quick morning workout routine";
    private static final String TEST_NAME_EVENING = "Evening Workout";
    private static final String TEST_DESCRIPTION_EVENING = "An evening cardio session";
    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long TRAINING_SESSION_ID = 1L;
    private static final int YEAR_2026 = 2026;
    private static final Month JANUARY = Month.JANUARY;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int MINUTE_30 = 30;
    private static final int HOUR_18 = 18;
    private static final int MINUTE_0 = 0;

    private TrainingSessionMapper trainingSessionMapper;

    @BeforeEach
    void setUp() {
        trainingSessionMapper = new TrainingSessionMapper();
    }

    @Test
    void testToEntityFromCreateTrainingSessionDTO() {
        // Given
        CreateTrainingSessionDTO createTrainingSessionDTO = new CreateTrainingSessionDTO(USER_ID_1);

        // When
        TrainingSession trainingSession = trainingSessionMapper.toEntity(createTrainingSessionDTO);

        // Then
        assertThat(trainingSession).isNotNull();
        assertThat(trainingSession.getId()).isNull();
        assertThat(trainingSession.getUser()).isNotNull();
        assertThat(trainingSession.getUser().getId()).isEqualTo(USER_ID_1);
    }

    @Test
    void testToEntityFromCreateTrainingSessionDTOWhenNullReturnsNull() {
        // When
        TrainingSession trainingSession = trainingSessionMapper.toEntity(null);

        // Then
        assertThat(trainingSession).isNull();
    }

    @Test
    void testToDto() {
        // Given
        User user = new User();
        user.setId(USER_ID_1);

        LocalDateTime createdAt = LocalDateTime.of(YEAR_2026, JANUARY, DAY_13, HOUR_10, MINUTE_30);

        TrainingSession trainingSession = new TrainingSession(
            TRAINING_SESSION_ID,
            TEST_NAME,
            TEST_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            createdAt
        );

        // When
        TrainingSessionDTO trainingSessionDTO = trainingSessionMapper.toDto(trainingSession);

        // Then
        assertThat(trainingSessionDTO).isNotNull();
        assertThat(trainingSessionDTO.getId()).isEqualTo(TRAINING_SESSION_ID);
        assertThat(trainingSessionDTO.getName()).isEqualTo(TEST_NAME);
        assertThat(trainingSessionDTO.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(trainingSessionDTO.getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(trainingSessionDTO.getUserId()).isEqualTo(USER_ID_1);
        assertThat(trainingSessionDTO.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testToDtoWhenNullReturnsNull() {
        // When
        TrainingSessionDTO trainingSessionDTO = trainingSessionMapper.toDto(null);

        // Then
        assertThat(trainingSessionDTO).isNull();
    }

    @Test
    void testToDtoWithDifferentStatus() {
        // Given
        User user = new User();
        user.setId(USER_ID_2);

        LocalDateTime createdAt = LocalDateTime.of(YEAR_2026, JANUARY, DAY_13, HOUR_18, MINUTE_0);

        TrainingSession trainingSession = new TrainingSession(
            null,
            TEST_NAME_EVENING,
            TEST_DESCRIPTION_EVENING,
            TrainingSession.Status.FINISHED,
            user,
            createdAt
        );

        // When
        TrainingSessionDTO trainingSessionDTO = trainingSessionMapper.toDto(trainingSession);

        // Then
        assertThat(trainingSessionDTO).isNotNull();
        assertThat(trainingSessionDTO.getStatus()).isEqualTo(TrainingSession.Status.FINISHED);
        assertThat(trainingSessionDTO.getName()).isEqualTo(TEST_NAME_EVENING);
        assertThat(trainingSessionDTO.getDescription()).isEqualTo(TEST_DESCRIPTION_EVENING);
        assertThat(trainingSessionDTO.getUserId()).isEqualTo(USER_ID_2);
    }

    @Test
    void testToDtoWithNullNameAndDescription() {
        // Given
        User user = new User();
        user.setId(USER_ID_1);

        LocalDateTime createdAt = LocalDateTime.of(YEAR_2026, JANUARY, DAY_13, HOUR_10, MINUTE_0);

        TrainingSession trainingSession = new TrainingSession(
            TRAINING_SESSION_ID,
            null,
            null,
            TrainingSession.Status.IS_TEMPLATE,
            user,
            createdAt
        );

        // When
        TrainingSessionDTO trainingSessionDTO = trainingSessionMapper.toDto(trainingSession);

        // Then
        assertThat(trainingSessionDTO).isNotNull();
        assertThat(trainingSessionDTO.getName()).isNull();
        assertThat(trainingSessionDTO.getDescription()).isNull();
        assertThat(trainingSessionDTO.getStatus()).isEqualTo(TrainingSession.Status.IS_TEMPLATE);
    }
}
