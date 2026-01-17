package com.rvladimir.service.mapper;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;

import jakarta.inject.Singleton;

/**
 * Mapper for SessionExercise and its DTOs.
 */
@Singleton
public class SessionExerciseMapper {
    /**
     * Maps CreateSessionExerciseDTO to SessionExercise entity.
     */
    public SessionExercise toEntity(CreateSessionExerciseDTO dto, Exercise exercise, TrainingSession trainingSession) {
        SessionExercise entity = new SessionExercise();
        entity.setRounds(dto.getRounds());
        entity.setSets(dto.getSets());
        entity.setRepetitions(dto.getRepetitions());
        entity.setSprints(dto.getSprints());
        entity.setTime(dto.getTime());
        entity.setWeight(dto.getWeight());
        entity.setDistance(dto.getDistance());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setRestTime(dto.getRestTime());
        entity.setStatus(SessionExercise.Status.valueOf(dto.getStatus()));
        entity.setExerciseOrder(dto.getExerciseOrder());
        entity.setExercise(exercise);
        entity.setTrainingSession(trainingSession);
        entity.setCreatedAt(dto.getCreatedAt());
        if (dto.getUnitOfMeasurement() != null) {
            entity.setUnitOfMeasurement(SessionExercise.UnitOfMeasurement.valueOf(dto.getUnitOfMeasurement()));
        }
        return entity;
    }

    /**
     * Maps SessionExercise entity to SessionExerciseDTO.
     */
    public SessionExerciseDTO toDto(SessionExercise entity) {
        String unitOfMeasurement = null;
        if (entity.getUnitOfMeasurement() != null) {
            unitOfMeasurement = entity.getUnitOfMeasurement().name();
        }
        return new SessionExerciseDTO(
            entity.getId(),
            entity.getRounds(),
            entity.getSets(),
            entity.getRepetitions(),
            entity.getSprints(),
            entity.getTime(),
            entity.getWeight(),
            entity.getDistance(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getRestTime(),
            entity.getStatus().name(),
            entity.getExerciseOrder(),
            entity.getExercise().getId(),
            entity.getTrainingSession().getId(),
            entity.getCreatedAt(),
            unitOfMeasurement
        );
    }
}
