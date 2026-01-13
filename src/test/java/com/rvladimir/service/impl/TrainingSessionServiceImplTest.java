package com.rvladimir.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.User;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.service.mapper.TrainingSessionMapper;
import com.rvladimir.web.error.ValidationException;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for TrainingSessionServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceImplTest {

    private static final long USER_ID_1 = 1L;
    private static final long USER_ID_2 = 2L;
    private static final long TRAINING_SESSION_ID = 1L;
    private static final String TEST_NAME = "Morning Workout";
    private static final String TEST_DESCRIPTION = "A quick morning workout routine";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final int YEAR_2026 = 2026;
    private static final int MONTH_JANUARY = 1;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int MINUTE_30 = 30;

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingSessionMapper trainingSessionMapper;

    @InjectMocks
    private TrainingSessionServiceImpl trainingSessionService;

    private CreateTrainingSessionDTO createTrainingSessionDTO;
    private TrainingSession trainingSession;
    private TrainingSessionDTO trainingSessionDTO;

    @BeforeEach
    void setUp() {
        createTrainingSessionDTO = new CreateTrainingSessionDTO(USER_ID_1);

        User user = new User();
        user.setId(USER_ID_1);

        trainingSession = new TrainingSession();
        trainingSession.setId(null);
        trainingSession.setUser(user);

        LocalDateTime createdAt = LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30);

        trainingSessionDTO = new TrainingSessionDTO(
            TRAINING_SESSION_ID,
            TEST_NAME,
            TEST_DESCRIPTION,
            TrainingSession.Status.STARTED,
            USER_ID_1,
            createdAt
        );
    }

    @Test
    void testCreateSuccess() {
        // Given
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);
        when(trainingSessionMapper.toEntity(createTrainingSessionDTO)).thenReturn(trainingSession);

        User user = new User();
        user.setId(USER_ID_1);

        TrainingSession savedTrainingSession = new TrainingSession(
            TRAINING_SESSION_ID,
            TEST_NAME,
            TEST_DESCRIPTION,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(savedTrainingSession);
        when(trainingSessionMapper.toDto(savedTrainingSession)).thenReturn(trainingSessionDTO);

        // When
        TrainingSessionDTO result = trainingSessionService.create(createTrainingSessionDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TRAINING_SESSION_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID_1);
        assertThat(result.getName()).isEqualTo(TEST_NAME);
        assertThat(result.getStatus()).isEqualTo(TrainingSession.Status.STARTED);

        verify(userRepository).existsById(USER_ID_1);
        verify(trainingSessionMapper).toEntity(createTrainingSessionDTO);
        verify(trainingSessionRepository).save(any(TrainingSession.class));
        verify(trainingSessionMapper).toDto(savedTrainingSession);
    }

    @Test
    void testCreateThrowsValidationExceptionWhenUserNotFound() {
        // Given
        when(userRepository.existsById(USER_ID_2)).thenReturn(false);

        CreateTrainingSessionDTO dto = new CreateTrainingSessionDTO(USER_ID_2);

        // When & Then
        assertThatThrownBy(() -> trainingSessionService.create(dto))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining(USER_NOT_FOUND_MESSAGE)
            .satisfies(ex -> {
                ValidationException validationEx = (ValidationException) ex;
                assertThat(validationEx.getField()).isEqualTo("userId");
                assertThat(validationEx.getCode()).isEqualTo("NOT_FOUND");
            });

        verify(userRepository).existsById(USER_ID_2);
        verify(trainingSessionMapper, never()).toEntity(any());
        verify(trainingSessionRepository, never()).save(any());
    }

    @Test
    void testCreateWithDifferentUser() {
        // Given
        CreateTrainingSessionDTO dto = new CreateTrainingSessionDTO(USER_ID_2);

        User user = new User();
        user.setId(USER_ID_2);

        TrainingSession session = new TrainingSession();
        session.setUser(user);

        when(userRepository.existsById(USER_ID_2)).thenReturn(true);
        when(trainingSessionMapper.toEntity(dto)).thenReturn(session);

        TrainingSession savedSession = new TrainingSession(
            TRAINING_SESSION_ID,
            null,
            null,
            TrainingSession.Status.STARTED,
            user,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(savedSession);

        TrainingSessionDTO resultDto = new TrainingSessionDTO(
            TRAINING_SESSION_ID,
            null,
            null,
            TrainingSession.Status.STARTED,
            USER_ID_2,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );
        when(trainingSessionMapper.toDto(savedSession)).thenReturn(resultDto);

        // When
        TrainingSessionDTO result = trainingSessionService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(USER_ID_2);
        verify(userRepository).existsById(USER_ID_2);
    }
}
