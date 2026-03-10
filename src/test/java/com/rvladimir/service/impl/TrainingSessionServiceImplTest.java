package com.rvladimir.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
import com.rvladimir.domain.User;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.repository.UserRepository;
import com.rvladimir.service.dto.CreateTrainingSessionDTO;
import com.rvladimir.service.dto.QuickStartIntervalDTO;
import com.rvladimir.service.dto.QuickStartIntervalResponseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;
import com.rvladimir.service.dto.TrainingSessionDTO;
import com.rvladimir.service.mapper.SessionExerciseMapper;
import com.rvladimir.service.mapper.TrainingSessionMapper;
import com.rvladimir.web.error.ValidationException;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private static final long SESSION_EXERCISE_ID = 10L;
    private static final String TEST_NAME = "Morning Workout";
    private static final String TEST_DESCRIPTION = "A quick morning workout routine";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String VALIDATION_FIELD_USER_ID = "userId";
    private static final String VALIDATION_CODE_NOT_FOUND = "NOT_FOUND";
    private static final TypeOfExercise TYPE_HIIT = TypeOfExercise.HIIT;
    private static final TypeOfExercise TYPE_BOXING_BAG = TypeOfExercise.BOXING_BAG;
    private static final SessionExercise.Status STATUS_STARTED = SessionExercise.Status.STARTED;
    private static final String QUICK_START_HIIT = "Quick Start – HIIT";
    private static final String QUICK_START_INTERVAL = "Quick Start – INTERVAL";
    private static final String QUICK_START_BOXING_BAG = "Quick Start – BOXING_BAG";
    private static final String NOTES_FOCUS_ON_SPEED = "Focus on speed";
    private static final int YEAR_2026 = 2026;
    private static final int MONTH_JANUARY = 1;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int MINUTE_30 = 30;
    private static final int TEST_ROUNDS = 5;
    private static final int TEST_SPRINTS = 10;
    private static final int TEST_REST_TIME = 60;
    private static final LocalTime TEST_DURATION = LocalTime.of(0, 3);

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingSessionMapper trainingSessionMapper;

    @Mock
    private SessionExerciseRepository sessionExerciseRepository;

    @Mock
    private SessionExerciseMapper sessionExerciseMapper;

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
                assertThat(validationEx.getField()).isEqualTo(VALIDATION_FIELD_USER_ID);
                assertThat(validationEx.getCode()).isEqualTo(VALIDATION_CODE_NOT_FOUND);
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

    @Test
    void testQuickStartIntervalSuccess() {
        // Given
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            USER_ID_1, TYPE_HIIT, TEST_ROUNDS, TEST_SPRINTS, TEST_DURATION, TEST_REST_TIME, null, null
        );

        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        User user = new User();
        user.setId(USER_ID_1);
        TrainingSession savedSession = new TrainingSession(
            TRAINING_SESSION_ID, QUICK_START_HIIT, null,
            TrainingSession.Status.STARTED, user,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(savedSession);

        SessionExercise savedExercise = new SessionExercise();
        savedExercise.setId(SESSION_EXERCISE_ID);
        savedExercise.setRounds(TEST_ROUNDS);
        savedExercise.setSprints(TEST_SPRINTS);
        savedExercise.setDuration(TEST_DURATION);
        savedExercise.setRestTime(TEST_REST_TIME);
        savedExercise.setStatus(SessionExercise.Status.STARTED);
        savedExercise.setTypeOfExercise(TypeOfExercise.HIIT);
        savedExercise.setTrainingSession(savedSession);
        when(sessionExerciseRepository.save(any(SessionExercise.class))).thenReturn(savedExercise);

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO(
            TRAINING_SESSION_ID, QUICK_START_HIIT, null,
            TrainingSession.Status.STARTED, USER_ID_1,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30)
        );
        when(trainingSessionMapper.toDto(savedSession)).thenReturn(sessionDTO);

        SessionExerciseDTO exerciseDTO = new SessionExerciseDTO(
            SESSION_EXERCISE_ID, TEST_ROUNDS, null, null, TEST_SPRINTS, TEST_DURATION,
            null, null, null, null, TEST_REST_TIME, STATUS_STARTED, 1,
            null, TRAINING_SESSION_ID,
            LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30),
            null, TYPE_HIIT, null
        );
        when(sessionExerciseMapper.toDto(savedExercise)).thenReturn(exerciseDTO);

        // When
        QuickStartIntervalResponseDTO result = trainingSessionService.quickStartInterval(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTrainingSession()).isNotNull();
        assertThat(result.getTrainingSession().getId()).isEqualTo(TRAINING_SESSION_ID);
        assertThat(result.getTrainingSession().getName()).isEqualTo(QUICK_START_HIIT);
        assertThat(result.getTrainingSession().getStatus()).isEqualTo(TrainingSession.Status.STARTED);
        assertThat(result.getSessionExercise()).isNotNull();
        assertThat(result.getSessionExercise().getId()).isEqualTo(SESSION_EXERCISE_ID);
        assertThat(result.getSessionExercise().getRounds()).isEqualTo(TEST_ROUNDS);
        assertThat(result.getSessionExercise().getTypeOfExercise()).isEqualTo(TYPE_HIIT);

        verify(userRepository).existsById(USER_ID_1);
        verify(trainingSessionRepository).save(any(TrainingSession.class));
        verify(sessionExerciseRepository).save(any(SessionExercise.class));
        verify(trainingSessionMapper).toDto(savedSession);
        verify(sessionExerciseMapper).toDto(savedExercise);
    }

    @Test
    void testQuickStartIntervalUserNotFound() {
        // Given
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            USER_ID_2, TYPE_HIIT, TEST_ROUNDS, TEST_SPRINTS, TEST_DURATION, TEST_REST_TIME, null, null
        );
        when(userRepository.existsById(USER_ID_2)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> trainingSessionService.quickStartInterval(dto))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining(USER_NOT_FOUND_MESSAGE)
            .satisfies(ex -> {
                ValidationException validationEx = (ValidationException) ex;
                assertThat(validationEx.getField()).isEqualTo(VALIDATION_FIELD_USER_ID);
                assertThat(validationEx.getCode()).isEqualTo(VALIDATION_CODE_NOT_FOUND);
            });

        verify(userRepository).existsById(USER_ID_2);
        verify(trainingSessionRepository, never()).save(any());
        verify(sessionExerciseRepository, never()).save(any());
    }

    @Test
    void testQuickStartIntervalWithNullTypeOfExercise() {
        // Given — typeOfExercise is null, session name should default to "INTERVAL"
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            USER_ID_1, null, TEST_ROUNDS, null, null, TEST_REST_TIME, null, null
        );
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        User user = new User();
        user.setId(USER_ID_1);
        TrainingSession savedSession = new TrainingSession(
            TRAINING_SESSION_ID, QUICK_START_INTERVAL, null,
            TrainingSession.Status.STARTED, user, LocalDateTime.now()
        );
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(savedSession);

        SessionExercise savedExercise = new SessionExercise();
        savedExercise.setId(SESSION_EXERCISE_ID);
        savedExercise.setRounds(TEST_ROUNDS);
        savedExercise.setTrainingSession(savedSession);
        when(sessionExerciseRepository.save(any(SessionExercise.class))).thenReturn(savedExercise);

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO(
            TRAINING_SESSION_ID, QUICK_START_INTERVAL, null,
            TrainingSession.Status.STARTED, USER_ID_1, LocalDateTime.now()
        );
        when(trainingSessionMapper.toDto(savedSession)).thenReturn(sessionDTO);

        SessionExerciseDTO exerciseDTO = new SessionExerciseDTO(
            SESSION_EXERCISE_ID, TEST_ROUNDS, null, null, null, null,
            null, null, null, null, TEST_REST_TIME, STATUS_STARTED, 1,
            null, TRAINING_SESSION_ID, LocalDateTime.now(), null, null, null
        );
        when(sessionExerciseMapper.toDto(savedExercise)).thenReturn(exerciseDTO);

        // When
        QuickStartIntervalResponseDTO result = trainingSessionService.quickStartInterval(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTrainingSession().getName()).isEqualTo(QUICK_START_INTERVAL);
        assertThat(result.getSessionExercise().getTypeOfExercise()).isNull();

        verify(trainingSessionRepository).save(any(TrainingSession.class));
        verify(sessionExerciseRepository).save(any(SessionExercise.class));
    }

    @Test
    void testQuickStartIntervalWithBoxingBagType() {
        // Given
        QuickStartIntervalDTO dto = new QuickStartIntervalDTO(
            USER_ID_1, TYPE_BOXING_BAG, TEST_ROUNDS, TEST_SPRINTS, TEST_DURATION, TEST_REST_TIME, null,
            NOTES_FOCUS_ON_SPEED
        );
        when(userRepository.existsById(USER_ID_1)).thenReturn(true);

        User user = new User();
        user.setId(USER_ID_1);
        TrainingSession savedSession = new TrainingSession(
            TRAINING_SESSION_ID, QUICK_START_BOXING_BAG, null,
            TrainingSession.Status.STARTED, user, LocalDateTime.now()
        );
        when(trainingSessionRepository.save(any(TrainingSession.class))).thenReturn(savedSession);

        SessionExercise savedExercise = new SessionExercise();
        savedExercise.setId(SESSION_EXERCISE_ID);
        savedExercise.setTypeOfExercise(TypeOfExercise.BOXING_BAG);
        savedExercise.setNotes(NOTES_FOCUS_ON_SPEED);
        savedExercise.setTrainingSession(savedSession);
        when(sessionExerciseRepository.save(any(SessionExercise.class))).thenReturn(savedExercise);

        TrainingSessionDTO sessionDTO = new TrainingSessionDTO(
            TRAINING_SESSION_ID, QUICK_START_BOXING_BAG, null,
            TrainingSession.Status.STARTED, USER_ID_1, LocalDateTime.now()
        );
        when(trainingSessionMapper.toDto(savedSession)).thenReturn(sessionDTO);

        SessionExerciseDTO exerciseDTO = new SessionExerciseDTO(
            SESSION_EXERCISE_ID, TEST_ROUNDS, null, null, TEST_SPRINTS, TEST_DURATION,
            null, null, null, null, TEST_REST_TIME, STATUS_STARTED, 1,
            null, TRAINING_SESSION_ID, LocalDateTime.now(), null, TYPE_BOXING_BAG, NOTES_FOCUS_ON_SPEED
        );
        when(sessionExerciseMapper.toDto(savedExercise)).thenReturn(exerciseDTO);

        // When
        QuickStartIntervalResponseDTO result = trainingSessionService.quickStartInterval(dto);

        // Then
        assertThat(result.getTrainingSession().getName()).isEqualTo(QUICK_START_BOXING_BAG);
        assertThat(result.getSessionExercise().getTypeOfExercise()).isEqualTo(TYPE_BOXING_BAG);
        assertThat(result.getSessionExercise().getNotes()).isEqualTo(NOTES_FOCUS_ON_SPEED);
    }
}
