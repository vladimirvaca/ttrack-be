package com.rvladimir.service.impl;

import com.rvladimir.domain.Exercise;
import com.rvladimir.domain.SessionExercise;
import com.rvladimir.domain.TrainingSession;
import com.rvladimir.repository.ExerciseRepository;
import com.rvladimir.repository.SessionExerciseRepository;
import com.rvladimir.repository.TrainingSessionRepository;
import com.rvladimir.service.SessionExerciseService;
import com.rvladimir.service.dto.CreateSessionExerciseDTO;
import com.rvladimir.service.dto.SessionExerciseDTO;
import com.rvladimir.service.mapper.SessionExerciseMapper;

import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

/**
 * Implementation of SessionExerciseService.
 */
@Singleton
@AllArgsConstructor
public class SessionExerciseServiceImpl implements SessionExerciseService {

    private final SessionExerciseRepository sessionExerciseRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final SessionExerciseMapper sessionExerciseMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SessionExerciseDTO createSessionExercise(Long trainingSessionId, CreateSessionExerciseDTO dto) {
        Optional<Exercise> exerciseOpt = exerciseRepository.findById(dto.getExerciseId());
        Optional<TrainingSession> trainingSessionOpt = trainingSessionRepository.findById(trainingSessionId);
        if (exerciseOpt.isEmpty() || trainingSessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Exercise or TrainingSession not found");
        }
        SessionExercise entity = sessionExerciseMapper.toEntity(dto, exerciseOpt.get(), trainingSessionOpt.get());
        SessionExercise saved = sessionExerciseRepository.save(entity);
        return sessionExerciseMapper.toDto(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SessionExerciseDTO> getSessionExercisesByTrainingSession(Long trainingSessionId) {
        List<SessionExercise> entities = sessionExerciseRepository.findByTrainingSessionId(trainingSessionId);
        return entities.stream()
                .map(sessionExerciseMapper::toDto)
                .collect(Collectors.toList());
    }
}
