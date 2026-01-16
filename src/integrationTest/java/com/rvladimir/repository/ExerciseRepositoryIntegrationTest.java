package com.rvladimir.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.Exercise.Type;
import com.rvladimir.test.PostgresTestContainer;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.test.support.TestPropertyProvider;

import jakarta.inject.Inject;

import java.time.LocalDateTime;
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
class ExerciseRepositoryIntegrationTest implements TestPropertyProvider {

    private static final String TEST_NAME = "Push Up";
    private static final String TEST_DESCRIPTION = "A basic upper body exercise";
    private static final Type TEST_TYPE = Type.STRENGTH;
    private static final String TEST_IMAGE = "pushup.png";
    private static final String TEST_NAME_2 = "Squat";
    private static final String TEST_DESCRIPTION_2 = "A basic lower body exercise";
    private static final Type TEST_TYPE_2 = Type.BALANCE;
    private static final String TEST_IMAGE_2 = "squat.png";
    private static final long NON_EXISTING_ID = 999L;

    @Container
    static PostgreSQLContainer<?> postgres = PostgresTestContainer.getInstance();

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
        exerciseRepository.deleteAll();
    }

    @Test
    void testSaveExercise() {
        // Given
        Exercise exercise = new Exercise(
            null,
            TEST_NAME,
            TEST_DESCRIPTION,
            TEST_TYPE,
            TEST_IMAGE,
            LocalDateTime.now()
        );

        // When
        Exercise savedExercise = exerciseRepository.save(exercise);

        // Then
        assertThat(savedExercise.getId()).isNotNull();
        assertThat(savedExercise.getName()).isEqualTo(TEST_NAME);
        assertThat(savedExercise.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(savedExercise.getType()).isEqualTo(TEST_TYPE);
        assertThat(savedExercise.getImage()).isEqualTo(TEST_IMAGE);
        assertThat(savedExercise.getCreatedAt()).isNotNull();
    }

    @Test
    void testFindById() {
        // Given
        Exercise exercise = new Exercise(
            null,
            TEST_NAME,
            TEST_DESCRIPTION,
            TEST_TYPE,
            TEST_IMAGE,
            LocalDateTime.now()
        );
        Exercise savedExercise = exerciseRepository.save(exercise);

        // When
        Optional<Exercise> foundExercise = exerciseRepository.findById(savedExercise.getId());

        // Then
        assertThat(foundExercise).isPresent();
        assertThat(foundExercise.get().getName()).isEqualTo(TEST_NAME);
    }

    @Test
    void testFindByIdNotFound() {
        // When
        Optional<Exercise> foundExercise = exerciseRepository.findById(NON_EXISTING_ID);

        // Then
        assertThat(foundExercise).isEmpty();
    }

    @Test
    void testSaveMultipleExercises() {
        // Given
        Exercise exercise1 = new Exercise(
            null,
            TEST_NAME,
            TEST_DESCRIPTION,
            TEST_TYPE,
            TEST_IMAGE,
            LocalDateTime.now()
        );
        Exercise exercise2 = new Exercise(
            null,
            TEST_NAME_2,
            TEST_DESCRIPTION_2,
            TEST_TYPE_2,
            TEST_IMAGE_2,
            LocalDateTime.now()
        );

        // When
        exerciseRepository.save(exercise1);
        exerciseRepository.save(exercise2);

        // Then
        long count = exerciseRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    void testUpdateExercise() {
        // Given
        Exercise exercise = new Exercise(
            null,
            TEST_NAME,
            TEST_DESCRIPTION,
            TEST_TYPE,
            TEST_IMAGE,
            LocalDateTime.now()
        );
        Exercise savedExercise = exerciseRepository.save(exercise);

        // When
        savedExercise.setName(TEST_NAME_2);
        savedExercise.setDescription(TEST_DESCRIPTION_2);
        savedExercise.setType(TEST_TYPE_2);
        savedExercise.setImage(TEST_IMAGE_2);
        Exercise updatedExercise = exerciseRepository.update(savedExercise);

        // Then
        assertThat(updatedExercise.getName()).isEqualTo(TEST_NAME_2);
        assertThat(updatedExercise.getDescription()).isEqualTo(TEST_DESCRIPTION_2);
        assertThat(updatedExercise.getType()).isEqualTo(TEST_TYPE_2);
        assertThat(updatedExercise.getImage()).isEqualTo(TEST_IMAGE_2);

        Optional<Exercise> retrievedExercise = exerciseRepository.findById(savedExercise.getId());
        assertThat(retrievedExercise).isPresent();
        assertThat(retrievedExercise.get().getName()).isEqualTo(TEST_NAME_2);
    }

    @Test
    void testDeleteExercise() {
        // Given
        Exercise exercise = new Exercise(
            null,
            TEST_NAME,
            TEST_DESCRIPTION,
            TEST_TYPE,
            TEST_IMAGE,
            LocalDateTime.now()
        );
        Exercise savedExercise = exerciseRepository.save(exercise);
        Long exerciseId = savedExercise.getId();

        // When
        exerciseRepository.deleteById(exerciseId);

        // Then
        Optional<Exercise> foundExercise = exerciseRepository.findById(exerciseId);
        assertThat(foundExercise).isEmpty();
    }
}
