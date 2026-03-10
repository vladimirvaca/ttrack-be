package com.rvladimir.service.mapper;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import jakarta.inject.Singleton;

import java.time.LocalDateTime;

/**
 * Mapper for SessionExercise and its DTOs.
 */
@Singleton
public class SessionExerciseMapper {

    /**
     * Maps CreateSessionExerciseDTO to SessionExercise entity.
     * The {@code createdAt} field is set server-side to the current timestamp.
     */
    public SessionExercise toEntity(CreateSessionExerciseDTO dto, Exercise exercise, TrainingSession trainingSession) {
        SessionExercise entity = new SessionExercise();
        entity.setRounds(dto.getRounds());
        entity.setSets(dto.getSets());
        entity.setRepetitions(dto.getRepetitions());
        entity.setSprints(dto.getSprints());
        entity.setDuration(dto.getDuration());
        entity.setWeight(dto.getWeight());
        entity.setDistance(dto.getDistance());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRestTime(dto.getRestTime());
        entity.setStatus(dto.getStatus());
        entity.setExerciseOrder(dto.getExerciseOrder());
        entity.setExercise(exercise);
        entity.setTrainingSession(trainingSession);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUnitOfMeasurement(dto.getUnitOfMeasurement());
        entity.setTypeOfExercise(dto.getTypeOfExercise());
        entity.setNotes(dto.getNotes());
        return entity;
    }

    /**
     * Maps SessionExercise entity to SessionExerciseDTO.
     */
    public SessionExerciseDTO toDto(SessionExercise entity) {
        Long exerciseId = null;
        if (entity.getExercise() != null) {
            exerciseId = entity.getExercise().getId();
        }
        Long trainingSessionId = null;
        if (entity.getTrainingSession() != null) {
            trainingSessionId = entity.getTrainingSession().getId();
        }
        return new SessionExerciseDTO(
            entity.getId(),
            entity.getRounds(),
            entity.getSets(),
            entity.getRepetitions(),
            entity.getSprints(),
            entity.getDuration(),
            entity.getWeight(),
            entity.getDistance(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getRestTime(),
            entity.getStatus(),
            entity.getExerciseOrder(),
            exerciseId,
            trainingSessionId,
            entity.getCreatedAt(),
            entity.getUnitOfMeasurement(),
            entity.getTypeOfExercise(),
            entity.getNotes()
        );
    }
}
