package com.rvladimir.service.mapper;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.domain.TypeOfExercise;
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
        if (dto.getStatus() != null) {
            entity.setStatus(SessionExercise.Status.valueOf(dto.getStatus()));
        }
        entity.setExerciseOrder(dto.getExerciseOrder());
        entity.setExercise(exercise);
        entity.setTrainingSession(trainingSession);
        entity.setCreatedAt(LocalDateTime.now());
        if (dto.getUnitOfMeasurement() != null) {
            entity.setUnitOfMeasurement(SessionExercise.UnitOfMeasurement.valueOf(dto.getUnitOfMeasurement()));
        }
        if (dto.getTypeOfExercise() != null) {
            entity.setTypeOfExercise(TypeOfExercise.valueOf(dto.getTypeOfExercise()));
        }
        entity.setNotes(dto.getNotes());
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
        String status = null;
        if (entity.getStatus() != null) {
            status = entity.getStatus().name();
        }
        String typeOfExercise = null;
        if (entity.getTypeOfExercise() != null) {
            typeOfExercise = entity.getTypeOfExercise().name();
        }
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
            status,
            entity.getExerciseOrder(),
            exerciseId,
            trainingSessionId,
            entity.getCreatedAt(),
            unitOfMeasurement,
            typeOfExercise,
            entity.getNotes()
        );
    }
}
