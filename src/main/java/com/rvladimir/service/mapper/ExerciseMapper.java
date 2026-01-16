package com.rvladimir.service.mapper;

import com.rvladimir.domain.Exercise;
import com.rvladimir.service.dto.ExerciseDTO;

import jakarta.inject.Singleton;

import java.time.LocalDateTime;

@Singleton
public class ExerciseMapper {

    /**
     * Convert ExerciseDTO to Exercise entity
     * @param exerciseDTO the DTO to convert
     * @return the Exercise entity
     */
    public Exercise toEntity(ExerciseDTO exerciseDTO) {
        if (exerciseDTO == null) {
            return null;
        }
        Exercise exercise = new Exercise();
        exercise.setId(null);
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setType(exerciseDTO.getType());
        exercise.setImage(exerciseDTO.getImage());
        exercise.setCreatedAt(LocalDateTime.now());
        return exercise;
    }

    /**
     * Convert Exercise entity to ExerciseDTO
     * @param exercise the entity to convert
     * @return the ExerciseDTO
     */
    public ExerciseDTO toDto(Exercise exercise) {
        if (exercise == null) {
            return null;
        }
        return new ExerciseDTO(
            exercise.getId(),
            exercise.getName(),
            exercise.getDescription(),
            exercise.getType(),
            exercise.getImage()
        );
    }
}
