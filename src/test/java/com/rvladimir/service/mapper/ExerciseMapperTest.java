package com.rvladimir.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.service.dto.ExerciseDTO;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;


/**
 * Test class for the ExerciseMapper.
 */
public class ExerciseMapperTest {

    private static final String EXERCISE_NAME = "Push-ups";
    private static final String EXERCISE_DESCRIPTION = "Exercise to use arms";
    private static final String EXERCISE_IMAGE = "pushups.png";
    private static final long EXERCISE_ID = 1L;

    private ExerciseMapper exerciseMapper;

    @BeforeEach
    void setUp() {
        exerciseMapper = new ExerciseMapper();
    }

    @Test
    void testToEntityFromExerciseDTO() {
        //Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(
            null,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            Exercise.Type.STRENGTH,
            EXERCISE_IMAGE
        );

        //When
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);

        //then
        assertThat(exercise).isNotNull();
        assertThat(exercise.getId()).isNull();
        assertThat(exercise.getName()).isEqualTo(EXERCISE_NAME);
        assertThat(exercise.getDescription()).isEqualTo(EXERCISE_DESCRIPTION);
        assertThat(exercise.getType()).isEqualTo(Exercise.Type.STRENGTH);
        assertThat(exercise.getImage()).isEqualTo(EXERCISE_IMAGE);
    }

    @Test
    void testToEntityFromExerciseDTOReturnsNullWhenInputIsNull() {
        // When
        Exercise exercise = exerciseMapper.toEntity(null);

        // Then
        assertThat(exercise).isNull();
    }

    @Test
    void testToDto() {
        // Given
        Exercise exercise = new Exercise();
        exercise.setId(EXERCISE_ID);
        exercise.setName(EXERCISE_NAME);
        exercise.setDescription(EXERCISE_DESCRIPTION);
        exercise.setType(Exercise.Type.STRENGTH);
        exercise.setImage(EXERCISE_IMAGE);

        // When
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // Then
        assertThat(exerciseDTO).isNotNull();
        assertThat(exerciseDTO.getId()).isEqualTo(EXERCISE_ID);
        assertThat(exerciseDTO.getName()).isEqualTo(EXERCISE_NAME);
        assertThat(exerciseDTO.getDescription()).isEqualTo(EXERCISE_DESCRIPTION);
        assertThat(exerciseDTO.getType()).isEqualTo(Exercise.Type.STRENGTH);
        assertThat(exerciseDTO.getImage()).isEqualTo(EXERCISE_IMAGE);
    }

    @Test
    void testToDtoReturnsNullWhenInputIsNull() {
        // When
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(null);

        // Then
        assertThat(exerciseDTO).isNull();
    }

    @Test
    void testToDtoWithDifferentType() {
        // Given
        Exercise exercise = new Exercise();
        exercise.setId(EXERCISE_ID);
        exercise.setName(EXERCISE_NAME);
        exercise.setDescription(EXERCISE_DESCRIPTION);
        exercise.setType(Exercise.Type.CARDIO);
        exercise.setImage(EXERCISE_IMAGE);

        // When
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // Then
        assertThat(exerciseDTO).isNotNull();
        assertThat(exerciseDTO.getId()).isEqualTo(EXERCISE_ID);
        assertThat(exerciseDTO.getName()).isEqualTo(EXERCISE_NAME);
        assertThat(exerciseDTO.getDescription()).isEqualTo(EXERCISE_DESCRIPTION);
        assertThat(exerciseDTO.getType()).isEqualTo(Exercise.Type.CARDIO);
        assertThat(exerciseDTO.getImage()).isEqualTo(EXERCISE_IMAGE);
    }

    @ParameterizedTest
    @EnumSource(Exercise.Type.class)
    void testToEntityFromExerciseDTOWithAllTypes(Exercise.Type type) {
        // Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(
            null,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            type,
            EXERCISE_IMAGE
        );

        // When
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);

        // Then
        assertThat(exercise).isNotNull();
        assertThat(exercise.getType()).isEqualTo(type);
    }

    @ParameterizedTest
    @EnumSource(Exercise.Type.class)
    void testToDtoWithAllTypes(Exercise.Type type) {
        // Given
        Exercise exercise = new Exercise();
        exercise.setId(EXERCISE_ID);
        exercise.setName(EXERCISE_NAME);
        exercise.setDescription(EXERCISE_DESCRIPTION);
        exercise.setType(type);
        exercise.setImage(EXERCISE_IMAGE);
        exercise.setCreatedAt(LocalDateTime.now());

        // When
        ExerciseDTO exerciseDTO = exerciseMapper.toDto(exercise);

        // Then
        assertThat(exerciseDTO).isNotNull();
        assertThat(exerciseDTO.getType()).isEqualTo(type);
    }

    @Test
    void testToEntityFromExerciseDTOSetsCreatedAt() {
        // Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(
            null,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            Exercise.Type.STRENGTH,
            EXERCISE_IMAGE
        );

        // When
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);

        // Then
        assertThat(exercise.getCreatedAt()).isNotNull();
        assertThat(exercise.getCreatedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void testToEntityFromExerciseDTOWithEmptyStrings() {
        // Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(
            null,
            "",
            "",
            Exercise.Type.STRENGTH,
            ""
        );

        // When
        Exercise exercise = exerciseMapper.toEntity(exerciseDTO);

        // Then
        assertThat(exercise).isNotNull();
        assertThat(exercise.getName()).isEmpty();
        assertThat(exercise.getDescription()).isEmpty();
        assertThat(exercise.getImage()).isEmpty();
    }

    @Test
    void testToEntityFromExerciseDTODoesNotMutateInput() {
        // Given
        ExerciseDTO exerciseDTO = new ExerciseDTO(
            null,
            EXERCISE_NAME,
            EXERCISE_DESCRIPTION,
            Exercise.Type.STRENGTH,
            EXERCISE_IMAGE
        );
        ExerciseDTO copy = new ExerciseDTO(
            exerciseDTO.getId(),
            exerciseDTO.getName(),
            exerciseDTO.getDescription(),
            exerciseDTO.getType(),
            exerciseDTO.getImage()
        );

        // When
        exerciseMapper.toEntity(exerciseDTO);

        // Then
        assertThat(exerciseDTO).usingRecursiveComparison().isEqualTo(copy);
    }

    @Test
    void testToDtoDoesNotMutateInput() {
        // Given
        Exercise exercise = new Exercise();
        exercise.setId(EXERCISE_ID);
        exercise.setName(EXERCISE_NAME);
        exercise.setDescription(EXERCISE_DESCRIPTION);
        exercise.setType(Exercise.Type.STRENGTH);
        exercise.setImage(EXERCISE_IMAGE);
        exercise.setCreatedAt(LocalDateTime.now());
        Exercise copy = new Exercise();
        copy.setId(exercise.getId());
        copy.setName(exercise.getName());
        copy.setDescription(exercise.getDescription());
        copy.setType(exercise.getType());
        copy.setImage(exercise.getImage());
        copy.setCreatedAt(exercise.getCreatedAt());

        // When
        exerciseMapper.toDto(exercise);

        // Then
        assertThat(exercise).usingRecursiveComparison().isEqualTo(copy);
    }
}
