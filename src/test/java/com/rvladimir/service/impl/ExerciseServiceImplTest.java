package com.rvladimir.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rvladimir.domain.Exercise;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.service.dto.ExerciseDTO;
import com.rvladimir.service.mapper.ExerciseMapper;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for ExerciseServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class ExerciseServiceImplTest {

    private static final long EXERCISE_ID = 1L;
    private static final long EXERCISE_ID_2 = 2L;
    private static final String EXERCISE_NAME = "Push-ups";
    private static final String EXERCISE_DESCRIPTION = "Upper body strength exercise";
    private static final Exercise.Type EXERCISE_TYPE = Exercise.Type.STRENGTH;
    private static final String EXERCISE_IMAGE = "https://example.com/pushups.jpg";
    private static final String DIFFERENT_NAME = "Squats";
    private static final String DIFFERENT_DESCRIPTION = "Lower body strength exercise";
    private static final Exercise.Type DIFFERENT_TYPE = Exercise.Type.BALANCE;
    private static final String DIFFERENT_IMAGE = "https://example.com/squats.jpg";
    private static final int YEAR_2026 = 2026;
    private static final int MONTH_JANUARY = 1;
    private static final int DAY_13 = 13;
    private static final int HOUR_10 = 10;
    private static final int MINUTE_30 = 30;
    private static final int PAGE_SIZE = 10;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ExerciseMapper exerciseMapper;

    @InjectMocks
    private ExerciseServiceImpl exerciseService;

    private ExerciseDTO exerciseDTO;
    private Exercise exercise;
    private LocalDateTime createdAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(YEAR_2026, MONTH_JANUARY, DAY_13, HOUR_10, MINUTE_30);

        exerciseDTO = new ExerciseDTO(
            null,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            EXERCISE_TYPE,
            EXERCISE_IMAGE
        );

        exercise = new Exercise();
        exercise.setId(null);
        exercise.setName(EXERCISE_NAME);
        exercise.setDescription(EXERCISE_DESCRIPTION);
        exercise.setType(EXERCISE_TYPE);
        exercise.setImage(EXERCISE_IMAGE);
    }

    @Test
    void testCreateSuccess() {
        // Given
        Exercise savedExercise = new Exercise(
            EXERCISE_ID,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            EXERCISE_TYPE,
            EXERCISE_IMAGE,
            createdAt
        );

        ExerciseDTO resultDTO = new ExerciseDTO(
            EXERCISE_ID,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            EXERCISE_TYPE,
            EXERCISE_IMAGE
        );

        when(exerciseMapper.toEntity(exerciseDTO)).thenReturn(exercise);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(savedExercise);
        when(exerciseMapper.toDto(savedExercise)).thenReturn(resultDTO);

        // When
        ExerciseDTO result = exerciseService.create(exerciseDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(EXERCISE_ID);
        assertThat(result.getName()).isEqualTo(EXERCISE_NAME);
        assertThat(result.getDescription()).isEqualTo(EXERCISE_DESCRIPTION);
        assertThat(result.getType()).isEqualTo(EXERCISE_TYPE);
        assertThat(result.getImage()).isEqualTo(EXERCISE_IMAGE);

        verify(exerciseMapper).toEntity(exerciseDTO);
        verify(exerciseRepository).save(any(Exercise.class));
        verify(exerciseMapper).toDto(savedExercise);
    }

    @Test
    void testCreateWithDifferentValues() {
        // Given
        ExerciseDTO dto = new ExerciseDTO(
            null,
            DIFFERENT_NAME,
            DIFFERENT_DESCRIPTION,
            DIFFERENT_TYPE,
            DIFFERENT_IMAGE
        );

        Exercise inputExercise = new Exercise();
        inputExercise.setName(DIFFERENT_NAME);
        inputExercise.setDescription(DIFFERENT_DESCRIPTION);
        inputExercise.setType(DIFFERENT_TYPE);
        inputExercise.setImage(DIFFERENT_IMAGE);

        Exercise savedExercise = new Exercise(
            EXERCISE_ID,
            DIFFERENT_NAME,
            DIFFERENT_DESCRIPTION,
            DIFFERENT_TYPE,
            DIFFERENT_IMAGE,
            createdAt
        );

        ExerciseDTO resultDTO = new ExerciseDTO(
            EXERCISE_ID,
            DIFFERENT_NAME,
            DIFFERENT_DESCRIPTION,
            DIFFERENT_TYPE,
            DIFFERENT_IMAGE
        );

        when(exerciseMapper.toEntity(dto)).thenReturn(inputExercise);
        when(exerciseRepository.save(any(Exercise.class))).thenReturn(savedExercise);
        when(exerciseMapper.toDto(savedExercise)).thenReturn(resultDTO);

        // When
        ExerciseDTO result = exerciseService.create(dto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(EXERCISE_ID);
        assertThat(result.getName()).isEqualTo(DIFFERENT_NAME);
        assertThat(result.getType()).isEqualTo(DIFFERENT_TYPE);

        verify(exerciseMapper).toEntity(dto);
        verify(exerciseRepository).save(any(Exercise.class));
        verify(exerciseMapper).toDto(savedExercise);
    }

    @Test
    void testGetAllSuccess() {
        // Given
        Pageable pageable = Pageable.from(0, PAGE_SIZE);

        Exercise exercise1 = new Exercise(
            EXERCISE_ID,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            EXERCISE_TYPE,
            EXERCISE_IMAGE,
            createdAt
        );

        Exercise exercise2 = new Exercise(
            EXERCISE_ID_2,
            DIFFERENT_NAME,
            DIFFERENT_DESCRIPTION,
            DIFFERENT_TYPE,
            DIFFERENT_IMAGE,
            createdAt
        );

        ExerciseDTO dto1 = new ExerciseDTO(
            EXERCISE_ID,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            EXERCISE_TYPE,
            EXERCISE_IMAGE
        );

        ExerciseDTO dto2 = new ExerciseDTO(
            EXERCISE_ID_2,
            DIFFERENT_NAME,
            DIFFERENT_DESCRIPTION,
            DIFFERENT_TYPE,
            DIFFERENT_IMAGE
        );

        List<Exercise> exercises = Arrays.asList(exercise1, exercise2);
        Page<Exercise> exercisePage = Page.of(exercises, pageable, (long) exercises.size());

        when(exerciseRepository.findAll(pageable)).thenReturn(exercisePage);
        when(exerciseMapper.toDto(exercise1)).thenReturn(dto1);
        when(exerciseMapper.toDto(exercise2)).thenReturn(dto2);

        // When
        Page<ExerciseDTO> result = exerciseService.getAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(dto1, dto2);
        assertThat(result.getPageNumber()).isEqualTo(0);

        verify(exerciseRepository).findAll(pageable);
    }

    @Test
    void testGetAllEmpty() {
        // Given
        Pageable pageable = Pageable.from(0, PAGE_SIZE);
        Page<Exercise> emptyPage = Page.of(List.of(), pageable, 0L);

        when(exerciseRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<ExerciseDTO> result = exerciseService.getAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalSize()).isEqualTo(0);

        verify(exerciseRepository).findAll(pageable);
    }
}
