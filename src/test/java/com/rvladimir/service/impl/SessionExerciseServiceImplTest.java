package com.rvladimir.service.impl;

import static com.rvladimir.service.impl.SessionExerciseTestHelper.createCreateSessionExerciseDTO;
import static com.rvladimir.service.impl.SessionExerciseTestHelper.createSessionExerciseDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;
import com.rvladimir.service.mapper.SessionExerciseMapper;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for SessionExerciseServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class SessionExerciseServiceImplTest {

    @Mock
    private SessionExerciseRepository sessionExerciseRepository;
    @Mock
    private ExerciseRepository exerciseRepository;
    @Mock
    private TrainingSessionRepository trainingSessionRepository;
    @Mock
    private SessionExerciseMapper sessionExerciseMapper;

    @InjectMocks
    private SessionExerciseServiceImpl sessionExerciseService;

    private CreateSessionExerciseDTO createDto;
    private SessionExercise sessionExercise;
    private SessionExerciseDTO sessionExerciseDTO;
    private Exercise exercise;
    private TrainingSession trainingSession;

    @BeforeEach
    void setUp() {
        exercise = new Exercise();
        exercise.setId(SessionExerciseTestHelper.EXERCISE_ID);
        trainingSession = new TrainingSession();
        trainingSession.setId(SessionExerciseTestHelper.TRAINING_SESSION_ID);
        createDto = createCreateSessionExerciseDTO();
        sessionExercise = new SessionExercise();
        sessionExerciseDTO = createSessionExerciseDTO();
    }

    @Test
    void testCreateSessionExerciseSuccess() {
        // Given
        when(exerciseRepository.findById(SessionExerciseTestHelper.EXERCISE_ID)).thenReturn(Optional.of(exercise));
        when(trainingSessionRepository.findById(SessionExerciseTestHelper.TRAINING_SESSION_ID))
            .thenReturn(Optional.of(trainingSession));
        when(sessionExerciseMapper.toEntity(createDto, exercise, trainingSession)).thenReturn(sessionExercise);
        when(sessionExerciseRepository.save(any(SessionExercise.class))).thenReturn(sessionExercise);
        when(sessionExerciseMapper.toDto(sessionExercise)).thenReturn(sessionExerciseDTO);

        // When
        SessionExerciseDTO result =
            sessionExerciseService.createSessionExercise(SessionExerciseTestHelper.TRAINING_SESSION_ID, createDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(exerciseRepository).findById(SessionExerciseTestHelper.EXERCISE_ID);
        verify(trainingSessionRepository).findById(SessionExerciseTestHelper.TRAINING_SESSION_ID);
        verify(sessionExerciseMapper).toEntity(createDto, exercise, trainingSession);
        verify(sessionExerciseRepository).save(any(SessionExercise.class));
        verify(sessionExerciseMapper).toDto(sessionExercise);
    }

    @Test
    void testCreateSessionExerciseNotFound() {
        // Given
        when(exerciseRepository.findById(SessionExerciseTestHelper.EXERCISE_ID)).thenReturn(Optional.empty());
        when(trainingSessionRepository.findById(SessionExerciseTestHelper.TRAINING_SESSION_ID))
            .thenReturn(Optional.of(trainingSession));
        // When & Then
        Assertions.assertThrows(IllegalArgumentException.class, () ->
            sessionExerciseService.createSessionExercise(SessionExerciseTestHelper.TRAINING_SESSION_ID, createDto));
    }
}
